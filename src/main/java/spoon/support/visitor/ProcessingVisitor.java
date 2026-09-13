/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor;

import java.util.ArrayDeque;
import java.util.Deque;

import spoon.processing.Processor;
import spoon.processing.TraversalStrategy;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtScanner;

/**
 * This visitor implements the code processing engine.
 */
public class ProcessingVisitor extends CtScanner {

	Factory factory;

	Processor<?> processor;

	/**
	 * The constructor.
	 */
	public ProcessingVisitor(Factory factory) {
		this.factory = factory;
	}

	private boolean canBeProcessed(CtElement e) {
		if (!factory.getEnvironment().isProcessingStopped()
				&& processor.getProcessedElementTypes() != null) {
			for (Object o : processor.getProcessedElementTypes()) {
				if (!((Class<?>) o).isAssignableFrom(e.getClass())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public Processor<?> getProcessor() {
		return processor;
	}

	/**
	 * Applies the processing to the given element. To apply the processing,
	 * this method calls, for all the registered processor in, the
	 * {@link Processor#process(CtElement)} method if
	 * {@link Processor#isToBeProcessed(CtElement)} returns true.
	 */
	@Override
	public void scan(CtElement e) {
		if (e == null) {
			return;
		}
		if (getClass() == ProcessingVisitor.class && e instanceof CtBinaryOperator<?> binaryOperator) {
			scanBinaryOperator(binaryOperator);
			return;
		}
		processIfRequested(e, TraversalStrategy.PRE_ORDER);
		super.scan(e);
		processIfRequested(e, TraversalStrategy.POST_ORDER);
	}

	@SuppressWarnings("unchecked")
	private void processIfRequested(CtElement element, TraversalStrategy strategy) {
		Processor<CtElement> p = (Processor<CtElement>) processor;
		if (p.getTraversalStrategy() == strategy && canBeProcessed(element) && p.isToBeProcessed(element)) {
			p.process(element);
		}
	}

	private void scanBinaryOperator(CtBinaryOperator<?> root) {
		Deque<BinaryFrame> frames = new ArrayDeque<>();
		frames.push(new BinaryFrame(root));
		while (!frames.isEmpty()) {
			BinaryFrame frame = frames.peek();
			switch (frame.nextStage()) {
			case 0 -> {
				processIfRequested(frame.operator, TraversalStrategy.PRE_ORDER);
				enter(frame.operator);
				scan(CtRole.ANNOTATION, frame.operator.getAnnotations());
				scan(CtRole.TYPE, frame.operator.getType());
				scan(CtRole.CAST, frame.operator.getTypeCasts());
			}
			case 1 -> scanBinaryOperand(frames, CtRole.LEFT_OPERAND, frame.operator.getLeftHandOperand());
			case 2 -> scanBinaryOperand(frames, CtRole.RIGHT_OPERAND, frame.operator.getRightHandOperand());
			case 3 -> scan(CtRole.COMMENT, frame.operator.getComments());
			default -> {
				exit(frame.operator);
				processIfRequested(frame.operator, TraversalStrategy.POST_ORDER);
				frames.pop();
				}
			}
		}
	}

	private void scanBinaryOperand(Deque<BinaryFrame> frames, CtRole role, CtElement operand) {
		if (operand instanceof CtBinaryOperator<?> binaryOperator) {
			frames.push(new BinaryFrame(binaryOperator));
		} else {
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

	public void setProcessor(Processor<?> processor) {
		this.processor = processor;
	}
}
