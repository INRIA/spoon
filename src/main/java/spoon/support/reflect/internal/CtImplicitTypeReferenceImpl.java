package spoon.support.reflect.internal;

import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

public class CtImplicitTypeReferenceImpl<R> extends CtTypeReferenceImpl<R>
		implements CtImplicitTypeReference<R> {
	String name;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImplicitTypeReference(this);
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		name = simplename;
		return (T) this;
	}

	@Override
	public String getSimpleName() {
		return name;
	}
}
