/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ParentNotInitializedException;

/**
 * This filter matches all elements that can be considered as line of code (e.g. directly contained in a block, or a then statement). This discards CtStatement that are not used as statement (such as a method call used as RHS).
 * <pre>
 * // lines of a method
 * List&lt;CtStatement&gt; lines = method.getElements(new LineFilter());
 * // find the parent that is used as a statement (in a block or in a branch)
 * CtStatement parentStatement = element.getParent(new LineFilter());
 * </pre>
 */
public class LineFilter extends TypeFilter<CtStatement> {

	/**
	 * Creates the filter.
	 */
	public LineFilter() {
		super(CtStatement.class);
	}

	@Override
	public boolean matches(CtStatement element) {
		if (!super.matches(element)) {
			return false;
		}
		if (element instanceof CtBlock) {
			return false;
		}
		CtElement parent;
		try {
			parent = element.getParent();
		} catch (ParentNotInitializedException e) {
			return false;
		}
		if (parent instanceof CtStatementList) {
			return true;
		}
		if (parent instanceof CtIf) {
			CtIf anIf = (CtIf) parent;
			return element.equals(anIf.getThenStatement()) || element.equals(anIf.getElseStatement());
		}
		if (parent instanceof CtLoop) {
			CtLoop loop = (CtLoop) parent;
			return loop.getBody().equals(element);
		}
		return false;
	}
}
