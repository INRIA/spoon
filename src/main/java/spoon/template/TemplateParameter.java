/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.template;

/**
 * This interface defines a typed template parameter. It is parameterized by
 * <code>T</code>, the type of the template parameter, which can be retrieved
 * by the {@link #S()} method. For more details on how to use template
 * parameters, see {@link Template}.
 */
public interface TemplateParameter<T> {

	/**
	 * Gets the type of the template parameter. This methods has no runtime
	 * meaning (should return a <code>null</code> reference) but is used as a
	 * marker in a template code.
	 */
	T S();

}
