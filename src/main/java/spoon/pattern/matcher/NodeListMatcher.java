/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.pattern.matcher;
//TODO move to spoon.pattern.matcher

import java.util.List;

import spoon.pattern.ParameterValueProvider;
import spoon.reflect.declaration.CtElement;

/**
 * Marks the SubstitutionRequest which has to match whole AST node (not only some attribute of node)
 */
public interface NodeListMatcher {
	/**
	 * matches this {@link NodeListMatcher} with `nextTargets` and `nextTemplates` storing the matched values into `parameters`
	 * @param parameters the collector of matched parameters
	 * @param nextTargets the List of
	 * @param nextTemplates
	 * @return if this {@link NodeListMatcher} matched and all `nextTemplates` matched, then return number of matching items from `nextTargets`
	 * if something doesn't match, then return -1
	 */
	int matches(ParameterValueProvider parameters, List<CtElement> nextTargets, List<CtElement> nextTemplates);
}
