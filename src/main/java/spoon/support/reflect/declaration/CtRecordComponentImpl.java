/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import spoon.JLSViolation;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.CtExtendedModifier;

public class CtRecordComponentImpl extends CtNamedElementImpl implements CtRecordComponent {

	private static final Set<String> forbiddenNames = createForbiddenNames();
	@MetamodelPropertyField(role = CtRole.TYPE)
	private CtTypeReference<Object> type;
	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public CtMethod<?> toMethod() {
		CtMethod<?> method = this.getFactory().createMethod();
		method.setSimpleName(getSimpleName());
		method.setType((CtTypeReference) getType());
		method.setExtendedModifiers(Collections.singleton(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
		method.setImplicit(true);
		method.setBody(getFactory().createCodeSnippetStatement("return " + getSimpleName()));
		return method;
	}

	@Override
	public CtField<?> toField() {
		CtField<?> field = this.getFactory().createField();
		field.setSimpleName(getSimpleName());
		field.setType((CtTypeReference) getType());
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		modifiers.add(new CtExtendedModifier(ModifierKind.PRIVATE, true));
		modifiers.add(new CtExtendedModifier(ModifierKind.FINAL, true));
		field.setExtendedModifiers(modifiers);
		field.setImplicit(true);
		return field;
	}

	@Override
	public boolean isImplicit() {
		return true;
	}

	@Override
	public CtTypeReference<Object> getType() {
		return type;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<Object> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.type);
		this.type = type;
		return (C) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtRecordComponent(this);
	}

	@Override
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		checkName(simpleName);
		return super.setSimpleName(simpleName);
	}

	private void checkName(String simpleName) {
		if (forbiddenNames.contains(simpleName)) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, "The name '" + simpleName + "' is not allowed as record component name.");
		}
	}
	private static Set<String> createForbiddenNames() {
		return Set.of("clone", "finalize", "getClass", "notify", "notifyAll", "equals", "hashCode", "toString", "wait");
	}

	@Override
	public CtRecordComponent clone() {
		return (CtRecordComponent) super.clone();
	}



	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return (E) this;
	}
}

