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
package spoon.generating.equals;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;
import spoon.support.visitor.equals.IgnoredByEquals;

public class EqualsVisitorScanner extends CtScanner {
	public static final String TARGET_EQUALS_PACKAGE = "spoon.support.visitor.equals";
	public static final String GENERATING_EQUALS_PACKAGE = "spoon.generating.equals";
	public static final String GENERATING_EQUALS = GENERATING_EQUALS_PACKAGE + ".EqualsVisitor";
	private CtClass<Object> target;

	public EqualsVisitorScanner(CtClass<Object> target) {
		this.target = target;
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> element) {
		if (!element.getSimpleName().startsWith("visitCt")) {
			return;
		}

		Factory factory = element.getFactory();
		CtMethod<T> clone = factory.Core().clone(element);

		final CtAnnotation<?> ignoredAnnotation = factory.Core().createAnnotation();
		ignoredAnnotation.setAnnotationType(factory.Type().createReference(IgnoredByEquals.class));

		for (int i = 2; i < clone.getBody().getStatements().size() - 1; i++) {
			final CtInvocation targetInvocation = (CtInvocation) ((CtInvocation) clone.getBody().getStatement(i)).getArguments().get(0);
			if (targetInvocation.getExecutable().getExecutableDeclaration().getAnnotations().contains(ignoredAnnotation)) {
				clone.getBody().getStatement(i--).delete();
				continue;
			}
			CtInvocation replace = (CtInvocation) factory.Core().clone(clone.getBody().getStatement(i));
			clone.getBody().getStatement(i).replace(replace);
		}

		target.addMethod(clone);
	}
}
