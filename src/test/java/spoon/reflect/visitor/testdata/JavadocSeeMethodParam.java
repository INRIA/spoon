/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.testdata;

import java.util.ArrayList;

/**
 * A class whose Javadoc contains a {@code @see} tag referencing a method
 * with an {@code ArrayList} parameter.
 *
 * @see JavadocSeeMethodParam#doSomething(ArrayList)
 */
public class JavadocSeeMethodParam {

	/**
	 * Does something with an ArrayList.
	 *
	 * @param items the list of items
	 */
	public void doSomething(ArrayList<String> items) {
	}
}
