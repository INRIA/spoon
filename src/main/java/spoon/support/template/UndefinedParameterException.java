/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.template;

import spoon.template.TemplateException;

public class UndefinedParameterException extends TemplateException {

	private static final long serialVersionUID = 1L;

	public UndefinedParameterException() {
	}

	public UndefinedParameterException(String message) {
		super(message);
	}

	public UndefinedParameterException(Throwable cause) {
		super(cause);
	}

}
