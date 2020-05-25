/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.util.ArrayList;

import spoon.compiler.SpoonResource;

/**
 * A filtering resource, see https://github.com/INRIA/spoon/issues/877
 *
 */
public class FilteringFolder extends VirtualFolder {

	/** Removes all resources matching the given Java regex
	 * Eg. resources3.removeIfMatches(".*packageprotected.*");
	 */
	public FilteringFolder removeAllThatMatch(String regex) {
		for (SpoonResource f : new ArrayList<>(files)) {
			if (f.getPath().matches(regex)) {
				files.remove(f);
			}
		}
		return this;
	}

}
