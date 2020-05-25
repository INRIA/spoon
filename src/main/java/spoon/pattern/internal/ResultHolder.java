/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import spoon.SpoonException;

/**
 * Container for single or multiple values of required type
 */
public abstract class ResultHolder<T> {
	private final Class<T> requiredClass;

	public ResultHolder(Class<T> requiredClass) {
		this.requiredClass = requiredClass;
	}

	/**
	 * @return the class of values, which acceptable by this result holder
	 */
	public Class<T> getRequiredClass() {
		return requiredClass;
	}

	/**
	 * @return true if it accepts 0, 1 or more values. false if it accepts exactly one value. If none, then value is null
	 */
	public abstract boolean isMultiple();

	/**
	 * adds a result into this result holder
	 * @param value a new value of result holder
	 */
	public abstract void addResult(T value);

	/**
	 * calls consumer.accept(value) once for each contained value
	 * @param consumer
	 */
	public abstract void mapEachResult(Function<T, T> consumer);
	/**
	 * @return List of actually stored results
	 */
	public abstract List<T> getResults();

	/**
	 * Container of single value of required type
	 *
	 * @param <T>
	 */
	public static class Single<T> extends ResultHolder<T> {

		private T result;

		public Single(Class<T> requiredClass) {
			super(requiredClass);
		}

		@Override
		public boolean isMultiple() {
			return false;
		}

		@Override
		public void addResult(T value) {
			if (this.result != null) {
				throw new SpoonException("Cannot add second value into single value ConversionContext");
			}
			this.result = value;
		}

		public T getResult() {
			return result;
		}

		@Override
		public void mapEachResult(Function<T, T> consumer) {
			result = consumer.apply(result);
		}

		@Override
		public List<T> getResults() {
			return result == null ? Collections.emptyList() : Collections.singletonList(result);
		}
	}

	/**
	 * Container of multiple values of required type
	 *
	 * @param <T>
	 */
	public static class Multiple<T> extends ResultHolder<T> {

		List<T> result = new ArrayList<>();

		public Multiple(Class<T> requiredClass) {
			super(requiredClass);
		}

		@Override
		public boolean isMultiple() {
			return true;
		}
		@Override
		public void addResult(T value) {
			this.result.add(value);
		}

		public List<T> getResult() {
			return result;
		}

		@Override
		public List<T> getResults() {
			return result;
		}

		@Override
		public void mapEachResult(Function<T, T> consumer) {
			for (ListIterator<T> iter = result.listIterator(); iter.hasNext();) {
				iter.set(consumer.apply(iter.next()));
			}
		}
	}
}
