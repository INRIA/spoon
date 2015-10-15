package spoon.support;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.InternalFactory;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.support.reflect.internal.CtCircularTypeReferenceImpl;

public class DefaultInternalFactory implements InternalFactory {
	Factory mainFactory;

	public DefaultInternalFactory(Factory factory) {
		mainFactory = factory;
	}

	@Override
	public <T> CtCircularTypeReference createCircularTypeReference() {
		CtCircularTypeReference e = new CtCircularTypeReferenceImpl();
		e.setFactory(mainFactory);
		return e;
	}
}
