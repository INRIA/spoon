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
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtType;

public abstract class CtLoopImpl extends CtStatementImpl implements CtLoop {
	private static final long serialVersionUID = 1L;

	CtStatement body;

	@Override
	public CtStatement getBody() {
		return body;
	}

	@SuppressWarnings("unchecked")
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
	public CtLoop clone() {
		return (CtLoop) super.clone();
	}

	@Override
	public Void S() {
		return null;
	}

	public CtCodeElement getSubstitution(CtType<?> targetType) {
		return clone();
	}
}
