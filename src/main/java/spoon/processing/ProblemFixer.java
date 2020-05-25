/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.processing;

import spoon.compiler.Environment;
import spoon.reflect.Changes;
import spoon.reflect.declaration.CtElement;

/**
 * This interface defines problem fixers. Problem fixers can be provided when a
 * problem is reported to the environment. The user can then chose what fixer to
 * use.
 *
 * @see Environment#report(Processor, org.apache.log4j.Level, CtElement, String,
 * ProblemFixer[])
 */
public interface ProblemFixer<T extends CtElement> extends FactoryAccessor {

	/**
	 * Returns the description of this fixer
	 */
	String getDescription();

	/**
	 * Returns a short String that represent this fixer.
	 */
	String getLabel();

	/**
	 * Runs this fix on given element. This fixer should modify the given model
	 * and return a list of the modified elements.
	 *
	 * @param element
	 * 		the element marked by a problem
	 * @return List of modified elements
	 */
	Changes run(T element);
}
