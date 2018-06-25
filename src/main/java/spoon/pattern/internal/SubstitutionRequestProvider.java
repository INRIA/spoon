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
