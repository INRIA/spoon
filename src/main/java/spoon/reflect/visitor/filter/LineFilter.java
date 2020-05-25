/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
			CtStatement body = loop.getBody();
			if (body == null) {
				return false;
			}
			return body.equals(element);
		}
		return false;
	}
}
