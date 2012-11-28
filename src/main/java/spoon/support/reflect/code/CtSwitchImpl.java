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

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.ChildList;

public class CtSwitchImpl<S> extends CtStatementImpl implements CtSwitch<S> {
	private static final long serialVersionUID = 1L;

	List<CtCase<? super S>> cases = new ChildList<CtCase<? super S>>(this);

	CtExpression<S> expression;

	public void accept(CtVisitor visitor) {
		visitor.visitCtSwitch(this);
	}

	public List<CtCase<? super S>> getCases() {
		return cases;
	}

	public CtExpression<S> getSelector() {
		return expression;
	}

	public void setCases(List<CtCase<? super S>> cases) {
		this.cases = new ChildList<CtCase<? super S>>(cases,this);
	}

	public void setSelector(CtExpression<S> selector) {
		this.expression = selector;
		expression.setParent(this);
	}

}
