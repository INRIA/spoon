package spoon.test.generics.testclasses2;

import spoon.reflect.code.CtConditional;

public class SameSignature3<T extends String> implements ISameSignature3<T> {
    @Override
	public <U, K extends U, L extends T> void visitCtConditional(final CtConditional<U> conditional, K k, L l) {
	}
}

interface ISameSignature3<T> {
	<V, L extends V, K extends T> void visitCtConditional(final CtConditional<V> conditional, L l, K k);
}
