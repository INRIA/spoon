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
package spoon.reflect;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtElement;

/**
 * This class is a container for a list changes that may have occurred on a model
 * because of a transformation.
 */
public class Changes {

	List<CtElement> added;

	List<CtElement> modified;

	List<CtElement> removed;

	/**
	 * Creates an instance to be further initialized.
	 */
	public Changes() {
	}

	/**
	 * Gets the list of elements added in the model.
	 */
	public List<CtElement> getAdded() {
		if (added == null) {
			added = new ArrayList<>();
		}
		return added;
	}

	/**
	 * Gets the list of elements removed from the model.
	 */
	public List<CtElement> getRemoved() {
		if (removed == null) {
			removed = new ArrayList<>();
		}
		return removed;
	}

	/**
	 * Gets the list of updated elements.
	 */
	public List<CtElement> getModified() {
		if (modified == null) {
			modified = new ArrayList<>();
		}
		return modified;
	}

	/**
	 * Returns true if elements are added.
	 */
	public boolean hasAdded() {
		return added != null && !added.isEmpty();
	}

	/**
	 * Returns true if elements are modified.
	 */
	public boolean hasModified() {
		return modified != null && !modified.isEmpty();
	}

	/**
	 * Returns true if elements are removed.
	 */
	public boolean hasRemoved() {
		return removed != null && !removed.isEmpty();
	}

}
