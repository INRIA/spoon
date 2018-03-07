/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import spoon.SpoonException;

/**
 * Container for single or multiple values of required type
 */
public abstract class ResultHolder<T> {
	private Class<T> requiredClass;

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
	 * @param requiredClass only the values of this class are acceptable by this result holder
	 * @return this to support fluent API
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <U> ResultHolder<U> setRequiredClass(Class<U> requiredClass) {
		this.requiredClass = (Class) requiredClass;
		return (ResultHolder<U>) this;
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
		};

		public <U> ResultHolder.Single<U> setRequiredClass(Class<U> requiredClass) {
			return (ResultHolder.Single<U>) super.setRequiredClass(requiredClass);
		}
	}

	/**
	 * Container of multiple values of required type
	 *
	 * @param <T>
	 */
	public static class Multiple<T> extends ResultHolder<T> {

		List<T> result = new ArrayList<>();

		Multiple(Class<T> requiredClass) {
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
		public void mapEachResult(Function<T, T> consumer) {
			for (ListIterator<T> iter = result.listIterator(); iter.hasNext();) {
				iter.set(consumer.apply(iter.next()));
			}
		}

		public <U> ResultHolder.Multiple<U> setRequiredClass(Class<U> requiredClass) {
			return (ResultHolder.Multiple<U>) super.setRequiredClass(requiredClass);
		}
	}
}
