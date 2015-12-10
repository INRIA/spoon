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
package spoon.support.template;

import spoon.reflect.declaration.CtElement;
import spoon.template.TemplateMatcher;

/**
 * Allows the definition of a specific matching policy for a given template
 * parameter. When using {@link spoon.template.TemplateMatcher}, parameters are
 * by default matched to anything. Defining a new type and precising it in the
 * {@link spoon.template.Parameter} annotation allows to precise the form that
 * the parameter will match.
 *
 * <p>
 * Note that this feature is not yet fully supported but will be in a close
 * future.
 */
public interface ParameterMatcher {

	/**
	 * To be defined to implement a matching strategy for template parameter(s).
	 *
	 * @param templateMatcher
	 * 		the instance of the matcher that is currently performing the
	 * 		matching (up-caller)
	 * @param template
	 * 		the template element to match against
	 * @param toMatch
	 * 		the element to be tested for a match
	 * @return true if matching
	 */
	boolean match(TemplateMatcher templateMatcher, CtElement template, CtElement toMatch);

}
