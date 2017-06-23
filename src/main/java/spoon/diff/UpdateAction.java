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
package spoon.diff;

import spoon.diff.context.Context;

public class UpdateAction<T> extends Action {
	private final T oldElement;
	private final T newElement;

	public UpdateAction(Context context, T newElement, T oldElement) {
		super(context);
		this.oldElement = oldElement;
		this.newElement = newElement;
	}

	@Override
	public T getChangedElement() {
		return getNewElement();
	}

	public T getNewElement() {
		return newElement;
	}

	public T getOldElement() {
		return oldElement;
	}
}
