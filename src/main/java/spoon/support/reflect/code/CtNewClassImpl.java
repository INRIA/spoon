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

package spoon.support.reflect.code;

import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ChildList;

public class CtNewClassImpl<T> extends
		CtTargetedExpressionImpl<T, CtExpression<?>> implements CtNewClass<T> {
	private static final long serialVersionUID = 1L;

	CtClass<?> annonymousClass;

	List<CtExpression<?>> arguments = new ChildList<CtExpression<?>>(this);

	CtExecutableReference<T> executable;

	String label;

	public void accept(CtVisitor visitor) {
		visitor.visitCtNewClass(this);
	}

	public CtClass<?> getAnonymousClass() {
		return annonymousClass;
	}

	public List<CtExpression<?>> getArguments() {
		return arguments;
	}

	public CtExecutableReference<T> getExecutable() {
		return executable;
	}

	public String getLabel() {
		return label;
	}

	public void insertAfter(CtStatement statement) {
		CtStatementImpl.insertAfter(this, statement);
	}

	public void insertBefore(CtStatement statement) {
		CtStatementImpl.insertBefore(this, statement);
	}

	public void insertAfter(CtStatementList<?> statements) {
		CtStatementImpl.insertAfter(this, statements);
	}

	public void insertBefore(CtStatementList<?> statements) {
		CtStatementImpl.insertBefore(this, statements);
	}

	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList<?>) element);
		} else {
			super.replace(element);
		}
	}

	public void setAnonymousClass(CtClass<?> annonymousClass) {
		this.annonymousClass = annonymousClass;
		annonymousClass.setParent(this);
	}

	public void setArguments(List<CtExpression<?>> arguments) {
		this.arguments = new ChildList<CtExpression<?>>(arguments,this);
	}

	public void setExecutable(CtExecutableReference<T> executable) {
		this.executable = executable;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
