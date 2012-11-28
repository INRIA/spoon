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
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ChildList;

public class CtForImpl extends CtLoopImpl implements CtFor {
	private static final long serialVersionUID = 1L;

	CtExpression<Boolean> expression;

	List<CtStatement> forInit = new ChildList<CtStatement>(this);

	List<CtStatement> forUpdate = new ChildList<CtStatement>(this);

	public void accept(CtVisitor visitor) {
		visitor.visitCtFor(this);
	}

	public CtExpression<Boolean> getExpression() {
		return expression;
	}

	public List<CtStatement> getForInit() {
		return forInit;
	}

	public List<CtStatement> getForUpdate() {
		return forUpdate;
	}

	public void setExpression(CtExpression<Boolean> expression) {
		this.expression = expression;
		expression.setParent(this);
	}

	public void setForInit(List<CtStatement> forInit) {
		this.forInit = new ChildList<CtStatement>(forInit,this);
	}

	public void setForUpdate(List<CtStatement> forUpdate) {
		this.forUpdate = new ChildList<CtStatement>(forUpdate,this);
	}

}
