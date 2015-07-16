package spoon.support.reflect.reference;

import spoon.reflect.reference.CtImplicitTypeReference;
import spoon.reflect.reference.CtReference;

public class CtImplicitTypeReferenceImpl<R> extends CtTypeReferenceImpl<R>
		implements CtImplicitTypeReference<R> {
	String name;

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
