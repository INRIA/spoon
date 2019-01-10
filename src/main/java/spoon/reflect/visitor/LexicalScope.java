/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
