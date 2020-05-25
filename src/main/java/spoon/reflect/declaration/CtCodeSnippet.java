/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.SNIPPET;

/**
 * This interface represents snippets of source code that can be used in the AST
 * to represent complex code without having to build the corresponding program
 * model structure. It is mainly provided on simplification purpose in order to
 * avoid having to build the program's model. Code snippets should be compiled
 * to validate their contents and the result of the compilation should be used
 * to replace the code snippet in the final AST.
 *
 * @see CtType#compileAndReplaceSnippets()
 */
public interface CtCodeSnippet {

	/**
	 * Sets the textual value of the code.
	 */
	@PropertySetter(role = SNIPPET)
	<C extends CtCodeSnippet> C setValue(String value);

	/**
	 * Gets the textual value of the code.
	 */
	@PropertyGetter(role = SNIPPET)
	String getValue();

}
