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

import java.util.function.Consumer;

import spoon.pattern.matcher.TobeMatched;
import spoon.reflect.code.CtStatementList;

/**
 * Delivers to be substituted value
 * Matches value
 */
abstract class AbstractPrimitiveMatcher extends AbstractRepeatableMatcher implements PrimitiveMatcher {

	protected AbstractPrimitiveMatcher() {
	}


	/**
	 * calls consumer.accept(Object) once for each item of the `multipleValues` collection or array.
	 * If it is not a collection or array then it calls consumer.accept(Object) once with `multipleValues`
	 * If `multipleValues` is null then consumer.accept(Object) is not called
	 * @param multipleValues to be iterated potential collection of items
	 * @param consumer the receiver of items
	 */
	@SuppressWarnings("unchecked")
	static void forEachItem(Object multipleValues, Consumer<Object> consumer) {
		if (multipleValues instanceof CtStatementList) {
			//CtStatementList extends Iterable, but we want to handle it as one node.
			consumer.accept(multipleValues);
			return;
		}
		if (multipleValues instanceof Iterable) {
			for (Object item : (Iterable<Object>) multipleValues) {
				consumer.accept(item);
			}
			return;
		}
		if (multipleValues instanceof Object[]) {
			for (Object item : (Object[]) multipleValues) {
				consumer.accept(item);
			}
			return;
		}
		consumer.accept(multipleValues);
	}

	@Override
	public TobeMatched matchAllWith(TobeMatched tobeMatched) {
		//we are matching single CtElement or attribute value
		return tobeMatched.matchNext((target, parameters) -> {
			return matchTarget(target, tobeMatched.getParameters());
		});
	}
}
