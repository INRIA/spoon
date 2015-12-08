/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
