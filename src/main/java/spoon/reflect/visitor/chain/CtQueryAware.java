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
package spoon.reflect.visitor.chain;

/**
 * Expert-only capability interface so as to write advanced {@link CtFunction} and {@link spoon.reflect.visitor.Filter}
 * that need to access the state of the top-level {@link CtQuery} instance
 * containing the function to be evaluated.
 *
 * Not meant to be implemented directly, only in conjunction with
 * {@link CtConsumableFunction}, {@link CtFunction} or {@link spoon.reflect.visitor.Filter}.
 */
public interface CtQueryAware {
	/**
	 * This method is called when the filter/function is added as a step to a {@link CtQuery} by the query engine ({@link CtQueryImpl}).
	 * @param query an instance registering this function/filter.
	 */
	void setQuery(CtQuery query);
}
