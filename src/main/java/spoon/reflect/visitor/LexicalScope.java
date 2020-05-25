/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.support.Experimental;

import java.util.function.Function;

/**
 * Represents that a lexical scope in the language
 *
 * Note that scopes are changing after variable declaration. For example:
 *
 * void draw() {
 *  //scope1
 * int a;
 * //scope2
 * int b;
 * //scope3
 * }
 *
 * See https://en.wikipedia.org/wiki/Scope_(computer_science)#Lexical_scoping
 */
@Experimental
public interface LexicalScope {
	/** adds an element to the scope */
	LexicalScope addNamedElement(CtNamedElement element);

	/**
	 * @return the {@link CtElement} which represents the current scope
	 */
	CtElement getScopeElement();

	/**
	 * @param name to be searched simple name
	 * @param fnc is called for each named element with same simple name, which is defined in this or parent {@link LexicalScope}.
	 * 	Function `fnc` is called as long as there are some matching elements and `fnc` returns null.
	 * 	If `fnc` returns not null value then searching is stopped and that value is a returned
	 * @return the value returned by `fnc` or null
	 */
	<T> T forEachElementByName(String name, Function<? super CtNamedElement, T> fnc);

}
