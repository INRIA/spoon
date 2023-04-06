/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.util.internal;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

public final class ModelCollectionUtils {

	private ModelCollectionUtils() {
	}

	/**
	 * Changes the parent of a CtElement to the passed owner iff the element does not already have a
	 * different parent.
	 *
	 * @param owner the owner of the element
	 * @param element the element to set the parent for
	 */
	public static void linkToParent(CtElement owner, CtElement element) {
		if (!owner.getFactory().getEnvironment().checksAreSkipped()
				&& element.isParentInitialized() && element.getParent() != owner) {
			//the `e` already has an different parent. Check if it is still linked to that parent
			if (element.getRoleInParent() != null) {
				throw new SpoonException(
						"The default behavior has changed, a new check has been added! Don't worry, you can disable this check\n"
								+ "with one of the following options:\n"
								+ " - by configuring Spoon with getEnvironment().setSelfChecks(true)\n"
								+ " - by removing the node from its previous parent (element.delete())\n"
								+ " - by cloning the node before adding it here (element.clone())\n"
				);
			}
		}
		element.setParent(owner);
	}
}
