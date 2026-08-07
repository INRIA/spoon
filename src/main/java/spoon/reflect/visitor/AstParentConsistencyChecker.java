/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.ArrayDeque;
import java.util.Deque;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;

public class AstParentConsistencyChecker extends CtScanner {

	private CtElement parent;

	@Override
	public void scan(CtElement element) {
		// We allow to share references across the AST
		// that is a leaf reference can be the same
		// at different places
		if (element == null || element instanceof CtReference) {
			return;
		}
		checkParent(element, parent);
		CtElement parent = this.parent;
		this.parent = element;
		try {
			super.scan(element);
		} finally {
			this.parent = parent;
		}
	}

	/**
	 * @param operator binary operator to visit
	 * @param <T> type of the expression produced by the operator
	 */
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		if (getClass() != AstParentConsistencyChecker.class) {
			super.visitCtBinaryOperator(operator);
			return;
		}

		CtElement initialParent = parent;
		Deque<BinaryFrame> frames = new ArrayDeque<>();
		frames.push(new BinaryFrame(operator));
		try {
			while (!frames.isEmpty()) {
				BinaryFrame frame = frames.peek();
				switch (frame.nextStage()) {
				case 0 -> {
					parent = frame.operator;
					enter(frame.operator);
					scan(CtRole.ANNOTATION, frame.operator.getAnnotations());
					scan(CtRole.TYPE, frame.operator.getType());
					scan(CtRole.CAST, frame.operator.getTypeCasts());
				}
				case 1 -> scanBinaryOperand(
						frames, CtRole.LEFT_OPERAND, frame.operator.getLeftHandOperand(), frame.operator);
				case 2 -> scanBinaryOperand(
						frames, CtRole.RIGHT_OPERAND, frame.operator.getRightHandOperand(), frame.operator);
				case 3 -> scan(CtRole.COMMENT, frame.operator.getComments());
				default -> {
					exit(frame.operator);
					frames.pop();
					parent = frames.isEmpty() ? operator : frames.peek().operator;
				}
				}
			}
		} finally {
			parent = initialParent;
		}
	}

	private void scanBinaryOperand(
			Deque<BinaryFrame> frames, CtRole role, CtElement operand, CtBinaryOperator<?> parentOperator) {
		if (operand instanceof CtBinaryOperator<?> binaryOperator) {
			checkParent(binaryOperator, parentOperator);
			frames.push(new BinaryFrame(binaryOperator));
		} else {
			parent = parentOperator;
			scan(role, operand);
		}
	}

	private static final class BinaryFrame {
		private final CtBinaryOperator<?> operator;
		private int stage;

		private BinaryFrame(CtBinaryOperator<?> operator) {
			this.operator = operator;
		}

		private int nextStage() {
			int currentStage = stage;
			stage = currentStage + 1;
			return currentStage;
		}
	}

	private static void checkParent(CtElement element, CtElement expectedParent) {
		if (expectedParent != null
				&& element.isParentInitialized() // this is the fix of #1747
				&& element.getParent() != expectedParent) {
			throw new IllegalStateException(toDebugString(element) // better debug
					+ " is set as child of\n" + toDebugString(element.getParent())
					+ "however it is visited as a child of\n" + toDebugString(expectedParent));
		}
	}

	private static String toDebugString(CtElement e) {
		String elementDescription = e.getClass().getSimpleName();
		if (e instanceof CtBinaryOperator<?> binaryOperator) {
			elementDescription = binaryOperator.getKind() + " binary operator";
		} else if (e instanceof CtNamedElement namedElement) {
			elementDescription += " " + namedElement.getSimpleName();
		}
		return "Element: " + elementDescription + "\nClass: " + e.getClass() + "\nposition: " + e.getPosition() + "\n";
	}
}
