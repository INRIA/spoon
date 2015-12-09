/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.InternalFactory;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.support.reflect.internal.CtCircularTypeReferenceImpl;
import spoon.support.reflect.internal.CtImplicitArrayTypeReferenceImpl;
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

	@Override
	public <T> CtImplicitArrayTypeReference<T> createImplicitArrayTypeReference() {
		final CtImplicitArrayTypeReference<T> e = new CtImplicitArrayTypeReferenceImpl<T>();
		e.setFactory(mainFactory);
		return e;
	}
}
