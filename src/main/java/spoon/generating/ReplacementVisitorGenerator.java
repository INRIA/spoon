/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.generating.replace.ReplaceScanner;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.ReferenceFilter;

import java.util.List;

import static spoon.generating.replace.ReplaceScanner.GENERATING_REPLACE_VISITOR;
import static spoon.generating.replace.ReplaceScanner.TARGET_REPLACE_PACKAGE;

public class ReplacementVisitorGenerator extends AbstractProcessor<CtType<?>> {
	@Override
	public boolean isToBeProcessed(CtType<?> candidate) {
		return CtScanner.class.getName().equals(candidate.getQualifiedName()) && super.isToBeProcessed(candidate);
	}

	@Override
	public void process(CtType<?> element) {
		new ReplaceScanner(createReplacementVisitor()).scan(getFactory().Class().get(CtScanner.class));
	}

	private CtClass<Object> createReplacementVisitor() {
		final CtPackage aPackage = getFactory().Package().getOrCreate(TARGET_REPLACE_PACKAGE);
		final CtClass<Object> target = getFactory().Class().get(GENERATING_REPLACE_VISITOR);
		target.addModifier(ModifierKind.PUBLIC);
		aPackage.addType(target);
		final List<CtTypeReference> references = target.getReferences(new ReferenceFilter<CtTypeReference>() {
			@Override
			public boolean matches(CtTypeReference reference) {
				return reference != null && GENERATING_REPLACE_VISITOR.equals(reference.getQualifiedName());
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
