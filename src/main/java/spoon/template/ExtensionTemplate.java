/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
