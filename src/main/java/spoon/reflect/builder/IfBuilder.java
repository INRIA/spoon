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
package spoon.reflect.builder;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.factory.Factory;

public class IfBuilder<P extends AbsBuilder<CtIf, ?, ?>> extends
		AbsBuilder<CtIf, IfBuilder<P>, P> {

	public IfBuilder(Factory factory, CtExpression e) {
		super(factory, factory.Core().createIf());
		getElement().setCondition(e);
	}

	public IfBuilder<P> inThen(CtStatement ... statements) {
		for (int i = 0; i < statements.length; i++) {
			CtStatement statement = statements[i];
			inThen(statement);
		}
		return this;
	}

	public IfBuilder<P> inThen(AbsBuilder<? extends CtStatement, ?, ?>... statements) {
		for (int i = 0; i < statements.length; i++) {
			inThen(statements[i]);
		}
		return this;
	}

	public IfBuilder<P> inThen(CtStatement s) {
		if (getElement().getThenStatement() == null) {
			getElement().setThenStatement(getFactory().Core().createBlock());
		}
		((CtBlock) getElement().getThenStatement()).addStatement(s);
		return this;
	}


	public IfBuilder<P> inThen(AbsBuilder<? extends CtStatement, ?, ?> b) {
		return inThen(b.build());
	}


	public IfBuilder<P> inElse(CtStatement ... statements) {
		for (int i = 0; i < statements.length; i++) {
			CtStatement statement = statements[i];
			inElse(statement);
		}
		return this;
	}

	public IfBuilder<P> inElse(AbsBuilder<? extends CtStatement, ?, ?>... statements) {
		for (int i = 0; i < statements.length; i++) {
			inElse(statements[i]);
		}
		return this;
	}

	public IfBuilder<P> inElse(CtStatement s) {
		if (getElement().getElseStatement() == null) {
			getElement().setElseStatement(getFactory().Core().createBlock());
		}
		((CtBlock) getElement().getElseStatement()).addStatement(s);
		return this;
	}


	public IfBuilder<P> inElse(AbsBuilder<? extends CtStatement, ?, ?> b) {
		return inElse(b.build());
	}
}
