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
import spoon.reflect.declaration.CtElement;

public abstract class Action {
	private final Context context;
	private final CtElement newElement;
	private final Object newValue;

	Action(Context context, CtElement newElement) {
		this.context = context;
		this.newElement = newElement;
		this.newValue = null;
	}

	Action(Context context, Object newValue) {
		this.context = context;
		this.newValue = newValue;
		this.newElement = null;
	}

	public abstract void rollback();

	public Context getContext() {
		return context;
	}

	public CtElement getNewElement() {
		return newElement;
	}

	public Object getNewValue() {
		return newValue;
	}
}
