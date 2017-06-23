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
package spoon.experimental.modelobs.action;

import spoon.experimental.modelobs.context.Context;

/**
 * defines an action change on the model
 */
public abstract class Action {
	private final Context context;

	Action(Context context) {
		this.context = context;
	}

	/**
	 * get the changed value of the model
	 * @param <T> the type of the element
	 * @return the changed value
	 */
	public abstract <T> T getChangedValue();

	/**
	 * get the context of the change
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
}
