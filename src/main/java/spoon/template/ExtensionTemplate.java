/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

import java.util.ArrayList;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtTypeReference;

/**
 * Inserts all the methods, fields, constructors, initialization blocks (if
 * target is a class), inner types, and super interfaces (except
 * {@link Template}) from a given template by substituting all the template
 * parameters by their values. Members annotated with
 * {@link spoon.template.Local} or {@link Parameter} are not inserted.
 */
public class ExtensionTemplate extends AbstractTemplate<CtType<?>> {
	@Override
	public CtType<?> apply(CtType<?> target) {
		CtClass<? extends Template<?>> templateType = Substitution.getTemplateCtClass(target.getFactory(), this);
		CtType<?> generated = TemplateBuilder.createPattern(templateType, templateType, this)
		.setAddGeneratedBy(isAddGeneratedBy())
		.substituteSingle(target, CtType.class);
		for (CtTypeReference<?> iface : new ArrayList<>(generated.getSuperInterfaces())) {
			iface.delete();
			target.addSuperInterface(iface);
		}
		for (CtTypeMember tm : new ArrayList<>(generated.getTypeMembers())) {
			tm.delete();
			target.addTypeMember(tm);
		}
		return target;
	}
}
