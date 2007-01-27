/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.visitor;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtReference;

/**
 * A simple visitor that takes a reference filter and returns all the references
 * that match it.
 */

public class ReferenceQueryVisitor<T extends CtReference> extends CtScanner {
	ReferenceFilter<T> filter;

	List<T> result = new ArrayList<T>();

	/**
	 * Constructs a reference query visitor with a given reference filter.
	 */
	public ReferenceQueryVisitor(ReferenceFilter<T> filter) {
		super();
		this.filter = filter;
	}

	/**
	 * Gets the result (references matching the filter).
	 */
	public List<T> getResult() {
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(CtReference reference) {
		if (reference == null)
			return;
		if (filter.getType().isAssignableFrom(reference.getClass())) {
			if (filter.matches((T) reference)) {
				result.add((T) reference);
			}
		}
		super.scan(reference);
	}
}