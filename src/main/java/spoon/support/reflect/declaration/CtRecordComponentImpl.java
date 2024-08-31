/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import spoon.JLSViolation;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
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
		method.setType(getClonedType());
		method.setExtendedModifiers(Collections.singleton(new CtExtendedModifier(ModifierKind.PUBLIC, true)));

		CtFieldAccess<?> ctVariableAccess = (CtFieldAccess<?>) getFactory().Code()
			.createVariableRead(getRecordFieldReference(), false);

		method.setBody(getFactory().Code().createCtReturn(ctVariableAccess));

		return makeTreeImplicit(method);
	}

	private CtFieldReference<?> getRecordFieldReference() {
		CtRecord parent = isParentInitialized() ? (CtRecord) getParent() : null;

		// Reference the field we think should exist. It might be added to the record later on, so do not directly
		// query for it.
		CtFieldReference<?> reference = getFactory().createFieldReference()
			.setFinal(true)
			.setStatic(false)
			.setType(getClonedType())
			.setSimpleName(getSimpleName());

		// We have a parent record, make the field refer to it. Ideally we could do this all the time, but if we
		// do not yet have a parent that doesn't work.
		if (parent != null) {
			reference.setDeclaringType(parent.getReference());
		}

		return reference;
	}

	@Override
	public CtField<?> toField() {
		CtField<?> field = this.getFactory().createField();
		field.setSimpleName(getSimpleName());
		field.setType(getClonedType());
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		modifiers.add(new CtExtendedModifier(ModifierKind.PRIVATE, true));
		modifiers.add(new CtExtendedModifier(ModifierKind.FINAL, true));
		field.setExtendedModifiers(modifiers);
		return makeTreeImplicit(field);
	}

	@Override
	public boolean isImplicit() {
		return true;
	}

	private @Nullable CtTypeReference<?> getClonedType() {
		return getType() != null ? getType().clone() : null;
	}

	@Override
	public CtTypeReference<Object> getType() {
		return type;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference type) {
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

	private static <T extends CtElement> T makeTreeImplicit(T element) {
		element.accept(new CtScanner() {
			@Override
			protected void enter(CtElement e) {
				e.setImplicit(true);
			}
		});
		return element;
	}
}
