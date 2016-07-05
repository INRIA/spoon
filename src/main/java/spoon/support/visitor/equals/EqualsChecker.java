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
package spoon.support.visitor.equals;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;

public class EqualsChecker extends CtInheritanceScanner {
	private CtElement other;
	private boolean isNotEqual;

	public void setOther(CtElement other) {
		this.other = other;
	}

	public boolean isNotEqual() {
		return isNotEqual;
	}

	@Override
	public void scanCtNamedElement(CtNamedElement e) {
		final CtNamedElement peek = (CtNamedElement) this.other;
		if (!e.getSimpleName().equals(peek.getSimpleName())) {
			isNotEqual = true;
			return;
		}
		super.scanCtNamedElement(e);
	}

	@Override
	public void scanCtReference(CtReference reference) {
		final CtReference peek = (CtReference) this.other;
		if (!reference.getSimpleName().equals(peek.getSimpleName())) {
			isNotEqual = true;
			return;
		}
		super.scanCtReference(reference);
	}

	@Override
	public void scanCtStatement(CtStatement s) {
		final CtStatement peek = (CtStatement) this.other;
		final String leftLabel = s.getLabel();
		final String rightLabel = peek.getLabel();
		if (leftLabel == null && rightLabel == null) {
			super.scanCtStatement(s);
			return;
		}
		if (leftLabel == null || !leftLabel.equals(rightLabel)) {
			isNotEqual = true;
			return;
		}
		super.scanCtStatement(s);
	}

	@Override
	public void scanCtModifiable(CtModifiable m) {
		final CtModifiable peek = (CtModifiable) this.other;
		if (m.getVisibility() == null) {
			if (peek.getVisibility() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getVisibility() == null) {
			isNotEqual = true;
			return;
		} else  if (!m.getVisibility().equals(peek.getVisibility())) {
			isNotEqual = true;
			return;
		}
		if (m.getModifiers().size() != peek.getModifiers().size()) {
			isNotEqual = true;
			return;
		}
		if (!m.getModifiers().containsAll(peek.getModifiers())) {
			isNotEqual = true;
			return;
		}
		super.scanCtModifiable(m);
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		final CtOperatorAssignment peek = (CtOperatorAssignment) this.other;
		if (assignment.getKind() == null) {
			if (peek.getKind() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getKind() == null) {
			isNotEqual = true;
			return;
		} else if (!assignment.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtOperatorAssignment(assignment);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
		final CtBinaryOperator peek = (CtBinaryOperator) this.other;
		if (e.getKind() == null) {
			if (peek.getKind() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getKind() == null) {
			isNotEqual = true;
			return;
		} else if (!e.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtBinaryOperator(e);
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
		final CtUnaryOperator peek = (CtUnaryOperator) this.other;
		if (e.getKind() == null) {
			if (peek.getKind() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getKind() == null) {
			isNotEqual = true;
			return;
		} else if (!e.getKind().equals(peek.getKind())) {
			isNotEqual = true;
			return;
		}
		super.visitCtUnaryOperator(e);
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> e) {
		final CtArrayTypeReference peek = (CtArrayTypeReference) this.other;
		if (e.getDimensionCount() != peek.getDimensionCount()) {
			isNotEqual = true;
			return;
		}
		super.visitCtArrayTypeReference(e);
	}

	@Override
	public void visitCtBreak(CtBreak e) {
		final CtBreak peek = (CtBreak) this.other;
		if (e.getTargetLabel() == null) {
			if (peek.getTargetLabel() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getTargetLabel() == null) {
			isNotEqual = true;
			return;
		} else if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			isNotEqual = true;
			return;
		}
		super.visitCtBreak(e);
	}

	@Override
	public void visitCtContinue(CtContinue e) {
		final CtContinue peek = (CtContinue) this.other;
		if (e.getTargetLabel() == null) {
			if (peek.getTargetLabel() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getTargetLabel() == null) {
			isNotEqual = true;
			return;
		} else if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			isNotEqual = true;
			return;
		}
		super.visitCtContinue(e);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> e) {
		final CtExecutableReference peek = (CtExecutableReference) this.other;
		if (e.isConstructor() != peek.isConstructor()) {
			isNotEqual = true;
			return;
		}
		super.visitCtExecutableReference(e);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> e) {
		final CtMethod peek = (CtMethod) this.other;
		if (e.isDefaultMethod() != peek.isDefaultMethod()) {
			isNotEqual = true;
			return;
		}
		super.visitCtMethod(e);
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> e) {
		final CtParameter peek = (CtParameter) this.other;
		if (e.isVarArgs() != peek.isVarArgs()) {
			isNotEqual = true;
			return;
		}
		super.visitCtParameter(e);
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> e) {
		final CtLiteral peek = (CtLiteral) this.other;
		if (e.getValue() == null) {
			if (peek.getValue() != null) {
				isNotEqual = true;
				return;
			}
		} else if (peek.getValue() == null) {
			isNotEqual = true;
			return;
		} else if (!e.getValue().equals(peek.getValue())) {
			isNotEqual = true;
			return;
		}
		super.visitCtLiteral(e);
	}
}
