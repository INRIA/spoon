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
package spoon.template;

import spoon.SpoonException;

/**
 * This runtime exception can be throws when something wrong occurs in template
 * operations such as loading, substitution, and matching.
 *
 * @see spoon.template.Substitution
 * @see spoon.template.TemplateMatcher
 */
public class TemplateException extends SpoonException {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty exception.
	 */
	public TemplateException() {
		super();
	}

	/**
	 * Exception with a message.
	 */
	public TemplateException(String message) {
		super(message);
	}

	/**
	 * Exception with a cause.
	 */
	public TemplateException(Throwable cause) {
		super(cause);
	}

}
