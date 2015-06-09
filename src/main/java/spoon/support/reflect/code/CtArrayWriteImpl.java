package spoon.support.reflect.code;

import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtExpression;
import spoon.reflect.visitor.CtVisitor;

public class CtArrayWriteImpl<T> extends CtArrayAccessImpl<T, CtExpression<?>>
		implements CtArrayWrite<T> {
	private static final long serialVersionUID = 1L;

	public void accept(CtVisitor visitor) {
		visitor.visitCtArrayWrite(this);
	}
}
