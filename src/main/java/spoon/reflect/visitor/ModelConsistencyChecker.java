/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.compiler.Environment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.support.Internal;
import spoon.support.Level;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This scanner checks that a program model is consistent with regards to the
 * parent elements (children must have the right parent). This class can be used
 * to validate that a program transformation does not harm the model integrity,
 * and also to automatically fix it when possible.
 */
public class ModelConsistencyChecker extends CtScanner {

	boolean fixInconsistencies;
	boolean fixNullParents;

	Environment environment;

	Deque<CtElement> stack = new ArrayDeque<>();

	private final List<InconsistentElements> inconsistentElements = new ArrayList<>();

	/**
	 * Creates a new model consistency checker.
	 *
	 * @param environment
	 * 		the environment where to report errors, if null, no errors are reported
	 * @param fixInconsistencies
	 * 		automatically fix the inconsistencies rather than reporting
	 * 		warnings (to report warnings, set this to false)
	 * @param fixNullParents
	 * 		automatically fix the null parents rather than reporting
	 * 		warnings (to report warnings, set this to false)
	 */
	public ModelConsistencyChecker(Environment environment, boolean fixInconsistencies, boolean fixNullParents) {
		this.environment = environment;
		this.fixInconsistencies = fixInconsistencies;
		this.fixNullParents = fixNullParents;
	}

	/**
	 * Lists the inconsistencies in the given element and its children.
	 *
	 * @param ctElement the element to check
	 * @return a list of inconsistencies
	 */
	@Internal
	public static List<InconsistentElements> listInconsistencies(CtElement ctElement) {
		ModelConsistencyChecker checker = new ModelConsistencyChecker(null, false, false);
		checker.scan(ctElement);
		return checker.inconsistentElements();
	}

	/**
	 * Enters an element.
	 */
	@Override
	public void enter(CtElement element) {
		if (!stack.isEmpty() && (!element.isParentInitialized() || element.getParent() != stack.peek())) {
			InconsistentElements inconsistentElements = new InconsistentElements(element, List.copyOf(stack));
			this.inconsistentElements.add(inconsistentElements);

			if ((!element.isParentInitialized() && fixNullParents) || (element.getParent() != stack.peek() && fixInconsistencies)) {
				element.setParent(stack.peek());
			} else if (environment != null) {
				environment.report(null, Level.WARN, inconsistentElements.reason());
				this.dumpStack();
			}
		}
		stack.push(element);
	}

	/**
	 * Exits an element.
	 */
	@Override
	protected void exit(CtElement e) {
		stack.pop();
	}

	/**
	 * Gets the list of elements that are considered inconsistent.
	 * <p>
	 * If {@link #fixInconsistencies} is set to true, this list will
	 * contain all the elements that have been fixed.
	 *
	 * @return the invalid elements
	 */
	private List<InconsistentElements> inconsistentElements() {
		return List.copyOf(inconsistentElements);
	}

	private void dumpStack() {
		environment.debugMessage("model consistency checker expectedParents:");
		for (CtElement e : stack) {
			environment.debugMessage("    " + e.getClass().getSimpleName() + " " + (e.getPosition().isValidPosition() ? String.valueOf(e.getPosition()) : "(?)"));
		}
	}


	/**
	 * Represents an inconsistent element.
	 *
	 * @param element the element with the invalid parent
	 * @param expectedParents the expected parents of the element
	 */
	@Internal
	public record InconsistentElements(CtElement element, List<CtElement> expectedParents) {
		/**
		 * Creates a new inconsistent element.
		 *
		 * @param element the element with the invalid parent
		 * @param expectedParents the expected parents of the element
		 */
		public InconsistentElements {
			expectedParents = List.copyOf(expectedParents);
		}

		private String reason() {
			CtElement expectedParent = this.expectedParents.isEmpty() ? null : this.expectedParents.get(0);
			return "The element %s has the parent %s, but expected the parent %s".formatted(
				formatElement(this.element),
				this.element.isParentInitialized() ? formatElement(this.element.getParent()) : "null",
				expectedParent != null ? formatElement(expectedParent) : "null"
			);
		}

		private static String formatElement(CtElement ctElement) {
			String name = ctElement instanceof CtNamedElement ctNamedElement ? " " + ctNamedElement.getSimpleName() : "";

			return "%s%s".formatted(
				ctElement.getClass().getSimpleName(),
				name
			);
		}

		private String dumpExpectedParents() {
			return this.expectedParents.stream()
				.map(ctElement -> "    %s %s".formatted(
					ctElement.getClass().getSimpleName(),
					ctElement.getPosition().isValidPosition() ? String.valueOf(ctElement.getPosition()) : "(?)"
				))
				.collect(Collectors.joining(System.lineSeparator()));
		}

		@Override
		public String toString() {
			return "%s%n%s".formatted(this.reason(), this.dumpExpectedParents());
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}
			if (!(object instanceof InconsistentElements that)) {
				return false;
			}

			return this.element == that.element();
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this.element);
		}
	}
}
