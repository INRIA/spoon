/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

/**
 * Represents an action of Printer, which prints whole element
 */
public abstract class ElementPrinterEvent implements PrinterEvent {
	private final CtRole role;
	private final CtElement element;

	public ElementPrinterEvent(CtRole role, CtElement element) {
		this.role = role;
		this.element = element;
	}

	@Override
	public CtRole getRole() {
		return role;
	}

	@Override
	public SourcePositionHolder getElement() {
		return element;
	}

	@Override
	public String toString() {
		if (role != null && element != null) {
			return role.name() + "->" + element.toStringDebug();
		}
		return "illformed ElementPrinterEvent";
	}
}

