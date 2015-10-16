package spoon.support.reflect.internal;

import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;

public class CtImplicitArrayTypeReferenceImpl<T> extends CtArrayTypeReferenceImpl<T> implements CtImplicitArrayTypeReference<T> {
	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImplicitArrayTypeReference(this);
	}
}
