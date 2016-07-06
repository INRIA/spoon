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
			throw new RuntimeException("Can't instante class processor.", e);
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

	/**
	 * @deprecated
	 */
	@Deprecated
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException("\'equals\' is not supported...maybe you intended to call \'isEqualTo\'");
	}

	public int hashCode() {
		return 1;
	}
}
