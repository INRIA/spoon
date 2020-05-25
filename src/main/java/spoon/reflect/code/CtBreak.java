/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;



/**
 * This code element defines a break statement.
 * Example:
 * <pre>
 *     for(int i=0; i&lt;10; i++) {
 *         if (i&gt;3) {
 *				break; // &lt;-- break statement
 *         }
 *     }
 * </pre>
 */
public interface CtBreak extends CtLabelledFlowBreak {



	@Override
	CtBreak clone();
}
