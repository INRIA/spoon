/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
