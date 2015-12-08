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
package spoon.reflect.path.impl;

import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.path.CtPathRole;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A CtPathElement that define some roles for matching.
 * <p>
 * Differents roles are define :
 * <ul>
 * <li>statement: match on all statements define in the body of an executable</li>
 * <li>parameter: match on parameter of an executable</li>
 * <li>defaultValue: for value of ctFields</li>
 * </ul>
 */
public class CtRolePathElement extends AbstractPathElement<CtElement, CtElement> {

	public static final String STRING = "#";

	private class RoleVisitor extends CtInheritanceScanner {
		private Collection<CtElement> matchs = new LinkedList<CtElement>();

		private RoleVisitor() {
		}

		@Override
		public <R> void scanCtExecutable(CtExecutable<R> e) {
			super.scanCtExecutable(e);

			switch (role) {
			case BODY:
				if (e.getBody() != null) {
					if (getArguments().containsKey("index")
							&& e.getBody()
								.getStatements()
								.size() > Integer.parseInt(
							getArguments().get("index"))) {
						matchs.add(e.getBody().getStatements().get(Integer
								.parseInt(getArguments().get("index"))));
					} else {
						matchs.addAll(e.getBody().getStatements());
					}
				}
			}

		}

		@Override
		public <T> void visitCtField(CtField<T> e) {
			super.visitCtField(e);

			if (role == CtPathRole.DEFAULT_VALUE && e.getDefaultExpression() != null) {
				matchs.add(e.getDefaultExpression());
			}
		}

		@Override
		public void visitCtIf(CtIf e) {
			super.visitCtIf(e);

			switch (role) {
			case THEN:
				if (e.getThenStatement() != null) {
					matchs.add(e.getThenStatement());
				}
			case ELSE:
				if (e.getElseStatement() != null) {
					matchs.add(e.getElseStatement());
				}
			}
		}
	}

	private final CtPathRole role;

	public CtRolePathElement(CtPathRole role) {
		this.role = role;
	}

	public CtPathRole getRole() {
		return role;
	}

	@Override
	public String toString() {
		return STRING + role.toString() + getParamString();
	}

	@Override
	public Collection<CtElement> getElements(Collection<CtElement> roots) {
		RoleVisitor visitor = new RoleVisitor();
		visitor.scan(roots);
		return visitor.matchs;
	}

}
