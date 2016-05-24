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
package spoon.generating.scanner;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

public class CtBiScannerScanner extends CtScanner {
	public static final String TARGET_BISCANNER_PACKAGE = "spoon.reflect.visitor";
	public static final String GENERATING_BISCANNER_PACKAGE = "spoon.generating.scanner";
	public static final String GENERATING_BISCANNER = GENERATING_BISCANNER_PACKAGE + ".CtBiScanner";
	private CtClass<Object> target;
	private CtLocalVariable<?> peekTemplate;

	public CtBiScannerScanner(CtClass<Object> target, CtLocalVariable<?> peekTemplate) {
		this.target = target;
		this.peekTemplate = peekTemplate;
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> element) {
		if (!element.getSimpleName().startsWith("visitCt")) {
			return;
		}

		Factory factory = element.getFactory();
		CtMethod<T> clone = factory.Core().clone(element);

		// Peek element from Stack.
		final CtLocalVariable<?> peek = factory.Core().clone(peekTemplate);
		final CtTypeReference type = factory.Core().clone(clone.getParameters().get(0).getType());
		type.getActualTypeArguments().clear();
		peek.getDefaultExpression().addTypeCast(type);
		peek.setType(type);
		clone.getBody().insertBegin(peek);

		for (int i = 2; i < clone.getBody().getStatements().size() - 1; i++) {
			final CtInvocation targetInvocation = (CtInvocation) ((CtInvocation) clone.getBody().getStatement(i)).getArguments().get(0);
			CtInvocation replace = (CtInvocation) factory.Core().clone(clone.getBody().getStatement(i));

			// Changes to biScan method.
			replace.getExecutable().setSimpleName("biScan");

			// Creates other inv.
			final CtVariableAccess<?> otherRead = factory.Code().createVariableRead(peek.getReference(), false);
			replace.addArgument(factory.Code().createInvocation(otherRead, ((CtInvocation) replace.getArguments().get(0)).getExecutable()));

			if ("Map".equals(targetInvocation.getExecutable().getType().getSimpleName())) {
				((CtExpression) replace.getArguments().get(0)).replace(factory.Code().createInvocation(targetInvocation, factory.Executable().createReference("List Map#values()")));
				replace.getArguments().add(1, factory.Code().createInvocation((CtExpression) replace.getArguments().get(1), factory.Executable().createReference("List Map#values()")));
				replace.getArguments().remove(2);
			}

			clone.getBody().getStatement(i).replace(replace);
		}

		target.addMethod(clone);
	}
}
