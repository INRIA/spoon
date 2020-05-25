/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing;

import spoon.SpoonException;
import spoon.processing.Processor;

import java.util.LinkedList;

/**
 * Base contract for all assertion objects: the minimum functionality that
 * any assertion object should provide.
 *
 * @param <T>
 * 		the self type of this assertion class.
 * @param <A>
 * 		the type of the actual value.
 */
public abstract class AbstractAssert<T extends AbstractAssert<T, A>, A> {
	protected final LinkedList<Processor<?>> processors = new LinkedList<>();
	protected final A actual;
	protected final T myself;

	protected AbstractAssert(A actual, Class<?> selfType) {
		this.myself = (T) selfType.cast(this);
		this.actual = actual;
	}

	/**
	 * Applies the processor on the actual value.
	 *
	 * @param processor
	 * 		the given processor.
	 * @return {@code this} assertion object.
	 */
	public T withProcessor(Processor<?> processor) {
		processors.add(processor);
		return myself;
	}

	/**
	 * Applies the processor on the actual value.
	 *
	 * @param processor
	 * 		the class of the given processor.
	 * @return {@code this} assertion object.
	 */
	public T withProcessor(Class<? extends Processor<?>> processor) {
		try {
			withProcessor(processor.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Can't instantiate class processor.", e);
		}
		return myself;
	}

	/**
	 * Applies the processor on the actual value.
	 *
	 * @param qualifiedName
	 * 		the qualified name of the given processor.
	 * @return {@code this} assertion object.
	 */
	public T withProcessor(String qualifiedName) {
		try {
			withProcessor((Class<? extends Processor<?>>) Thread.currentThread().getContextClassLoader().loadClass(qualifiedName));
		} catch (ClassNotFoundException e) {
			throw new SpoonException("Unable to load processor \"" + qualifiedName + "\"", e);
		}
		return myself;
	}

	public int hashCode() {
		return 1;
	}
}
