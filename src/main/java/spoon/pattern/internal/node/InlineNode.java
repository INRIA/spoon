/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.pattern.Generator;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.support.util.ImmutableMap;

/**
 * Represents a kind of {@link RootNode},
 * whose AST statements are understood as pattern statements.
 * For example CtForEach statement is handled as repeated generation of pattern
 * Or CtIf statement is handled as optionally generated pattern
 */
public interface InlineNode extends RootNode {
	/**
	 * Generates inline statements of this inline {@link RootNode}.
	 * This method is used when sources of pattern have to be printed
	 * @param generator a to be used {@link Generator}
	 * @param result holder of the result
	 * @param parameters a {@link ImmutableMap} with current parameters
	 */
	<T> void generateInlineTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters);
}
