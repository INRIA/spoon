package spoon.support.reflect.code;

import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.visitor.CtVisitor;

public class CtTypeAccessImpl<T> extends CtExpressionImpl<T> implements CtTypeAccess<T> {
	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtTypeAccess(this);
	}
}
