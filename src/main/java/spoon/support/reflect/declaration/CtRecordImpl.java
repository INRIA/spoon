/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import spoon.JLSViolation;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;

public class CtRecordImpl extends CtClassImpl<Object> implements CtRecord {
	private static final String ABSTRACT_MODIFIER_ERROR =
			"Abstract modifier is not allowed on record";
	@MetamodelPropertyField(role = CtRole.RECORD_COMPONENT)
	private Set<CtRecordComponent> components = new LinkedHashSet<>();

	@Override
	@DerivedProperty
	public CtTypeReference<?> getSuperclass() {
		return getFactory().Type().createReference("java.lang.Record");
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<Object>> C setSuperclass(CtTypeReference<?> superClass) {
		return (C) this;
	}

	@Override
	public CtRecord addRecordComponent(CtRecordComponent component) {
		if (component == null) {
			return this;
		}
		component.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, CtRole.RECORD_COMPONENT, components, component);
		components.add(component);

		if (getField(component.getSimpleName()) == null) {
			addField(component.toField());
		}
		if (!hasMethodWithSameNameAndNoParameter(component)) {
			addMethod(component.toMethod());
		}
		return this;
	}

	@Override
	public CtRecord removeRecordComponent(CtRecordComponent component) {
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, CtRole.RECORD_COMPONENT, components, component);
		components.remove(component);
		if (getField(component.getSimpleName()) != null
				&& getField(component.getSimpleName()).isImplicit()) {
			typeMembers.remove(component.toField());
		}
		if (getMethods().contains(component.toMethod())) {
			typeMembers.remove(component.toMethod());
		}
		return this;
	}

	@Override
	public Set<CtRecordComponent> getRecordComponents() {
		return Collections.unmodifiableSet(components);
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtRecord(this);
	}

	@Override
	public <C extends CtType<Object>> C addTypeMemberAt(int position, CtTypeMember member) {
		// a record can have only implicit instance fields and this is the best point to preserve the invariant
		// because there are multiple ways to add a field to a record
		String memberName = member.getSimpleName();

		if (member instanceof CtField && !member.isStatic()) {
			member.setImplicit(true);
			getAnnotationsWithName(memberName, ElementType.FIELD).forEach(member::addAnnotation);
		}
		if (member instanceof CtMethod && member.isImplicit()) {
			getAnnotationsWithName(memberName, ElementType.METHOD).forEach(member::addAnnotation);
		}
		if (member instanceof CtConstructor && member.isImplicit()) {
			for (CtParameter<?> parameter : ((CtConstructor<?>) member).getParameters()) {
				getAnnotationsWithName(parameter.getSimpleName(), ElementType.PARAMETER).forEach(parameter::addAnnotation);
			}

		}
		if (member instanceof CtMethod && (member.isAbstract() || member.isNative())) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, String.format("%s method is native or abstract, both is not allowed",
				memberName));
		}
		if (member instanceof CtAnonymousExecutable && !member.isStatic()) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, "Instance initializer is not allowed in a record (JLS 17 $8.10.2)");
		}
		return super.addTypeMemberAt(position, member);
	}

	private List<CtAnnotation<?>> getAnnotationsWithName(String name, ElementType elementType) {
		List<CtAnnotation<?>> result = new ArrayList<>();
		for (CtRecordComponent component : components) {
			if (component.getSimpleName().equals(name)) {
				for (CtAnnotation<? extends Annotation> annotation : component.getAnnotations()) {
					CtType<?> annotationType = annotation.getAnnotationType().getTypeDeclaration();
					// TODO: this is not the best way to handle this, but it's the best we can do for now
					if (annotationType != null) {
						Target target = annotationType.getAnnotation(Target.class);
						// https://docs.oracle.com/javase/specs/jls/se19/html/jls-9.html#jls-9.6.4.1
						// If an annotation of type java.lang.annotation.Target is not present on the declaration of
						// an annotation interface A, then A is applicable in all declaration contexts and in no
						// type contexts.
						if (target == null && elementType == ElementType.TYPE_USE) {
							continue;
						}
						if (target == null || Arrays.stream(target.value()).anyMatch(e -> e == elementType)) {
							result.add(annotation.clone());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public <C extends CtType<Object>> C setFields(List<CtField<?>> fields) {
		super.setFields(fields);
		for (CtRecordComponent component : components) {
			if (getField(component.getSimpleName()) == null) {
				addField(component.toField());
			}
		}
		return (C) this;
	}

	@Override
	public <C extends CtType<Object>> C setMethods(Set<CtMethod<?>> methods) {
		super.setMethods(methods);
		for (CtRecordComponent component : components) {
			if (!hasMethodWithSameNameAndNoParameter(component)) {
				addMethod(component.toMethod());
			}
		}
		return (C) this;
	}

	private boolean hasMethodWithSameNameAndNoParameter(CtRecordComponent component) {
		for (CtMethod<?> method : getMethodsByName(component.getSimpleName())) {
			if (method.getParameters().isEmpty()) {
				return true;
			}
		}
		return false;
	}


	@Override
	public <C extends CtType<Object>> C setTypeMembers(List<CtTypeMember> members) {
		super.setTypeMembers(members);
		for (CtRecordComponent component : components) {
			if (hasMethodWithSameNameAndNoParameter(component)) {
				addMethod(component.toMethod());
			}
			if (getField(component.getSimpleName()) == null) {
				addField(component.toField());
			}
		}
		return (C) this;
	}

	@Override
	public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
		if (modifier.equals(ModifierKind.ABSTRACT)) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, ABSTRACT_MODIFIER_ERROR);
		}
		return super.addModifier(modifier);
	}

	@Override
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		if (modifiers.contains(ModifierKind.ABSTRACT)) {
			JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, ABSTRACT_MODIFIER_ERROR);
		}
		return super.setModifiers(modifiers);
	}

	@Override
	public <C extends CtModifiable> C setExtendedModifiers(
			Set<CtExtendedModifier> extendedModifiers) {
		checkIfAbstractModifier(extendedModifiers);
		return super.setExtendedModifiers(extendedModifiers);
	}

	private boolean isAbstract(CtExtendedModifier v) {
		return v.getKind().equals(ModifierKind.ABSTRACT);
	}

	private void checkIfAbstractModifier(Set<CtExtendedModifier> extendedModifiers) {
		for (CtExtendedModifier extendedModifier : extendedModifiers) {
			if (isAbstract(extendedModifier)) {
				JLSViolation.throwIfSyntaxErrorsAreNotIgnored(this, ABSTRACT_MODIFIER_ERROR);
			}
		}
	}

	@Override
	public CtRecord setRecordComponents(Set<CtRecordComponent> components) {
		getRecordComponents().forEach(this::removeRecordComponent);
		components.forEach(this::addRecordComponent);
		return this;
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		Set<CtExtendedModifier> extendedModifiers = new HashSet<>(getExtendedModifiers());
		if (parent instanceof CtType) {
			extendedModifiers.add(CtExtendedModifier.implicit(ModifierKind.STATIC));
		} else {
			extendedModifiers.remove(CtExtendedModifier.implicit(ModifierKind.STATIC));
		}
		setExtendedModifiers(extendedModifiers);
		return super.setParent(parent);
	}

	@Override
	public Set<CtTypeReference<?>> getPermittedTypes() {
		return Set.of();
	}

	@Override
	@UnsettableProperty
	public CtRecord setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes) {
		return this;
	}

	@Override
	@UnsettableProperty
	public CtRecord addPermittedType(CtTypeReference<?> type) {
		return this;
	}

	@Override
	@UnsettableProperty
	public CtRecord removePermittedType(CtTypeReference<?> type) {
		return this;
	}

	@Override
	public CtRecord clone() {
		return (CtRecord) super.clone();
	}
}
