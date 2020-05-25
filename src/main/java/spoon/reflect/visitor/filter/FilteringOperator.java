/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

/**
 * This enumeration defines the possible composition operators for filters. It
 * is used in {@link spoon.reflect.visitor.filter.CompositeFilter}.
 *
 * @see spoon.reflect.visitor.Filter
 */
public enum FilteringOperator {

	/**
	 * Defines the union of several filters: it matches if one of the filters
	 * matches.
	 */
	UNION,
	/**
	 * Defines the intersection of several filters: it matches if all the
	 * filters match.
	 */
	INTERSECTION,
	/**
	 * Defines the substraction of several filters to one filter: it matches if
	 * the first filter matches and all the others do not match.
	 */
	SUBSTRACTION

}
