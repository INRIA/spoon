/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;

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
	private Set<CtImport> imports;

	JDTImportBuilder(CompilationUnitDeclaration declarationUnit,  Factory factory) {
		this.declarationUnit = declarationUnit;
		this.factory = factory;
		this.sourceUnit = declarationUnit.compilationResult.compilationUnit;
		this.filePath = CharOperation.charToString(sourceUnit.getFileName());
		// get the CU: it has already been built during model building in JDTBasedSpoonCompiler
		this.spoonUnit = JDTTreeBuilder.getOrCreateCompilationUnit(declarationUnit, factory);
		this.imports = new HashSet<>();
	}

	// package visible method in a package visible class, not in the public API
	void build() {
		// sets the imports of the Spoon compilation unit corresponding to `declarationUnit`

		if (declarationUnit.imports == null || declarationUnit.imports.length == 0) {
			return;
		}

		for (ImportReference importRef : declarationUnit.imports) {
			String importName = importRef.toString();
			if (!importRef.isStatic()) {
				if (importName.endsWith("*")) {
					int lastDot = importName.lastIndexOf('.');
					String packageName = importName.substring(0, lastDot);

					// only get package from the model by traversing from rootPackage the model
					// it does not use reflection to achieve that
					CtPackage ctPackage = this.factory.Package().get(packageName);

					if (ctPackage != null) {
						this.imports.add(createImportWithPosition(ctPackage.getReference(), importRef));
					} else {
						if (factory.getEnvironment().getNoClasspath()) {
							this.imports.add(createUnresolvedImportWithPosition(importName, false, importRef));
						}
					}

				} else {
					CtType klass = this.getOrLoadClass(importName);
					if (klass != null) {
						this.imports.add(createImportWithPosition(klass.getReference(), importRef));
					} else {
						if (factory.getEnvironment().getNoClasspath()) {
							this.imports.add(createUnresolvedImportWithPosition(importName, false, importRef));
						}
					}
				}
			} else {
				int lastDot = importName.lastIndexOf('.');
				String className = importName.substring(0, lastDot);
				String methodOrFieldName = importName.substring(lastDot + 1);

				CtType klass = this.getOrLoadClass(className);
				if (klass != null) {
					if ("*".equals(methodOrFieldName)) {
						this.imports.add(createImportWithPosition(factory.Type().createTypeMemberWildcardImportReference(klass.getReference()), importRef));
					} else {
						CtNamedElement methodOrField = null;

						methodOrField = klass.getField(methodOrFieldName);

						if (methodOrField == null) {
							List<CtMethod> methods = klass.getMethodsByName(methodOrFieldName);
							if (methods.size() > 0) {
								methodOrField = methods.get(0);
							}
						}

						if (methodOrField != null) {
							this.imports.add(createImportWithPosition(methodOrField.getReference(), importRef));
						}
					}
				} else {
					if (factory.getEnvironment().getNoClasspath()) {
						this.imports.add(createUnresolvedImportWithPosition(importName, true, importRef));
					}
				}
			}
		}

		spoonUnit.setImports(this.imports);
	}

	private CtImport createImportWithPosition(CtReference ref, ImportReference importRef) {
		char[] content = sourceUnit.getContents();
		CtImport imprt = factory.Type().createImport(ref);
		//include comment before import
		int declStart = importRef.declarationSourceStart;
		int commentStart = PositionBuilder.findNextNonWhitespace(false, content, declStart, PositionBuilder.findPrevNonWhitespace(content, 0, declStart - 1) + 1);
		imprt.setPosition(factory.Core().createCompoundSourcePosition(spoonUnit, importRef.sourceStart(), importRef.sourceEnd(), commentStart, importRef.declarationEnd, spoonUnit.getLineSeparatorPositions()));
		imprt.getReference().setPosition(factory.Core().createSourcePosition(spoonUnit, importRef.sourceStart(), importRef.sourceEnd(), spoonUnit.getLineSeparatorPositions()));
		return imprt;
	}

	private CtImport createUnresolvedImportWithPosition(String ref, boolean isStatic, ImportReference importRef) {
		char[] content = sourceUnit.getContents();
		CtImport imprt = factory.Type().createUnresolvedImport(ref, isStatic);
		//include comment before import
		int declStart = importRef.declarationSourceStart;
		int commentStart = PositionBuilder.findNextNonWhitespace(false, content, declStart, PositionBuilder.findPrevNonWhitespace(content, 0, declStart - 1) + 1);
		imprt.setPosition(factory.Core().createCompoundSourcePosition(spoonUnit, importRef.sourceStart(), importRef.sourceEnd(), commentStart, importRef.declarationEnd, spoonUnit.getLineSeparatorPositions()));
		return imprt;
	}

	private CtType getOrLoadClass(String className) {
		CtType klass = this.factory.Type().get(className);

		if (klass == null) {
			klass = this.factory.Interface().get(className);

			if (klass == null) {
				try {
					Class zeClass = this.getClass().getClassLoader().loadClass(className);
					klass = this.factory.Type().get(zeClass);
					return klass;
				} catch (NoClassDefFoundError | ClassNotFoundException e) {
					// in some cases we want to import an inner class.
					if (!className.contains(CtType.INNERTTYPE_SEPARATOR) && className.contains(CtPackage.PACKAGE_SEPARATOR)) {
						int lastIndexOfDot = className.lastIndexOf(CtPackage.PACKAGE_SEPARATOR);
						String classNameWithInnerSep = className.substring(0, lastIndexOfDot) + CtType.INNERTTYPE_SEPARATOR + className.substring(lastIndexOfDot + 1);
						return getOrLoadClass(classNameWithInnerSep);
					}
					return null;
				}
			}
		}
		return klass;
	}
}
