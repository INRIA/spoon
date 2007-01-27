/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.template;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtSimpleType;

/**
 * This class represents a template parameter that defines a statement list
 * directly expressed in Java (no returns).
 * 
 * <p>
 * To define a new statement list template parameter, you must subclass this
 * class and implement the {@link #statements()} method, which actually defines
 * the Java statements. It corresponds to a
 * {@link spoon.reflect.code.CtStatementList}.
 */
public abstract class StatementListTemplateParameter implements
		TemplateParameterList<Void> {

	/**
	 * Creates a new statement list template parameter.
	 */
	public StatementListTemplateParameter() {
	}

	@SuppressWarnings("unchecked")
	public CtStatementList<Void> getSubstitution(CtSimpleType<?> targetType) {
		CtClass<?> c;
		CtBlock<?> b;
		c = targetType.getFactory().Template().get(this.getClass());
		if (c == null) {
			c = targetType.getFactory().Class().get(this.getClass());
		}
		CtStatementList<Void> l = targetType.getFactory().Core()
				.createStatementList();
		if (this instanceof Template) {
			b = Substitution.substitute(targetType, (Template) this, c
					.getMethod("statements").getBody());
		} else {
			b = targetType.getFactory().Core().clone(
					c.getMethod("statements").getBody());
		}
		l.setStatements(b.getStatements());
		return l;
	}

	public Void S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template statement list.
	 */
	public abstract void statements() throws Throwable;
}
