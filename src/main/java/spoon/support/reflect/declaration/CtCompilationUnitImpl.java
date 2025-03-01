/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.cu.position.PartialSourcePositionImpl;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.util.ModelList;
import spoon.reflect.ModelElementContainerDefaultCapacities;

/**
 * Implements a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
public class CtCompilationUnitImpl extends CtElementImpl implements CtCompilationUnit {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.DECLARED_TYPE_REF)
	private final ModelList<CtTypeReference<?>> declaredTypeReferences = new ModelList<CtTypeReference<?>>() {
		@Override
		protected CtElement getOwner() {
			return CtCompilationUnitImpl.this;
		}

		@Override
		protected CtRole getRole() {
			return CtRole.DECLARED_TYPE_REF;
		}

		@Override
		protected int getDefaultCapacity() {
			return ModelElementContainerDefaultCapacities.COMPILATION_UNIT_DECLARED_TYPES_CONTAINER_DEFAULT_CAPACITY;
		}
	};

	@MetamodelPropertyField(role = CtRole.PACKAGE_DECLARATION)
	private CtPackageDeclaration packageDeclaration;

	@MetamodelPropertyField(role = CtRole.DECLARED_IMPORT)
	private final ModelList<CtImport> imports = new ModelList<CtImport>() {
		private static final long serialVersionUID = 1L;
		@Override
		protected CtElement getOwner() {
			return CtCompilationUnitImpl.this;
		}

		@Override
		protected CtRole getRole() {
			return CtRole.DECLARED_IMPORT;
		}

		@Override
		protected int getDefaultCapacity() {
			return ModelElementContainerDefaultCapacities.COMPILATION_UNIT_IMPORTS_CONTAINER_DEFAULT_CAPACITY;
		}
	};

	@MetamodelPropertyField(role = CtRole.DECLARED_MODULE_REF)
	private CtModuleReference moduleReference;

	private File file;

	/**
	 * The index of line breaks, as computed by JDT.
	 * Used to compute line numbers afterwards.
	 */
	private int[] lineSeparatorPositions;

	private ElementSourceFragment rootFragment;

	private String originalSourceCode;

	private PartialSourcePositionImpl myPartialSourcePosition;

	@Override
	public UNIT_TYPE getUnitType() {
		// we try to guess based on the file name
		if (file != null) {
			if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_MODULE_DECLARATION)) {
				return UNIT_TYPE.MODULE_DECLARATION;
			} else if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION)) {
				return UNIT_TYPE.PACKAGE_DECLARATION;
			} else {
				return UNIT_TYPE.TYPE_DECLARATION;
			}
		// else we just check if there is a declared type
		} else {
			if (getDeclaredTypes().isEmpty()) {
				if (getDeclaredModuleReference() != null) {
					return UNIT_TYPE.MODULE_DECLARATION;
				} else if (packageDeclaration != null) {
					return UNIT_TYPE.PACKAGE_DECLARATION;
				} else {
					return UNIT_TYPE.UNKNOWN;
				}
			} else {
				return UNIT_TYPE.TYPE_DECLARATION;
			}
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public CtType<?> getMainType() {
		if (getFile() == null || getDeclaredTypes().size() == 1) {
			return getDeclaredTypes().get(0);
		}
		for (CtType<?> t : getDeclaredTypes()) {
			String name = getFile().getName();
			name = name.substring(0, name.lastIndexOf('.'));
			if (t.getSimpleName().equals(name)) {
				return t;
			}
		}
		throw new RuntimeException(
				"inconsistent compilation unit: '"
						+ file
						+ "': declared types are "
						+ getDeclaredTypes());
	}

	@Override
	public List<CtType<?>> getDeclaredTypes() {
		return Collections.unmodifiableList(declaredTypeReferences.stream().map(ref -> ref.getTypeDeclaration()).collect(Collectors.toList()));
	}

	@Override
	public List<CtTypeReference<?>> getDeclaredTypeReferences() {
		return declaredTypeReferences;
	}

	@Override
	public CtCompilationUnitImpl setDeclaredTypeReferences(List<CtTypeReference<?>> types) {
		this.declaredTypeReferences.set(types);
		return this;
	}

	@Override
	@DerivedProperty
	public CtCompilationUnit setDeclaredTypes(List<CtType<?>> types) {
		return setDeclaredTypeReferences(types.stream().map(CtType::getReference).collect(Collectors.toList()));
	}

	@Override
	@DerivedProperty
	public CtCompilationUnitImpl addDeclaredType(CtType<?> type) {
		if (type != null) {
			addDeclaredTypeReference(type.getReference());
		}
		return this;
	}

	@Override
	public CtCompilationUnitImpl addDeclaredTypeReference(CtTypeReference<?> type) {
		this.declaredTypeReferences.add(type);
		return this;
	}

	@Override
	@DerivedProperty
	public CtModule getDeclaredModule() {
		return this.moduleReference != null ? this.moduleReference.getDeclaration() : null;
	}

	@Override
	public CtModuleReference getDeclaredModuleReference() {
		return moduleReference;
	}

	@Override
	public CtCompilationUnitImpl setDeclaredModule(CtModule module) {
		setDeclaredModuleReference(module == null ? null : module.getReference());
		return this;
	}

	@Override
	public CtCompilationUnitImpl setDeclaredModuleReference(CtModuleReference module) {
		//Do not set compilation unit as parent of module
		if (module != null) {
			module.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.DECLARED_MODULE_REF, module, this.moduleReference);
		this.moduleReference = module;
		return this;
	}

	@Override
	@DerivedProperty
	public CtPackage getDeclaredPackage() {
		if (packageDeclaration != null) {
			return packageDeclaration.getReference().getDeclaration();
		}
		if (declaredTypeReferences.size() > 0) {
			return declaredTypeReferences.get(0).getPackage().getDeclaration();
		}
		return getFactory().getModel().getRootPackage();
	}

	@Override
	public CtPackageDeclaration getPackageDeclaration() {
		if (packageDeclaration == null) {
			CtPackageReference packRef;
			if (declaredTypeReferences.size() > 0) {
				packRef = declaredTypeReferences.get(0).getPackage().clone();
			} else {
				packRef = getFactory().getModel().getRootPackage().getReference();
			}
			packageDeclaration = getFactory().Package().createPackageDeclaration(packRef);
		}
		return packageDeclaration;
	}

	@Override
	public CtCompilationUnitImpl setDeclaredPackage(CtPackage ctPackage) {
		setPackageDeclaration(ctPackage == null ? null : getFactory().Package().createPackageDeclaration(ctPackage.getReference()));
		return this;
	}

	@Override
	public CtCompilationUnitImpl setPackageDeclaration(CtPackageDeclaration packageDeclaration) {
		if (packageDeclaration != null) {
			packageDeclaration.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.PACKAGE_DECLARATION, packageDeclaration, this.packageDeclaration);
		this.packageDeclaration = packageDeclaration;
		return (CtCompilationUnitImpl) this;
	}

	@Override
	public CtCompilationUnitImpl setFile(File file) {
		this.file = file;
		//reset cached position (if any)
		this.position = SourcePosition.NOPOSITION;
		return this;
	}

	@Override
	public List<File> getBinaryFiles() {
		final List<File> binaries = new ArrayList<>();
		final String output = getFactory()
				.getEnvironment()
				.getBinaryOutputDirectory();
		if (output != null) {
			final File base = Paths
					.get(output, getDeclaredPackage()
					.getQualifiedName()
					.replace(".", File.separator))
					.toFile();
			if (base.isDirectory()) {
				for (final CtType type : getDeclaredTypes()) {
					// Add main type, for instance, 'Foo.class'.
					final String nameOfType = type.getSimpleName();
					final File fileOfType = new File(
							base, nameOfType + ".class");
					if (fileOfType.isFile()) {
						binaries.add(fileOfType);
					}
					// Add inner/anonymous types, for instance,
					// 'Foo$Bar.class'. Use 'getElements()' rather than
					// 'getNestedTypes()' to also fetch inner types of inner
					// types of inner types ... and so on.
					for (final CtType inner : type.getElements(
							new TypeFilter<>(CtType.class))) {
						// 'getElements' does not only return inner types but
						// also returns 'type' itself. Thus, we need to ensure
						// to not add 'type' twice.
						if (!inner.equals(type)) {
							final String nameOfInner =
									nameOfType + "$" + inner.getSimpleName();
							final File fileOfInnerType = new File(
									base, nameOfInner + ".class");
							if (fileOfInnerType.isFile()) {
								binaries.add(fileOfInnerType);
							}
						}
					}
				}
			}
		}
		return binaries;
	}


	@Override
	public String getOriginalSourceCode() {

		if (originalSourceCode == null && getFile() != null && getFile().exists()) {
			try {
				originalSourceCode = Files.readString(
					getFile().toPath(),
					this.getFactory().getEnvironment().getEncoding()
				);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return originalSourceCode;
	}


	@Override
	public ModelList<CtImport> getImports() {
		return this.imports;
	}

	@Override
	public CtCompilationUnitImpl setImports(Collection<CtImport> imports) {
		this.imports.set(imports);
		return this;
	}

	@Override
	public ElementSourceFragment getOriginalSourceFragment() {
		if (rootFragment == null) {
			if (moduleReference != null) {
				throw new SpoonException("Root source fragment of compilation unit of module is not supported");
			}
			if (declaredTypeReferences.isEmpty()) {
				throw new SpoonException("Root source fragment of compilation unit of package is not supported");
			}
			rootFragment = ElementSourceFragment.createSourceFragmentsFrom(this);
		}
		return rootFragment;
	}

	@Override
	public int[] getLineSeparatorPositions() {
		return lineSeparatorPositions;
	}

	@Override
	public CtCompilationUnitImpl setLineSeparatorPositions(int[] lineSeparatorPositions) {
		this.lineSeparatorPositions = lineSeparatorPositions;
		return this;
	}

	@Override
	public SourcePosition getPosition() {
		if (position == SourcePosition.NOPOSITION) {
			String sourceCode = getOriginalSourceCode();
			if (sourceCode != null) {
				position = getFactory().Core().createSourcePosition((CompilationUnit) this, 0, sourceCode.length() - 1, getLineSeparatorPositions());
			} else {
				//it is a virtual compilation unit (e.g. for Snippet)
				position = getFactory().Core().createSourcePosition((CompilationUnit) this, 0, Integer.MAX_VALUE - 1, getLineSeparatorPositions());
			}
		}
		return position;
	}

	@Override
	@UnsettableProperty
	public <E extends CtElement> E setPosition(SourcePosition position) {
		return (E) this;
	}

	/**
	 * @return a {@link SourcePosition} which points to this {@link CompilationUnit}. It always returns same value to safe memory.
	 */
	public SourcePosition getOrCreatePartialSourcePosition() {
		if (myPartialSourcePosition == null) {
			myPartialSourcePosition = new PartialSourcePositionImpl((CompilationUnit) this);
		}
		return myPartialSourcePosition;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCompilationUnit(this);
	}

	@Override
	public CtCompilationUnitImpl clone() {
		return (CtCompilationUnitImpl) super.clone();
	}

	@Override
	public CtElement getParent() throws ParentNotInitializedException {
		return null;
	}

	@Override
	@UnsettableProperty
	public <E extends CtElement> E setParent(CtElement parent) {
		return (E) this;
	}

	@Override
	public String toString() {
		if (this.file != null) {
			return this.file.getName();
		}
		return "CompilationUnit<unknown file>";
	}
}
