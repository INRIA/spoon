/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.declaration;

import spoon.SpoonException;

/**
 * This exception is thrown when the parent of an element has not been correctly
 * initialized.
 *
 * @author Renaud Pawlak
 * @see CtElement#setParent(CtElement)
 * @see CtElement#getParent()
 * @see CtElement#updateAllParentsBelow()
 */
public class ParentNotInitializedException extends SpoonException {

	private static final long serialVersionUID = 1L;

	public ParentNotInitializedException(String message) {
		super(message);
	}

}
