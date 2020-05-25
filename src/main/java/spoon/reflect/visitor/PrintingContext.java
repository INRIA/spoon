/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayDeque;
import java.util.Deque;

public class PrintingContext {

	private long NEXT_FOR_VARIABLE       = 1 << 0;
	private long IGNORE_GENERICS         = 1 << 1;
	private long SKIP_ARRAY              = 1 << 2;
	private long IGNORE_STATIC_ACCESS    = 1 << 3;
	private long IGNORE_ENCLOSING_CLASS  = 1 << 4;
	private long FORCE_WILDCARD_GENERICS = 1 << 5;
	private long FIRST_FOR_VARIABLE      = 1 << 6;

	private long state;
	private CtStatement statement;

	/**
	 * @return true if we are printing first variable declaration of CtFor statement
	 */
	public boolean isFirstForVariable() {
		return (state & FIRST_FOR_VARIABLE) != 0L;
	}

	/**
	 * @return true if we are printing second or next variable declaration of CtFor statement
	 */
	public boolean isNextForVariable() {
		return (state & NEXT_FOR_VARIABLE) != 0L;
	}

	public boolean ignoreGenerics() {
		return (state & IGNORE_GENERICS) != 0L;
	}

	public boolean skipArray() {
		return (state & SKIP_ARRAY) != 0L;
	}

	public boolean ignoreStaticAccess() {
		return (state & IGNORE_STATIC_ACCESS) != 0L;
	}

	public boolean ignoreEnclosingClass() {
		return (state & IGNORE_ENCLOSING_CLASS) != 0L;
	}

	public boolean forceWildcardGenerics() {
		return (state & FORCE_WILDCARD_GENERICS) != 0L;
	}

	/**
	 * @return true if `stmt` has to be handled as statement in current printing context
	 */
	public boolean isStatement(CtStatement stmt) {
		return this.statement == stmt;
	}

	public class Writable implements AutoCloseable {
		private long oldState;
		private CtStatement oldStatement;

		protected Writable() {
			oldState = state;
			oldStatement = statement;
		}

		@Override
		public void close() {
			state = oldState;
			statement = oldStatement;
		}

		/**
		 * @param v use true if printing first variable declaration of CtFor statement
		 */
		public <T extends Writable> T isFirstForVariable(boolean v) {
			setState(FIRST_FOR_VARIABLE, v);
			return (T) this;
		}

		/**
		 * @param v use true if printing second or next variable declaration of CtFor statement
		 */
		public <T extends Writable> T isNextForVariable(boolean v) {
			setState(NEXT_FOR_VARIABLE, v);
			return (T) this;
		}

		public <T extends Writable> T ignoreGenerics(boolean v) {
			setState(IGNORE_GENERICS, v);
			return (T) this;
		}

		public <T extends Writable> T skipArray(boolean v) {
			setState(SKIP_ARRAY, v);
			return (T) this;
		}

		public <T extends Writable> T ignoreStaticAccess(boolean v) {
			setState(IGNORE_STATIC_ACCESS, v);
			return (T) this;
		}

		public <T extends Writable> T ignoreEnclosingClass(boolean v) {
			setState(IGNORE_ENCLOSING_CLASS, v);
			return (T) this;
		}

		public <T extends Writable> T forceWildcardGenerics(boolean v) {
			setState(FORCE_WILDCARD_GENERICS, v);
			return (T) this;
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

		private void setState(long mask, boolean v) {
			state = v ? state | mask : state & ~mask;
		}
	}

	public Writable modify() {
		return new Writable();
	}

	Deque<CacheBasedConflictFinder> currentThis = new ArrayDeque<>();

	/**
	 * @return top level type
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

	public void pushCurrentThis(CtType<?> type) {
		currentThis.push(new CacheBasedConflictFinder(type));
	}

	public void popCurrentThis() {
		currentThis.pop();
	}


	Deque<CtElement> elementStack = new ArrayDeque<>();

	Deque<CtExpression<?>> parenthesedExpression = new ArrayDeque<>();

	CtType<?> currentTopLevel;

	@Override
	public String toString() {
		return "context.ignoreGenerics: " + ignoreGenerics() + "\n";
	}

	/**
	 * @param typeRef
	 * @return true if typeRef is equal to current (actually printed) Type (currentThis)
	 */
	public boolean isInCurrentScope(CtTypeReference<?> typeRef) {
		CtTypeReference<?> currentTypeRef = getCurrentTypeReference();
		return typeRef.equals(currentTypeRef);
	}
}
