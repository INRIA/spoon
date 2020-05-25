/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal;

import spoon.pattern.internal.node.RootNode;

/**
 * Maps AST model object to the {@link RootNode}
 */
public interface SubstitutionRequestProvider {
	/**
	 * @param object a node from the Pattern model to be matched
	 * @return {@link RootNode}, which has to be used to match `object` from model of {@link SubstitutionRequestProvider}
	 */
	RootNode getTemplateValueResolver(Object object);
}
