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
package spoon.compiler;

import spoon.SpoonException;

/**
 * thrown when the Spoon model of a program cannot be built
 */
public class ModelBuildingException extends SpoonException {
	private static final long serialVersionUID = 5029153216403064030L;

	public ModelBuildingException(String msg) {
		super(msg);
	}

	public ModelBuildingException(String msg, Exception e) {
		super(msg, e);
	}
}
