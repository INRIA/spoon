/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
