/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 *  This file originally comes from JavaParser and is distributed under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 */
package spoon.javadoc.internal;

	/**
	* Simply a pair of objects.
	*
	* @param <A> type of object a.
	* @param <B> type of object b.
	*/
	public class Pair<A, B> {
	public final A a;
	public final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair<?, ?> pair = (Pair<?, ?>) o;

		if (a != null ? !a.equals(pair.a) : pair.a != null) {
			return false;
		}
		if (b != null ? !b.equals(pair.b) : pair.b != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = a != null ? a.hashCode() : 0;
		return 31 * result + (b != null ? b.hashCode() : 0);
	}
	}
