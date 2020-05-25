/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.equals;

import spoon.reflect.code.CtAssignment;
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
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;

public class EqualsChecker extends CtInheritanceScanner {
	protected CtElement other;
	private boolean isNotEqual;
	private CtRole notEqualRole;

	public void setOther(CtElement other) {
		this.other = other;
		isNotEqual = false;
	}

	public boolean isNotEqual() {
		return isNotEqual;
	}

	public CtRole getNotEqualRole() {
		return notEqualRole;
	}

	/**
	 * @param role the role of the not equal attribute, or null if there is no such role
	 */
	protected void setNotEqual(CtRole role) {
		notEqualRole = role;
		isNotEqual = true;
		throw NotEqualException.INSTANCE;
	}

	@Override
	public void scanCtNamedElement(CtNamedElement e) {
		final CtNamedElement peek = (CtNamedElement) this.other;
		if (!e.getSimpleName().equals(peek.getSimpleName())) {
			setNotEqual(CtRole.NAME);
		}
		super.scanCtNamedElement(e);
	}

	@Override
	public void scanCtReference(CtReference reference) {
		final CtReference peek = (CtReference) this.other;
		if (!reference.getSimpleName().equals(peek.getSimpleName())) {
			setNotEqual(CtRole.NAME);
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
			setNotEqual(CtRole.LABEL);
		}
		super.scanCtStatement(s);
	}

	@Override
	public void scanCtModifiable(CtModifiable m) {
		final CtModifiable peek = (CtModifiable) this.other;
		if (m.getVisibility() == null) {
			if (peek.getVisibility() != null) {
				setNotEqual(CtRole.MODIFIER);
			}
		} else if (peek.getVisibility() == null) {
			setNotEqual(CtRole.MODIFIER);
		} else  if (!m.getVisibility().equals(peek.getVisibility())) {
			setNotEqual(CtRole.MODIFIER);
		}
		if (m.getModifiers().size() != peek.getModifiers().size()) {
			setNotEqual(CtRole.MODIFIER);
		}
		if (!m.getModifiers().containsAll(peek.getModifiers())) {
			setNotEqual(CtRole.MODIFIER);
		}
		super.scanCtModifiable(m);
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignment) {
		if (!(assignment instanceof CtOperatorAssignment) && this.other instanceof CtOperatorAssignment) {
			setNotEqual(null);
		}
		super.visitCtAssignment(assignment);
	}

	@Override
	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		final CtOperatorAssignment peek = (CtOperatorAssignment) this.other;
		if (assignment.getKind() == null) {
			if (peek.getKind() != null) {
				setNotEqual(CtRole.OPERATOR_KIND);
			}
		} else if (peek.getKind() == null) {
			setNotEqual(CtRole.OPERATOR_KIND);
		} else if (!assignment.getKind().equals(peek.getKind())) {
			setNotEqual(CtRole.OPERATOR_KIND);
		}
		super.visitCtOperatorAssignment(assignment);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
		final CtBinaryOperator peek = (CtBinaryOperator) this.other;
		if (e.getKind() == null) {
			if (peek.getKind() != null) {
				setNotEqual(CtRole.OPERATOR_KIND);
			}
		} else if (peek.getKind() == null) {
			setNotEqual(CtRole.OPERATOR_KIND);
		} else if (!e.getKind().equals(peek.getKind())) {
			setNotEqual(CtRole.OPERATOR_KIND);
		}
		super.visitCtBinaryOperator(e);
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> e) {
		final CtUnaryOperator peek = (CtUnaryOperator) this.other;
		if (e.getKind() == null) {
			if (peek.getKind() != null) {
				setNotEqual(CtRole.OPERATOR_KIND);
			}
		} else if (peek.getKind() == null) {
			setNotEqual(CtRole.OPERATOR_KIND);
		} else if (!e.getKind().equals(peek.getKind())) {
			setNotEqual(CtRole.OPERATOR_KIND);
		}
		super.visitCtUnaryOperator(e);
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> e) {
		final CtArrayTypeReference peek = (CtArrayTypeReference) this.other;
		if (e.getDimensionCount() != peek.getDimensionCount()) {
			setNotEqual(CtRole.TYPE);
		}
		super.visitCtArrayTypeReference(e);
	}

	@Override
	public void visitCtBreak(CtBreak e) {
		final CtBreak peek = (CtBreak) this.other;
		if (e.getTargetLabel() == null) {
			if (peek.getTargetLabel() != null) {
				setNotEqual(CtRole.TARGET_LABEL);
			}
		} else if (peek.getTargetLabel() == null) {
			setNotEqual(CtRole.TARGET_LABEL);
		} else if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			setNotEqual(CtRole.TARGET_LABEL);
		}
		super.visitCtBreak(e);
	}

	@Override
	public void visitCtContinue(CtContinue e) {
		final CtContinue peek = (CtContinue) this.other;
		if (e.getTargetLabel() == null) {
			if (peek.getTargetLabel() != null) {
				setNotEqual(CtRole.TARGET_LABEL);
			}
		} else if (peek.getTargetLabel() == null) {
			setNotEqual(CtRole.TARGET_LABEL);
		} else if (!e.getTargetLabel().equals(peek.getTargetLabel())) {
			setNotEqual(CtRole.TARGET_LABEL);
		}
		super.visitCtContinue(e);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> e) {
		final CtExecutableReference peek = (CtExecutableReference) this.other;
		if (e.isConstructor() != peek.isConstructor()) {
			setNotEqual(null);
		}
		super.visitCtExecutableReference(e);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> e) {
		final CtMethod peek = (CtMethod) this.other;
		if (e.isDefaultMethod() != peek.isDefaultMethod()) {
			setNotEqual(CtRole.MODIFIER);
		}
		super.visitCtMethod(e);
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> e) {
		final CtParameter peek = (CtParameter) this.other;
		if (e.isVarArgs() != peek.isVarArgs()) {
			setNotEqual(CtRole.MODIFIER);
		}
		super.visitCtParameter(e);
	}

	@Override
	public <T> void visitCtLiteral(CtLiteral<T> e) {
		final CtLiteral peek = (CtLiteral) this.other;
		if (e.getValue() == null) {
			if (peek.getValue() != null) {
				setNotEqual(CtRole.VALUE);
			}
		} else if (peek.getValue() == null) {
			setNotEqual(CtRole.VALUE);
		} else if (!e.getValue().equals(peek.getValue())) {
			setNotEqual(CtRole.VALUE);
		}
		super.visitCtLiteral(e);
	}

	@Override
	public void visitCtImport(CtImport ctImport) {
		final CtImport peek = (CtImport) this.other;

		if (ctImport.getImportKind() == null) {
			if (peek.getImportKind() != null) {
				setNotEqual(null);
			}
		} else if (peek.getImportKind() == null) {
			setNotEqual(null);
		} else if (!ctImport.getImportKind().equals(peek.getImportKind())) {
			setNotEqual(null);
		}

		super.visitCtImport(ctImport);
	}

}
