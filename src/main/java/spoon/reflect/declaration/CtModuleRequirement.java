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
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;

import java.util.Set;

/**
 * Represents a require module in a Java module definition
 * See: https://docs.oracle.com/javase/specs/jls/se9/html/jls-7.html#jls-7.7
 *
 * Example:
 * <pre>
 *     requires transitive com.example.foo.network;
 * </pre>
 */
public interface CtModuleRequirement extends CtModuleMember {

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
