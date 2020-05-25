/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.template.TemplateParameter;

/**
 * This code element represents a block of code, that is to say a list of
 * statements enclosed in curly brackets.
 *
 * Example: <pre>
 *  { // &lt;-- block start
 *   System.out.println("foo");
 *  }
 *	</pre>
 *
 * When the context calls for a return
 * value, the block should contain a return statement as a lastly reachable
 * statement. The returned type if any is given by R.
 */
public interface CtBlock<R> extends CtStatement, CtStatementList, TemplateParameter<R> {

	@Override
	CtBlock<R> clone();
}
