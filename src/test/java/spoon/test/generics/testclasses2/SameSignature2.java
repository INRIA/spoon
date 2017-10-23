package spoon.test.generics.testclasses2;

import spoon.reflect.code.CtConditional;

public class SameSignature2 implements ISameSignature {
    @Override
	public <U> void visitCtConditional(final CtConditional<U> conditional) {
	}
}

interface ISameSignature {
	<V> void visitCtConditional(final CtConditional<V> conditional);
}
