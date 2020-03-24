/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

/**
 * This code element defines a switch statement.
 *
 * Example: <pre>
 * int x = 0;
 * switch(x) { // &lt;-- switch statement
 *     case 1:
 *       System.out.println("foo");
 * }</pre>

 * @param <S>
 * 		the type of the selector expression (it would be better to be able
 * 		to define an upper bound, but it is not possible because of Java's
 * 		type hierarchy, especially since the enums that make things even
 * 		worse!)
 */
public interface CtSwitch<S> extends CtStatement, CtAbstractSwitch<S> {

	@Override
	CtSwitch<S> clone();
}
