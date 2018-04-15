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
package spoon.pattern.node;

import spoon.pattern.Generator;
import spoon.pattern.ResultHolder;
import spoon.pattern.parameter.ParameterValueProvider;

/**
 * Represents a kind of {@link RootNode},
 * whose AST statements are understood as pattern statements.
 * For example CtForEach statement is handled as repeated generation of pattern
 * Or CtIf statement is handled as optionally generated pattern
 */
public interface LiveNode extends RootNode {
	/**
	 * Generates Live statements of this live {@link RootNode}.
	 * This method is used when sources of pattern have to be printed
	 * @param generator
	 * @param result holder of the result
	 * @param parameters
	 */
	<T> void generateLiveTargets(Generator generator, ResultHolder<T> result, ParameterValueProvider parameters);
}
