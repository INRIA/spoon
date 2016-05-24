/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.generating;

import spoon.generating.equals.EqualsVisitorScanner;
import spoon.processing.AbstractManualProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtBiScanner;
import spoon.reflect.visitor.ReferenceFilter;

import java.util.List;

import static spoon.generating.equals.EqualsVisitorScanner.GENERATING_EQUALS;
import static spoon.generating.equals.EqualsVisitorScanner.TARGET_EQUALS_PACKAGE;

public class EqualsVisitorGenerator extends AbstractManualProcessor {
	@Override
	public void process() {
		new EqualsVisitorScanner(createEqualsVisitor()).scan(getFactory().Class().get(CtBiScanner.class));
	}

	private CtClass<Object> createEqualsVisitor() {
		final CtPackage aPackage = getFactory().Package().getOrCreate(TARGET_EQUALS_PACKAGE);
		final CtClass<Object> target = getFactory().Class().get(GENERATING_EQUALS);
		target.addModifier(ModifierKind.PUBLIC);
		aPackage.addType(target);
		final List<CtTypeReference> references = target.getReferences(new ReferenceFilter<CtTypeReference>() {
			@Override
			public boolean matches(CtTypeReference reference) {
				return reference != null && GENERATING_EQUALS.equals(reference.getQualifiedName());
			}

			@Override
			public Class<CtTypeReference> getType() {
				return CtTypeReference.class;
			}
		});
		for (CtTypeReference reference : references) {
			reference.setPackage(aPackage.getReference());
		}
		return target;
	}
}
