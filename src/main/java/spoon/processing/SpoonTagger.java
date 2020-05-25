/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import spoon.reflect.declaration.CtClass;

/**
 * Creates an empty class spoon.Spoon
 */
public class SpoonTagger extends AbstractManualProcessor {

	@Override
	public void process() {
		CtClass<?> spoon = getFactory().Class().create("spoon.Spoon");
	}
}
