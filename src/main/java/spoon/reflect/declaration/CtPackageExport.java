/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;

import java.util.List;

/**
 * Represents an exported or opened package in a Java module
 *
 * The exports directive specifies the name of a package to be exported by the current module.
 * For code in other modules, this grants access at compile time and run time to the public and protected types in the package,
 * and the public and protected members of those types. It also grants reflective access to those types and members for code in other modules.
 *
 * The opens directive specifies the name of a package to be opened by the current module.
 * For code in other modules, this grants access at run time, but not compile time, to the public and protected types in the package,
 * and the public and protected members of those types. It also grants reflective access to all types in the package, and all their members, for code in other modules.
 *
 * It is permitted for opens to specify a package which is not declared by a compilation unit associated with the current module.
 * (If the package should happen to be declared by an observable compilation unit associated with another module, the opens directive has no effect on that other module.)
 *
 * If an exports or opens directive has a to clause, then the directive is qualified; otherwise, it is unqualified.
 * For a qualified directive, the public and protected types in the package, and their public and protected members, are accessible solely to code in the modules specified in the to clause.
 * The modules specified in the to clause are referred to as friends of the current module. For an unqualified directive, these types and their members are accessible to code in any module.
 * It is permitted for the to clause of an exports or opens directive to specify a module which is not observable (§7.7.6).
 *
 * Examples:
 * <pre>
 *     exports com.example.foo.internal to com.example.foo.probe;
 *     opens com.example.foo.quux;
 * </pre>
 */
public interface CtPackageExport extends CtModuleDirective {

	@PropertySetter(role = CtRole.OPENED_PACKAGE)
	<T extends CtPackageExport> T setOpenedPackage(boolean openedPackage);

	@PropertyGetter(role = CtRole.OPENED_PACKAGE)
	boolean isOpenedPackage();

	@PropertyGetter(role = CtRole.PACKAGE_REF)
	CtPackageReference getPackageReference();

	@PropertySetter(role = CtRole.PACKAGE_REF)
	<T extends CtPackageExport> T setPackageReference(CtPackageReference packageReference);

	@PropertyGetter(role = CtRole.MODULE_REF)
	List<CtModuleReference> getTargetExport();

	@PropertySetter(role = CtRole.MODULE_REF)
	<T extends CtPackageExport> T setTargetExport(List<CtModuleReference> targetExport);

	@PropertySetter(role = CtRole.MODULE_REF)
	<T extends CtPackageExport> T addTargetExport(CtModuleReference targetExport);

	@Override
	CtPackageExport clone();
}
