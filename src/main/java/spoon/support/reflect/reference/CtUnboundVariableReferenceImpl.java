package spoon.support.reflect.reference;

import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.visitor.CtVisitor;

/** represents a reference to an unbound field (used when no full classpath is available */
public class CtUnboundVariableReferenceImpl<T> extends CtVariableReferenceImpl<T> implements CtUnboundVariableReference<T> {
	private static final long serialVersionUID = -932423216089690817L;

	@Override
	public void accept(CtVisitor visitor) {
		
		visitor.visitCtUnboundVariableReference(this);
	}

	
}
