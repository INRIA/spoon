package spoon.support.reflect.code;

import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.visitor.CtVisitor;

public class CtFieldWriteImpl<T> extends CtFieldAccessImpl<T> implements CtFieldWrite<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtFieldWrite(this);
	}
}
