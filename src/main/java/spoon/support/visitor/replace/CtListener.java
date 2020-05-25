/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.replace;

import spoon.reflect.declaration.CtElement;

class CtListener implements ReplaceListener<CtElement> {
	private final CtElement element;

	CtListener(CtElement element) {
		this.element = element;
	}

	@Override
	public void set(CtElement replace) {
	}
}
