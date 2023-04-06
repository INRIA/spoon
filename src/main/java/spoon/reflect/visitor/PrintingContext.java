/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;

public class PrintingContext {
	private CtStatement statement;
	private EnumSet<PrintingOptions> states = EnumSet.noneOf(PrintingOptions.class);
	Deque<CacheBasedConflictFinder> currentThis = new ArrayDeque<>();
	Deque<CtElement> elementStack = new ArrayDeque<>();
	Deque<CtExpression<?>> parenthesedExpression = new ArrayDeque<>();
	CtType<?> currentTopLevel;
	/**
	 * @return true if we are printing first variable declaration of CtFor statement
	 */
	public boolean isFirstForVariable() {
		return states.contains(PrintingOptions.FIRST_FOR_VARIABLE);
	}

	/**
	 * @return true if we are printing second or next variable declaration of CtFor statement
	 */
	public boolean isNextForVariable() {
		return states.contains(PrintingOptions.NEXT_FOR_VARIABLE);
	}
	/**
	 * @return true if we are ignore generics while printing statement, false otherwise.
	 */
	public boolean ignoreGenerics() {
		return states.contains(PrintingOptions.IGNORE_GENERICS);
	}
	/**
	 * @return true if we skip the array brackets, false otherwise.
	 */
	public boolean skipArray() {
		return states.contains(PrintingOptions.SKIP_ARRAY);
	}
	/**
	 * @return true if we skip printing the static access, false otherwise.
	 */
	public boolean ignoreStaticAccess() {
		return states.contains(PrintingOptions.IGNORE_STATIC_ACCESS);
	}
	/**
	 * @return true if we skip printing the enclosing class, false access, false otherwise.
	 */
	public boolean ignoreEnclosingClass() {
		return states.contains(PrintingOptions.IGNORE_ENCLOSING_CLASS);
	}
	/**
	 * @return true if we force printing the generic wildcard '?', false otherwise.
	 */
	public boolean forceWildcardGenerics() {
		return states.contains(PrintingOptions.FORCE_WILDCARD_GENERICS);
	}

	/**
	 * @return true if `stmt` has to be handled as statement in current printing context
	 */
	public boolean isStatement(CtStatement stmt) {
		return this.statement == stmt;
	}

	/**
	 * @return the current top level type or null if no type is defined.
	 */
	public CtTypeReference<?> getCurrentTypeReference() {
		if (currentTopLevel != null) {
			CacheBasedConflictFinder tc = getCurrentTypeContext();
			if (tc != null) {
				return tc.typeRef;
			}
			return currentTopLevel.getReference();
		}
		return null;
	}

	private CacheBasedConflictFinder getCurrentTypeContext() {
		if (currentThis != null && !currentThis.isEmpty()) {
			return currentThis.peek();
		}
		return null;
	}

	/**
	 * Adds the given type to the stack of types.
	 * @param type the type to add.
	 */
	public void pushCurrentThis(CtType<?> type) {
		currentThis.push(new CacheBasedConflictFinder(type));
	}
/**
 * Removes the current type context from the stack.
 */
	public void popCurrentThis() {
		currentThis.pop();
	}

	/** (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PrintingContext [currentTopLevel=" + currentTopLevel.getQualifiedName() + ", statement=" + statement.getShortRepresentation()
				+ ", states=" + states + "]";
	}

	/**
	 * @param typeRef  used to check if we are in the context of this type.
	 * @return true if typeRef is equal to current (actually printed) Type (currentThis)
	 */
	public boolean isInCurrentScope(CtTypeReference<?> typeRef) {
		CtTypeReference<?> currentTypeRef = getCurrentTypeReference();
		return typeRef.equals(currentTypeRef);
	}
	/**
	 * Creates a new Writable instance, coping the current state and statement if set.
	 * @return a new {@link Writable} instance.
	 */
	public Writable modify() {
		return new Writable();
	}

	public class Writable implements AutoCloseable {
		private EnumSet<PrintingOptions> oldStates;
		private CtStatement oldStatement;

		protected Writable() {
			oldStatement = statement;
			oldStates = EnumSet.copyOf(states);
		}

		@Override
		public void close() {
			states = EnumSet.copyOf(oldStates);
			statement = oldStatement;
		}

		/**
		 * @param add true to the set option, false for removing. {@link PrintingContext#isFirstForVariable()}
		 */
		public <T extends Writable> T isFirstForVariable(boolean add) {
			modifyState(PrintingOptions.FIRST_FOR_VARIABLE, add);
			return (T) this;
		}

		public <T extends Writable> T isNextForVariable(boolean add) {
			modifyState(PrintingOptions.NEXT_FOR_VARIABLE, add);
			return (T) this;
		}

		public <T extends Writable> T ignoreGenerics(boolean add) {
			modifyState(PrintingOptions.IGNORE_GENERICS, add);
			return (T) this;
		}
		public <T extends Writable> T skipArray(boolean add) {
			modifyState(PrintingOptions.SKIP_ARRAY, add);
			return (T) this;
		}

		public <T extends Writable> T ignoreStaticAccess(boolean add) {
			modifyState(PrintingOptions.IGNORE_STATIC_ACCESS, add);
			return (T) this;
		}

		public <T extends Writable> T ignoreEnclosingClass(boolean add) {
			modifyState(PrintingOptions.IGNORE_ENCLOSING_CLASS, add);
			return (T) this;
		}

		public <T extends Writable> T forceWildcardGenerics(boolean add) {
			modifyState(PrintingOptions.FORCE_WILDCARD_GENERICS, add);
			return (T) this;
		}
		/**
		 * Modifies the current printing state.
		 * @param option the option to modify.
		 * @param add true to add the option, false to remove it.
		 */
		private void modifyState(PrintingOptions option, boolean add) {
			if (add) {
				states.add(option);
			} else {
				states.remove(option);
			}
		}
		/**
		 * There are statements (e.g. invocation), which may play role of expression too.
		 * They have to be suffixed by semicolon depending on the printing context.
		 * Call this method to inform printer that invocation is used as statement.
		 *
		 * @param stmt the instance of the actually printed statement.
		 * Such statement will be finished by semicolon.
		 */
		public <T extends Writable> T setStatement(CtStatement stmt) {
			statement = stmt;
			return (T) this;
		}
	}


	/**
	 * This enums defines the printing options. The options are used to control the printing of the code.
	 */
	private enum PrintingOptions {
		NEXT_FOR_VARIABLE, IGNORE_GENERICS, SKIP_ARRAY, IGNORE_STATIC_ACCESS, IGNORE_ENCLOSING_CLASS, FORCE_WILDCARD_GENERICS, FIRST_FOR_VARIABLE
	}
}
