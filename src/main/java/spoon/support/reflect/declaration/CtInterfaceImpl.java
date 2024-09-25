/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.eval.VisitorPartialEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtInterfaceImpl<T> extends CtTypeImpl<T> implements CtInterface<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.PERMITTED_TYPE)
	Set<CtTypeReference<?>> permittedTypes = emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtInterface(this);
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		return getReference().isSubtypeOf(type);
	}

	@Override
	public boolean isInterface() {
		return true;
	}

	@Override
	public Collection<CtExecutableReference<?>> getDeclaredExecutables() {
		Set<CtTypeReference<?>> superInterfaces = getSuperInterfaces();
		if (superInterfaces.isEmpty()) {
			return super.getDeclaredExecutables();
		}
		List<CtExecutableReference<?>> l = new ArrayList<>(super.getDeclaredExecutables());
		for (CtTypeReference<?> sup : superInterfaces) {
			l.addAll(sup.getAllExecutables());
		}
		return Collections.unmodifiableList(l);
	}

	@Override
	public <R extends CtCodeElement> R partiallyEvaluate() {
		VisitorPartialEvaluator eval = new VisitorPartialEvaluator();
		return eval.evaluate((R) this);
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatement statement) throws ParentNotInitializedException {
		CtStatementImpl.insertAfter(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertAfter(CtStatementList statements) throws ParentNotInitializedException {
		CtStatementImpl.insertAfter(this, statements);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatement statement) throws ParentNotInitializedException {
		CtStatementImpl.insertBefore(this, statement);
		return (C) this;
	}

	@Override
	public <C extends CtStatement> C insertBefore(CtStatementList statements) throws ParentNotInitializedException {
		CtStatementImpl.insertBefore(this, statements);
		return (C) this;
	}

	@Override
	@UnsettableProperty
	public <C extends CtStatement> C setLabel(String label) {
		return (C) this;
	}

	@Override
	public CtInterface<T> clone() {
		return (CtInterface<T>) super.clone();
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		// unsettable property
		return (C) this;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Set<CtTypeReference<?>> getPermittedTypes() {
		return Collections.unmodifiableSet(permittedTypes);
	}

	@Override
	public CtInterface<T> setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes) {
		Collection<CtTypeReference<?>> types = permittedTypes != null ? permittedTypes : CtElementImpl.emptySet();
		getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(this, CtRole.PERMITTED_TYPE, this.permittedTypes, new LinkedHashSet<>(this.permittedTypes));
		this.permittedTypes.clear();
		for (CtTypeReference<?> type : types) {
			addPermittedType(type);
		}
		return this;
	}

	@Override
	public CtInterface<T> addPermittedType(CtTypeReference<?> type) {
		if (type == null) {
			return this;
		}
		if (this.permittedTypes == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			this.permittedTypes = new LinkedHashSet<>();
		}
		type.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, CtRole.PERMITTED_TYPE, this.permittedTypes, type);
		this.permittedTypes.add(type);
		return this;
	}

	@Override
	public CtInterface<T> removePermittedType(CtTypeReference<?> type) {
		if (this.permittedTypes == CtElementImpl.<CtTypeReference<?>>emptySet()) {
			return this;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, CtRole.PERMITTED_TYPE, this.permittedTypes, type);
		this.permittedTypes.remove(type);
		return this;
	}

	@Override
	public <N, C extends CtType<T>> C addNestedType(CtType<N> nestedType) {
		super.addNestedType(nestedType);

		if (nestedType == null) {
			return (C) this;
		}

		// Type members of interfaces are implicitly public static. We need to add the implicit
		// modifiers if they aren't public static already.
		Set<CtExtendedModifier> modifiers = new HashSet<>(nestedType.getExtendedModifiers());
		if (!nestedType.isPublic()) {
			modifiers.add(CtExtendedModifier.implicit(ModifierKind.PUBLIC));
		}
		if (!nestedType.isStatic()) {
			modifiers.add(CtExtendedModifier.implicit(ModifierKind.STATIC));
		}
		nestedType.setExtendedModifiers(modifiers);

		return (C) this;
	}

	@Override
	public <N> boolean removeNestedType(CtType<N> nestedType) {
		if (!super.removeNestedType(nestedType)) {
			return false;
		}

		// We might have added implicit public static modifiers so we need to remove them again
		Set<ModifierKind> addedKinds = EnumSet.of(ModifierKind.STATIC, ModifierKind.PUBLIC);
		Set<CtExtendedModifier> newModifiers = new HashSet<>(nestedType.getExtendedModifiers());
		newModifiers.removeIf(
				modifier -> modifier.isImplicit() && addedKinds.contains(modifier.getKind())
		);

		nestedType.setExtendedModifiers(newModifiers);

		return true;
	}
}
