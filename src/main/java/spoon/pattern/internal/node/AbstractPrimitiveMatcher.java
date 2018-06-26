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
package spoon.pattern.internal.node;

import spoon.pattern.internal.matcher.TobeMatched;

/**
 * Delivers to be substituted value
 * Matches value
 */
abstract class AbstractPrimitiveMatcher extends AbstractRepeatableMatcher implements PrimitiveMatcher {

	protected AbstractPrimitiveMatcher() {
	}


	@Override
	public TobeMatched matchAllWith(TobeMatched tobeMatched) {
		//we are matching single CtElement or attribute value
		return tobeMatched.matchNext((target, parameters) -> {
			return matchTarget(target, tobeMatched.getParameters());
		});
	}
}
