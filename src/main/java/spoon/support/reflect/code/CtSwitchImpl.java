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
package spoon.support.reflect.code;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import static spoon.reflect.ModelElementContainerDefaultCapacities.SWITCH_CASES_CONTAINER_DEFAULT_CAPACITY;

public class CtSwitchImpl<S> extends CtStatementImpl implements CtSwitch<S> {
	private static final long serialVersionUID = 1L;

	List<CtCase<? super S>> cases = emptyList();

	CtExpression<S> expression;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtSwitch(this);
	}

	@Override
	public List<CtCase<? super S>> getCases() {
		return cases;
	}

	@Override
	public CtExpression<S> getSelector() {
		return expression;
	}

	@Override
	public <T extends CtSwitch<S>> T setCases(List<CtCase<? super S>> cases) {
		this.cases.clear();
		for (CtCase<? super S> aCase : cases) {
			addCase(aCase);
		}
		return (T) this;
	}

	@Override
	public <T extends CtSwitch<S>> T setSelector(CtExpression<S> selector) {
		selector.setParent(this);
		this.expression = selector;
		return (T) this;
	}

	@Override
	public <T extends CtSwitch<S>> T addCase(CtCase<? super S> c) {
		if (cases == CtElementImpl.<CtCase<? super S>>emptyList()) {
			cases = new ArrayList<CtCase<? super S>>(SWITCH_CASES_CONTAINER_DEFAULT_CAPACITY);
		}
		c.setParent(this);
		cases.add(c);
		return (T) this;
	}

	@Override
	public boolean removeCase(CtCase<? super S> c) {
		return cases != CtElementImpl.<CtCase<? super S>>emptyList() && cases.remove(c);
	}

}
