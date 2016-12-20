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
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayDeque;
import java.util.Deque;

public class PrintingContext {

	private long NO_TYPE_DECL 			= 1 << 0;
	private long IGNORE_GENERICS 		= 1 << 1;
	private long SKIP_ARRAY 			= 1 << 2;
	private long IGNORE_STATIC_ACCESS   = 1 << 3;
	private long IGNORE_ENCLOSING_CLASS = 1 << 4;

	private long state;

	public boolean noTypeDecl() {
		return (state & NO_TYPE_DECL) != 0L;
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

	public class Writable implements AutoCloseable {
		private long oldState;

		protected Writable() {
			oldState = state;
		}
		@Override
		public void close() {
			state = oldState;
		}

		public <T extends Writable> T noTypeDecl(boolean v) {
			setState(NO_TYPE_DECL, v);
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
		private void setState(long mask, boolean v) {
			state = v ? state | mask : state & ~mask;
		}
	}

	public Writable modify() {
		return new Writable();
	}

	Deque<TypeContext> currentThis = new ArrayDeque<>();

	/**
	 * @return top level type
	 */
	public CtTypeReference<?> getCurrentTypeReference() {
		if (currentTopLevel != null) {
			TypeContext tc = getCurrentTypeContext();
			if (tc != null) {
				return tc.typeRef;
			}
			return currentTopLevel.getReference();
		}
		return null;
	}
	private TypeContext getCurrentTypeContext() {
		if (currentThis != null && currentThis.size() > 0) {
			TypeContext tc = currentThis.peek();
			return tc;
		}
		return null;
	}

	public void pushCurrentThis(CtType<?> type) {
		currentThis.push(new TypeContext(type));
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
		return currentTypeRef != null && typeRef.equals(currentTypeRef);
	}
}
