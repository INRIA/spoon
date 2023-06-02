/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Created by urli on 08/08/2017.
 */
class JDTImportBuilder {

	private final CompilationUnitDeclaration declarationUnit;
	private CompilationUnit spoonUnit;
	private ICompilationUnit sourceUnit;
	private Factory factory;
	private Set<CtImport> imports;

	JDTImportBuilder(CompilationUnitDeclaration declarationUnit,  Factory factory) {
		this.declarationUnit = declarationUnit;
		this.factory = factory;
		this.sourceUnit = declarationUnit.compilationResult.compilationUnit;
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

					// load package by looking up in the class loader or in the model being built
					CtPackage ctPackage = loadPackage(packageName);

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
				// A static import can be either a static field, a static method or a static type
				// It is possible that this method will add duplicate imports
				// Logically, if `foo` is a static method and `foo` is also a static field, then both should be
				// imported with `import static example.Foo.foo;` repeated twice.
				int lastDot = importName.lastIndexOf('.');
				String className = importName.substring(0, lastDot);
				String methodOrFieldOrTypeName = importName.substring(lastDot + 1);

				CtType<?> klass = this.getOrLoadClass(className);
				if (klass != null) {
					if (Objects.equals(methodOrFieldOrTypeName, "*")) {
						this.imports.add(createImportWithPosition(factory.Type().createTypeMemberWildcardImportReference(klass.getReference()), importRef));
					} else {
						CtNamedElement methodOrFieldOrType;

						methodOrFieldOrType = klass.getField(methodOrFieldOrTypeName);
						if (methodOrFieldOrType != null) {
							this.imports.add(createImportWithPosition(methodOrFieldOrType.getReference(), importRef));
						}

						List<CtMethod<?>> methods = klass.getMethodsByName(methodOrFieldOrTypeName);
						if (methods.size() > 0) {
							methodOrFieldOrType = methods.get(0);
							this.imports.add(createImportWithPosition(methodOrFieldOrType.getReference(), importRef));
						}

						methodOrFieldOrType = klass.getNestedType(methodOrFieldOrTypeName);
						if (methodOrFieldOrType != null) {
							this.imports.add(createImportWithPosition(methodOrFieldOrType.getReference(), importRef));
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

	private CtPackage loadPackage(String packageName) {
		// get all packages known for the current class loader and the ones which are accessible from it
		Package[] allPackagesInAllClassLoaders = Package.getPackages();

		Optional<Package> requiredPackage = Arrays.stream(allPackagesInAllClassLoaders)
				.filter(pkg -> pkg.getName().equals(packageName))
				.findAny();
		if (requiredPackage.isPresent()) {
			CtPackage ctPackage = factory.createPackage();
			ctPackage.setSimpleName(requiredPackage.get().getName());
			return ctPackage;
		}

		// get package by traversing the model
		return factory.Package().get(packageName);
	}

	private CtType getOrLoadClass(String className) {
		CtType klass = this.factory.Type().get(className);

		if (klass == null) {
			klass = this.factory.Interface().get(className);

			if (klass == null) {
				try {
					Class<?> zeClass = loadClass(className);
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

	private Class<?> loadClass(String className) throws ClassNotFoundException {
		Class<?> zeClass;
		if (this.factory.getEnvironment().getInputClassLoader() != null) {
			zeClass = this.factory.getEnvironment().getInputClassLoader().loadClass(className);
		} else {
			zeClass = this.getClass().getClassLoader().loadClass(className);
		}
		return zeClass;
	}
}
