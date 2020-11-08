/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.constraints;

import java.util.function.Predicate;
/**
 * This class defines an exists quantifier. For every element it returns true.
 * This allows checking, that no element of a specific type e.g. lambda is part of the meta model.
 */
public class Exists<T> implements Predicate<T> {

	@Override
	public boolean test(T t) {
		return false;
	}
}
