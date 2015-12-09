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
package spoon.reflect.visitor.filter;

import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.visitor.Filter;

/**
 * This simple filter matches all the occurrences of a return or a throw
 * statement (end of execution flow).
 */
public class ReturnOrThrowFilter implements Filter<CtCFlowBreak> {

	@Override
	public boolean matches(CtCFlowBreak cflowBreak) {
		return (cflowBreak instanceof CtReturn)
				|| (cflowBreak instanceof CtThrow);
	}
}
