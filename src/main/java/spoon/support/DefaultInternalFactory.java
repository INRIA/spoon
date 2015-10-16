package spoon.support;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.InternalFactory;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.support.reflect.internal.CtCircularTypeReferenceImpl;
import spoon.support.reflect.internal.CtImplicitTypeReferenceImpl;

public class DefaultInternalFactory implements InternalFactory {
	private final Factory mainFactory;

	public DefaultInternalFactory(Factory factory) {
		mainFactory = factory;
	}

	@Override
	public CtCircularTypeReference createCircularTypeReference() {
		CtCircularTypeReference e = new CtCircularTypeReferenceImpl();
		e.setFactory(mainFactory);
		return e;
	}

	@Override
	public <T> CtImplicitTypeReference<T> createImplicitTypeReference() {
		final CtImplicitTypeReferenceImpl<T> e = new CtImplicitTypeReferenceImpl<T>();
		e.setFactory(mainFactory);
		return e;
	}
}
