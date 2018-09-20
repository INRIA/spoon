/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.support.sniper.internal;

import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.path.CtRole;

/**
 * Represents an action of Printer, which prints whole element
 */
public abstract class ElementPrinterEvent implements PrinterEvent {
	private final CtRole role;
	private final SourcePositionHolder element;

	public ElementPrinterEvent(CtRole role, SourcePositionHolder element) {
		super();
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
	public String getToken() {
		return null;
	}
	@Override
	public boolean isWhitespace() {
		return false;
	}
	@Override
	public String toString() {
		return role.name() + "->" + element.toString();
	}
}

