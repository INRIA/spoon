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
package spoon.reflect.visitor.chain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A Consumer which has a list of other Consumers.
 * If the {@link #accept(Object)} method is called then it calls {@link Consumer#accept(Object)} for each registered consumer
 *
 * If the element, which is passed as input of this Consumer does not match parameter type of next consumer
 * then this call is silently skipped, with meaning such consumer is not interested in such elements - no problem.
 *
 * @param <T> the type of element which is accepted by this consumer
 */
public class MultiConsumer<T> implements Consumer<T> {
	private List<Consumer<T>> consumers = new ArrayList<>(1);

	public MultiConsumer() {
	}

	@Override
	public void accept(T element) {
		if (element == null) {
			return;
		}
		for (Consumer<T> consumer : consumers) {
			try {
				consumer.accept(element);
			} catch (ClassCastException e) {
				//in case of Lambda expressions, the type of accept method cannot be detected,
				//so then it fails with CCE. Handle it silently with meaning: "input element is not wanted by this Consumer. Ignore it"
			}
		}
	}

	/**
	 * Adds Consumer, which will be called when {@link #accept(Object)} is invoked
	 * @param consumer
	 * @return this instance of MultiConsumer - support of fluent API
	 */
	public MultiConsumer<T> add(Consumer<T> consumer) {
		consumers.add(consumer);
		return this;
	}

	/**
	 * Removes before registered Consumer
	 * @param consumer
	 * @return this instance of MultiConsumer - support of fluent API
	 */
	public MultiConsumer<T> remove(Consumer<T> consumer) {
		consumers.remove(consumer);
		return this;
	}

	public MultiConsumer<T> setLogging(boolean logging) {
//		invoke_accept.setLogging(logging);
		return this;
	}
}
