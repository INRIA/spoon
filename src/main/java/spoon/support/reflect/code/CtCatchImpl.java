/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtVisitor;

public class CtCatchImpl extends CtCodeElementImpl implements CtCatch {
	private static final long serialVersionUID = 1L;

	CtBlock<?> body;

	CtCatchVariable<? extends Throwable> parameter;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtCatch(this);
	}

	@Override
	public CtBlock<?> getBody() {
		return body;
	}

	@Override
	public CtCatchVariable<? extends Throwable> getParameter() {
		return parameter;
	}

	@Override
	public <T extends CtBodyHolder> T setBody(CtStatement statement) {
		CtBlock<?> body = getFactory().Code().getOrCreateCtBlock(statement);
		if (body != null) {
			body.setParent(this);
		}
		this.body = body;
		return (T) this;
	}

	@Override
	public <T extends CtCatch> T setParameter(CtCatchVariable<? extends Throwable> parameter) {
		if (parameter != null) {
			parameter.setParent(this);
		}
		this.parameter = parameter;
		return (T) this;
	}

	@Override
	public CtCatch clone() {
		return (CtCatch) super.clone();
	}
}
