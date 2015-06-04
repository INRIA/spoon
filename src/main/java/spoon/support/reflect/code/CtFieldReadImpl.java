package spoon.support.reflect.code;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.visitor.CtVisitor;

public class CtFieldReadImpl<T> extends CtFieldAccessImpl<T> implements CtFieldRead<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtFieldRead(this);
	}
}
