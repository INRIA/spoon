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

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.factory.Factory;

public class TryBuilder<P extends AbsBuilder<?, ?, ?>> extends
		AbsBuilder<CtTry, TryBuilder<P>, P> {

	public TryBuilder(Factory factory) {
		super(factory, factory.Core().createTry());
		getElement().setBody(getFactory().Core().createBlock());
	}

	public TryBuilder<P> inBody(CtStatement... statements) {
		if (statements.length == 1) {
			getElement().getBody().addStatement(statements[0]);
			return this;
		}
		for (int i = 0; i < statements.length; i++) {
			inBody(statements[i]);
		}
		return this;
	}

	public TryBuilder<P> inBody(AbsBuilder<? extends CtStatement, ?, ?>... statements) {
		for (int i = 0; i < statements.length; i++) {
			inBody(statements[i].build());
		}
		return this;
	}

	public TryBuilder<P> inFinally(CtStatement... statements) {
		if (statements.length == 1) {
			if (getElement().getFinalizer() == null) {
				getElement().setFinalizer(getFactory().Core().createBlock());
			}
			getElement().getFinalizer().addStatement(statements[0]);
			return this;
		}
		for (int i = 0; i < statements.length; i++) {
			inFinally(statements[i]);
		}
		return this;
	}

	public TryBuilder<P> inFinally(AbsBuilder<? extends CtStatement, ?, ?>... statements) {
		for (int i = 0; i < statements.length; i++) {
			inFinally(statements[i].build());
		}
		return this;
	}

	public CatchBuilder<TryBuilder<P>> createCatch() {
		CatchBuilder<TryBuilder<P>> aCatch = new CatchBuilder<>(getFactory());
		aCatch.setParent(this);
		return aCatch;
	}


	@Override
	public void end(AbsBuilder child) {
		if (child instanceof CatchBuilder) {
			getElement().addCatcher((CtCatch) child.build());
		}
	}
}
