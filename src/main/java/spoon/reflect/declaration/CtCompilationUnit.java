/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import java.io.File;
import java.util.Collection;
import java.util.List;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.Experimental;
import spoon.support.UnsettableProperty;
import spoon.support.util.ModelList;

/**
 * Defines a compilation unit. In Java, a compilation unit can contain only one
 * public type declaration and other secondary types declarations (not public).
 */
@Experimental
public interface CtCompilationUnit extends CtElement {
	enum UNIT_TYPE {
		TYPE_DECLARATION,
		PACKAGE_DECLARATION,
		MODULE_DECLARATION,
		UNKNOWN
	}

	/**
	 * Returns the declaration type of the compilation unit.
	 */
	UNIT_TYPE getUnitType();
	/**
	 * Gets the file that corresponds to this compilation unit if any (contains
	 * the source code).
	 */
	File getFile();

	/**
	 * Sets the file that corresponds to this compilation unit.
	 */
	CtCompilationUnit setFile(File file);

	/**
	 * @return array of offsets in the origin source file, where occurs line separator
	 */
	int[] getLineSeparatorPositions();

	/**
	 * @param lineSeparatorPositions array of offsets in the origin source file, where occurs line separator
	 */
	CtCompilationUnit setLineSeparatorPositions(int[] lineSeparatorPositions);
	/**
	 * Gets all binary (.class) files that corresponds to this compilation unit
	 * and have been created by calling
	 * {@link spoon.SpoonModelBuilder#compile(spoon.SpoonModelBuilder.InputType...)}.
	 */
	List<File> getBinaryFiles();

	/**
	 * Gets all the types declared in this compilation unit.
	 */
	@DerivedProperty
	@PropertyGetter(role = CtRole.DECLARED_TYPE)
	List<CtType<?>> getDeclaredTypes();

	/**
	 * Gets references to all the types declared in this compilation unit.
	 */
	@PropertyGetter(role = CtRole.DECLARED_TYPE_REF)
	List<CtTypeReference<?>> getDeclaredTypeReferences();

	/**
	 * Sets the references to types declared in this compilation unit.
	 */
	@PropertySetter(role = CtRole.DECLARED_TYPE_REF)
	CtCompilationUnit setDeclaredTypeReferences(List<CtTypeReference<?>> types);

	/**
	 * Sets the types declared in this compilation unit.
	 * It is here for backward compatibility.
	 * It calls internally {@link #setDeclaredTypeReferences(List)}
	 * so the {@link CtCompilationUnit} contains type reference only.
	 * It doesn't contain whole type, which belongs to it's CtPackage in primary `java concept` model.
	 * Note that {@link CtCompilationUnit} represents a secondary model related to mapping of java modules, packages and types to file system.
	 */
	@DerivedProperty
	CtCompilationUnit setDeclaredTypes(List<CtType<?>> types);

	/**
	 * Add a type to the list of declared types.
	 * It is here for backward compatibility.
	 * It calls internally {@link #addDeclaredTypeReference(CtTypeReference)}
	 * so the {@link CtCompilationUnit} contains type reference only.
	 * It doesn't contain whole type, which belongs to it's CtPackage in primary `java concept` model.
	 * Note that {@link CtCompilationUnit} represents a secondary model related to mapping of java modules, packages and types to file system.
	 */
	@DerivedProperty
	CtCompilationUnit addDeclaredType(CtType<?> type);

	/**
	 * Add a type reference to the list of declared types
	 */
	@PropertySetter(role = CtRole.DECLARED_TYPE_REF)
	CtCompilationUnit addDeclaredTypeReference(CtTypeReference<?> type);

	/**
	 * Gets the declared module if the compilationUnit is "module-info.java"
	 */
	@DerivedProperty
	@PropertyGetter(role = CtRole.DECLARED_MODULE)
	CtModule getDeclaredModule();

	/**
	 * Gets the declared module reference if the compilationUnit is "module-info.java"
	 */
	@PropertyGetter(role = CtRole.DECLARED_MODULE_REF)
	CtModuleReference getDeclaredModuleReference();

	/**
	 * Sets the declared module if the compilationUnit is "module-info.java"
	 * It is here for backward compatibility.
	 * It internally calls {@link #setDeclaredModuleReference(CtModuleReference)}
	 * It doesn't contain whole CtModule, which belongs to CtModel in primary `java concept` model.
	 * Note that {@link CtCompilationUnit} represents a secondary model related to mapping of java modules, packages and types to file system.
	 */
	@DerivedProperty
	CtCompilationUnit setDeclaredModule(CtModule module);
	/**
	 * Sets the declared module reference if the compilationUnit is "module-info.java"
	 */
	@PropertySetter(role = CtRole.DECLARED_MODULE_REF)
	CtCompilationUnit setDeclaredModuleReference(CtModuleReference module);

	/**
	 * Gets the declared package
	 */
	@DerivedProperty
	CtPackage getDeclaredPackage();

	/**
	 * @return the package declaration
	 */
	@PropertyGetter(role = CtRole.PACKAGE_DECLARATION)
	CtPackageDeclaration getPackageDeclaration();

	/**
	 * Sets the package declaration using the instance of CtPackage.
	 * It is here for backward compatibility.
	 * It calls internally {@link #setPackageDeclaration(CtPackageDeclaration)}
	 * It doesn't contain whole CtPackage, which belongs to it's parent package or to CtModule in primary `java concept` model.
	 * Note that {@link CtCompilationUnit} represents a secondary model related to mapping of java modules, packages and types to file system.
	 */
	@DerivedProperty
	CtCompilationUnit setDeclaredPackage(CtPackage ctPackage);

	/**
	 * Sets the package declaration
	 */
	@PropertySetter(role = CtRole.PACKAGE_DECLARATION)
	CtCompilationUnit setPackageDeclaration(CtPackageDeclaration packageDeclaration);

	/**
	 * Searches and returns the main type (the type which has the same name as
	 * the file).
	 */
	@DerivedProperty
	CtType<?> getMainType();

	/**
	 * Gets the original source code as a string.
	 */
	String getOriginalSourceCode();

	/**
	 * Get the imports computed for this CU.
	 * WARNING: This method is tagged as experimental, as its signature and/or usage might change in future release.
	 * @return All the imports from the original source code
	 */
	@Experimental
	@PropertyGetter(role = CtRole.DECLARED_IMPORT)
	ModelList<CtImport> getImports();

	@Override
	CtCompilationUnit clone();

	/**
	 * Set the imports of this CU
	 * WARNING: This method is tagged as experimental, as its signature and/or usage might change in future release.
	 * @param imports All the imports of the original source code
	 */
	@Experimental
	@PropertySetter(role = CtRole.DECLARED_IMPORT)
	CtCompilationUnit setImports(Collection<CtImport> imports);

	@Override
	@DerivedProperty
	CtElement getParent();

	@Override
	@UnsettableProperty
	<E extends CtElement> E setParent(E parent);

	@Override
	@UnsettableProperty
	<E extends CtElement> E setPosition(SourcePosition position);
}
