/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
	<C extends CtCodeSnippet> C setValue(String value);

	/**
	 * Gets the textual value of the code.
	 */
	String getValue();

}
