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
