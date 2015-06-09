package spoon.support.reflect.code;

import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.visitor.CtVisitor;

public class CtVariableWriteImpl<T> extends CtVariableAccessImpl<T> implements CtVariableWrite<T> {
	private static final long serialVersionUID = 1L;

	public void accept(CtVisitor visitor) {
		visitor.visitCtVariableWrite(this);
	}
}
