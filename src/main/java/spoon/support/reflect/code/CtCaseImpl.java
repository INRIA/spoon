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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtCaseImpl<E> extends CtStatementImpl implements CtCase<E> {
	private static final long serialVersionUID = 1L;

	CtExpression<E> caseExpression;

	List<CtStatement> statements = EMPTY_LIST();

	public void accept(CtVisitor visitor) {
		visitor.visitCtCase(this);
	}

	public CtExpression<E> getCaseExpression() {
		return caseExpression;
	}

	public List<CtStatement> getStatements() {
		return statements;
	}

	public void setCaseExpression(CtExpression<E> caseExpression) {
		this.caseExpression = caseExpression;
	}

	public void setStatements(List<CtStatement> statements) {
		this.statements = statements;
	}

	@Override
	public void addStatement(CtStatement statement) {
		if (statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			statements = new ArrayList<CtStatement>();
		}
		statements.add(statement);
	}

	@Override
	public void removeStatement(CtStatement statement) {
		if (statements == CtElementImpl.<CtStatement> EMPTY_LIST()) {
			return;
		}
		statements.remove(statement);
	}

	@Override
	public Iterator<CtStatement> iterator() {
		return getStatements().iterator();
	}

	// adds all those elements before the current case
//	@Override
//	public void insertBefore(CtStatementList statements)
//			throws ParentNotInitializedException {
//		
//		throw new SpoonException("");
//	}

}
