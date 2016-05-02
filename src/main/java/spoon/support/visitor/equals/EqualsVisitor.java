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
package spoon.support.visitor.equals;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtBiScanner;

public class EqualsVisitor extends CtBiScanner {
	public static boolean equals(CtElement element, CtElement other) {
		return !new EqualsVisitor().biScan(element, other);
	}

	@Override
	public boolean biScan(CtElement element, CtElement other) {
		if (isNotEqual) {
			return isNotEqual;
		}
		if (element == null) {
			if (other != null) {
				return fail();
			}
			return isNotEqual;
		} else if (other == null) {
			return fail();
		}
		if (element == other) {
			return isNotEqual;
		}

		try {
			String leftName = ((CtNamedElement) element).getSimpleName();
			String rightName = ((CtNamedElement) other).getSimpleName();
			if (!leftName.equals(rightName)) {
				return fail();
			}
		} catch (ClassCastException ignored) {
		}
		try {
			String leftName = ((CtReference) element).getSimpleName();
			String rightName = ((CtReference) other).getSimpleName();
			if (!leftName.equals(rightName)) {
				return fail();
			}
		} catch (ClassCastException ignored) {
		}
		try {
			String leftLabel = ((CtStatement) element).getLabel();
			String rightLabel = ((CtStatement) other).getLabel();
			if (leftLabel == null && rightLabel == null) {
				return super.biScan(element, other);
			}
			if (leftLabel == null || !leftLabel.equals(rightLabel)) {
				return fail();
			}
		} catch (ClassCastException ignored) {
		}

		return super.biScan(element, other);
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		final CtOperatorAssignment peek = (CtOperatorAssignment) this.stack.peek();
		if (!assignment.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtOperatorAssignment(assignment);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
		final CtBinaryOperator peek = (CtBinaryOperator) this.stack.peek();
		if (!e.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtBinaryOperator(e);
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
		final CtUnaryOperator peek = (CtUnaryOperator) this.stack.peek();
		if (!e.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtUnaryOperator(e);
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> e) {
		final CtArrayTypeReference peek = (CtArrayTypeReference) this.stack.peek();
		if (e.getDimensionCount() != peek.getDimensionCount()) {
			isNotEqual = true;
			return;
		}
		super.visitCtArrayTypeReference(e);
	}

	@Override
	public void visitCtBreak(CtBreak e) {
		final CtBreak peek = (CtBreak) this.stack.peek();
		if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			isNotEqual = true;
			return;
		}
		super.visitCtBreak(e);
	}

	@Override
	public void visitCtContinue(CtContinue e) {
		final CtContinue peek = (CtContinue) this.stack.peek();
		if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			isNotEqual = true;
			return;
		}
		super.visitCtContinue(e);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> e) {
		final CtExecutableReference peek = (CtExecutableReference) this.stack.peek();
		if (e.isConstructor() != peek.isConstructor()) {
			isNotEqual = true;
			return;
		}
		super.visitCtExecutableReference(e);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> e) {
		final CtMethod peek = (CtMethod) this.stack.peek();
		if (e.isDefaultMethod() != peek.isDefaultMethod()) {
			isNotEqual = true;
			return;
		}
		super.visitCtMethod(e);
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> e) {
		final CtParameter peek = (CtParameter) this.stack.peek();
		if (e.isVarArgs() != peek.isVarArgs()) {
			isNotEqual = true;
			return;
		}
		super.visitCtParameter(e);
	}
}
