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

import spoon.reflect.code.CtStatement;
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
public abstract class StatementTemplate implements TemplateParameter<Void>,
		Template {

	/**
	 * Creates a new statement list template parameter.
	 */
	public StatementTemplate() {
	}

	public CtStatement getSubstitution(CtSimpleType<?> targetType) {
		CtClass<?> c;
		c = targetType.getFactory().Class().get(this.getClass());
		if (c == null) {
			c = targetType.getFactory().Class().get(this.getClass());
		}
		if (this instanceof Template) {
			return Substitution.substitute(targetType, this,
					c.getMethod("statement").getBody().getStatements().get(0));
		} else {
			return targetType
					.getFactory()
					.Core()
					.clone(c.getMethod("statement").getBody().getStatements()
							.get(0));
		}
	}

	public Void S() {
		return null;
	}

	/**
	 * This method must be implemented to define the template statement list.
	 */
	public abstract void statement() throws Throwable;
}
