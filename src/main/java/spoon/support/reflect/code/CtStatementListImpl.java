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
import java.util.List;

import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.SourcePosition;
import spoon.reflect.visitor.CtVisitor;

public class CtStatementListImpl<R> extends CtCodeElementImpl implements
		CtStatementList<R> {
	private static final long serialVersionUID = 1L;

	List<CtStatement> statements = new ArrayList<CtStatement>();

	public void accept(CtVisitor visitor) {
		visitor.visitCtStatementList(this);
	}

	public List<CtStatement> getStatements() {
		return statements;
	}

	public void setStatements(List<CtStatement> statements) {
		this.statements = statements;
	}

	public R S() {
		return null;
	}

	public CtStatementList getSubstitution(CtSimpleType targetType) {
		return getFactory().Core().clone(this);
	}

	@Override
	public void setPosition(SourcePosition position) {
		for(CtStatement s:statements) {
			s.setPosition(position);
		}
	}
	
}
