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

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

public class MethodBuilder<P extends AbsBuilder<?, ?, ?>> extends
		AbsBuilder<CtMethod, MethodBuilder<P>, P> {

	public MethodBuilder(Factory factory, String name) {
		super(factory, factory.Core().createMethod());
		getElement().setBody(factory.Core().createBlock());
		getElement().setSimpleName(name);
		setPrivate();
		type(getFactory().Type().VOID_PRIMITIVE);
	}

	public MethodBuilder<P> type(CtTypeReference ref) {
		getElement().setType(ref);
		return this;
	}

	public MethodBuilder<P> type(Class clazz) {
		getElement().setType(getFactory().Code().createCtTypeReference(clazz));
		return this;
	}

	public MethodBuilder<P> setPublic() {
		getElement().addModifier(ModifierKind.PUBLIC);

		getElement().removeModifier(ModifierKind.PRIVATE);
		getElement().removeModifier(ModifierKind.PROTECTED);
		return this;
	}

	public MethodBuilder<P> setPrivate() {
		getElement().addModifier(ModifierKind.PRIVATE);

		getElement().removeModifier(ModifierKind.PUBLIC);
		getElement().removeModifier(ModifierKind.PROTECTED);
		return this;
	}

	public MethodBuilder<P> setProtected() {
		getElement().addModifier(ModifierKind.PROTECTED);

		getElement().removeModifier(ModifierKind.PRIVATE);
		getElement().removeModifier(ModifierKind.PUBLIC);
		return this;
	}

	public MethodBuilder<P> setAbstract() {
		getElement().addModifier(ModifierKind.ABSTRACT);
		getElement().setBody(null);
		return this;
	}

	public MethodBuilder<P> setStatic() {
		getElement().addModifier(ModifierKind.STATIC);
		return this;
	}

	public MethodBuilder<P> setFinal() {
		getElement().addModifier(ModifierKind.FINAL);
		return this;
	}

	public MethodBuilder<P> addParam(Class clazz, String name) {
		CtParameter parameter = getFactory().Core().createParameter();
		parameter.setType(getFactory().Code().createCtTypeReference(clazz));
		parameter.setSimpleName(name);
		getElement().addParameter(parameter);
		return this;
	}

	public MethodBuilder<P> inBody(AbsBuilder<? extends CtStatement, ?, ?> e) {
		return inBody(e.build());
	}

	public MethodBuilder<P> inBody(CtStatement e) {
		getElement().getBody().addStatement(e);
		return this;
	}
}
