/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.factory;

import spoon.reflect.declaration.CtType;
import spoon.support.visitor.java.JavaReflectionTreeBuilder;

import java.util.HashMap;
import java.util.Map;

import static spoon.testing.utils.ModelUtils.createFactory;

/**
 * Created by bdanglot on 10/17/16.
 */
public class ShadowFactory extends SubFactory {

	private Map<Class<?>, CtType<?>> shadowCache;

	/**
	 * The sub-factory constructor takes an instance of the parent factory.
	 *
	 * @param factory
	 */
	public ShadowFactory(Factory factory) {
		super(factory);
		this.shadowCache = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> CtType<T> get(Class<?> cl) {
		final CtType<T> shadowClass = (CtType<T>) shadowCache.get(cl);
		if (shadowClass == null) {
			final CtType<T> newShadowClass = new JavaReflectionTreeBuilder(createFactory()).scan((Class<T>) cl);
			this.shadowCache.put(cl, newShadowClass);
			return newShadowClass;
		} else {
			return shadowClass;
		}
	}


}
