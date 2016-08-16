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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;

public class CatchBuilder<P  extends AbsBuilder<? extends CtElement, ? extends AbsBuilder, ? extends AbsBuilder>> extends
		AbsBuilder<CtCatch, CatchBuilder<P>, P> {

	public CatchBuilder(Factory factory) {
		super(factory, factory.Core().createCatch());
		getElement().setBody(getFactory().Core().createBlock());
	}

	public CatchBuilder<P> parameter(String variableName, Class<?>...clazz) {
		CtTypeReference ref;
		if (clazz.length == 1) {
			ref = getFactory().Code().createCtTypeReference(clazz[0]);
		} else if (clazz.length > 1) {
			CtIntersectionTypeReference interRef = getFactory().Core().createIntersectionTypeReference();
			for (int i = 0; i < clazz.length; i++) {
				Class aClass = clazz[i];
				interRef.addBound(getFactory().Code().createCtTypeReference(aClass));
			}
			ref = interRef;
		} else {
			throw new IllegalArgumentException("No specified type");
		}
		getElement().setParameter(getFactory().Code().createCatchVariable(
				ref,
				variableName));
		return this;
	}

	public CatchBuilder<P> inBody(CtStatement... statements) {
		if (statements.length == 1) {
			getElement().getBody().addStatement(statements[0]);
			return this;
		}
		for (int i = 0; i < statements.length; i++) {
			inBody(statements[i]);
		}
		return this;
	}

	public CatchBuilder<P> inBody(AbsBuilder<? extends CtStatement, ?, ?>... statements) {
		for (int i = 0; i < statements.length; i++) {
			inBody(statements[i].build());
		}
		return this;
	}
}
