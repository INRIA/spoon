/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.filter.NamedElementFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by urli on 08/08/2017.
 */
class JDTImportBuilder {

	private final CompilationUnitDeclaration declarationUnit;
	private String filePath;
	private CompilationUnit spoonUnit;
	private ICompilationUnit sourceUnit;
	private Factory factory;
	private Set<CtReference> imports;

	JDTImportBuilder(CompilationUnitDeclaration declarationUnit,  Factory factory) {
		this.declarationUnit = declarationUnit;
		this.factory = factory;
		this.sourceUnit = declarationUnit.compilationResult.compilationUnit;
		this.filePath = CharOperation.charToString(sourceUnit.getFileName());
		// get the CU: it has already been built during model building in JDTBasedSpoonCompiler
		this.spoonUnit = factory.CompilationUnit().create(filePath);
		this.imports = new HashSet<>();
	}

	public void build() {
		if (declarationUnit.imports == null || declarationUnit.imports.length == 0) {
			return;
		}

		for (ImportReference importRef : declarationUnit.imports) {
			String importName = importRef.toString();
			if (!importRef.isStatic()) {
				// Starred import are only managed by importing types of the model for now
				// as it can cost a lot to retrieve all classes of a package by reflection
				if (importName.endsWith("*")) {
					int lastDot = importName.lastIndexOf(".");
					String packageName = importName.substring(0, lastDot);

					// only get package from the model by traversing from rootPackage the model
					// it does not use reflection to achieve that
					CtPackage ctPackage = this.factory.Package().get(packageName);

					if (ctPackage != null) {
						for (CtType type : ctPackage.getTypes()) {
							this.imports.add(type.getReference());
						}
					}

				} else {
					CtType klass = this.getOrLoadClass(importName);
					if (klass != null) {
						this.imports.add(klass.getReference());
					}
				}
			} else {
				int lastDot = importName.lastIndexOf(".");
				String className = importName.substring(0, lastDot);
				String methodOrFieldName = importName.substring(lastDot + 1);

				CtType klass = this.getOrLoadClass(className);
				if (klass != null) {

					// for now starred import are treated by importing
					// all static fields and methods
					// or all fields and methods if it concerns an interface
					if (methodOrFieldName.equals("*")) {
						Collection<CtFieldReference<?>> fields = klass.getAllFields();
						Set<CtMethod> methods = klass.getAllMethods();

						for (CtFieldReference fieldReference : fields) {
							if (fieldReference.isStatic() && fieldReference.getFieldDeclaration().hasModifier(ModifierKind.PUBLIC) || klass.isInterface()) {
								this.imports.add(fieldReference.clone());
							}
						}

						for (CtMethod method : methods) {
							if (method.hasModifier(ModifierKind.STATIC) && method.hasModifier(ModifierKind.PUBLIC) || klass.isInterface()) {
								this.imports.add(method.getReference());
							}
						}
					} else {
						List<CtNamedElement> methodOrFields = klass.getElements(new NamedElementFilter<>(CtNamedElement.class, methodOrFieldName));

						if (methodOrFields.size() > 0) {
							this.imports.add(methodOrFields.get(0).getReference());
						}
					}
				}
			}
		}

		spoonUnit.setImports(this.imports);
	}

	private CtType getOrLoadClass(String className) {
		CtType klass = this.factory.Class().get(className);

		if (klass == null) {
			klass = this.factory.Interface().get(className);

			if (klass == null) {
				try {
					Class zeClass = this.getClass().getClassLoader().loadClass(className);
					klass = this.factory.Type().get(zeClass);
					return klass;
				} catch (ClassNotFoundException e) {
					return null;
				}
			}
		}
		return klass;
	}
}
