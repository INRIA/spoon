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

import java.util.Set;

/**
 * Represents a require module in a Java module definition
 *
 * The requires directive specifies the name of a module on which the current module has a dependence.
 * The requires keyword may be followed by the modifier transitive.
 * This causes any module which requires the current module to have an implicitly declared dependence
 * on the module specified by the requires transitive directive.
 * The requires keyword may be followed by the modifier static.
 * This specifies that the dependence, while mandatory at compile time, is optional at run time.
 *
 * Example:
 * <pre>
 *     requires transitive com.example.foo.network;
 * </pre>
 *
 */
public interface CtModuleRequirement extends CtModuleDirective {

	enum RequiresModifier {
		STATIC, TRANSITIVE;
	}

	@PropertyGetter(role = CtRole.MODIFIER)
	Set<RequiresModifier> getRequiresModifiers();

	@PropertySetter(role = CtRole.MODIFIER)
	<T extends CtModuleRequirement> T setRequiresModifiers(Set<RequiresModifier> requiresModifiers);

	@PropertyGetter(role = CtRole.MODULE_REF)
	CtModuleReference getModuleReference();

	@PropertySetter(role = CtRole.MODULE_REF)
	<T extends CtModuleRequirement> T setModuleReference(CtModuleReference moduleReference);

	@Override
	CtModuleRequirement clone();
}
