package spoon.support.reflect.code;

import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtArrayReadImpl<T> extends CtArrayAccessImpl<T, CtExpression<?>>
		implements CtArrayRead<T> {
	private static final long serialVersionUID = 1L;

	public void accept(CtVisitor visitor) {
		visitor.visitCtArrayRead(this);
	}
}
