package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import spoon.SpoonException;
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
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;

public class CtRecordImpl<T> extends CtClassImpl<T> implements CtRecord<T> {
	private static final String ABSTRACT_MODIFIER_ERROR =
			"Abstract modifier is not allowed on record";
	private Set<CtRecordComponent<?>> components = new HashSet<>();

	@Override
	@DerivedProperty
	public CtTypeReference<?> getSuperclass() {
		return getFactory().Type().createReference("java.lang.Record");
	}

	@Override
	@UnsettableProperty
	public <C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass) {
		return (C) this;
	}

	@Override
	public <C> CtRecord<T> addRecordComponent(CtRecordComponent<C> component) {
		components.add(component);
		if (!getMethods().contains(component.toMethod())) {
			addMethod(component.toMethod());
		}
		return this;
	}

	@Override
	public <C> CtRecord<T> removeRecordComponent(CtRecordComponent<C> component) {
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
	public Set<CtRecordComponent<?>> getRecordComponents() {
		return Collections.unmodifiableSet(components);
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtRecord(this);
	}

	@Override
	public <C extends CtType<T>> C addTypeMemberAt(int position, CtTypeMember member) {
		// a record can have only implicit instance fields and this is the best point to preserve the invariant
		// because there are multiple ways to add a field to a record
		if (member instanceof CtField && !member.isStatic()) {
			member.setImplicit(true);
			getAnnotationsWithName(member.getSimpleName(), ElementType.FIELD).forEach(member::addAnnotation);
		}
		if (member instanceof CtMethod && member.isImplicit()) {
			getAnnotationsWithName(member.getSimpleName(), ElementType.METHOD).forEach(member::addAnnotation);
		}
		if (member instanceof CtConstructor && member.isImplicit()) {
			for (CtParameter<?> parameter : ((CtConstructor<?>) member).getParameters()) {
				getAnnotationsWithName(parameter.getSimpleName(), ElementType.PARAMETER).forEach(parameter::addAnnotation);
			}

		}
		if (member instanceof CtMethod && (member.isAbstract() || member.isNative())) {
			throw new SpoonException(String.format("%s method is native or abstract, both is not allowed",
					member.getSimpleName()));
		}
		if (member instanceof CtAnonymousExecutable) {
			throw new SpoonException("Anonymous executable is not allowed in a record");
		}
		return super.addTypeMemberAt(position, member);
	}

	private List<CtAnnotation<?>> getAnnotationsWithName(String name, ElementType elementType) {
		List<CtAnnotation<?>> result = new ArrayList<>();
		for (CtRecordComponent<?> component : components) {
			if (component.getSimpleName().equals(name)) {
				for (CtAnnotation<? extends Annotation> annotation : component.getAnnotations()) {
					CtType<?> annotationType = annotation.getAnnotationType().getTypeDeclaration();
					// TODO: this is not the best way to handle this, but it's the best we can do for now
					if (annotationType != null) {
						Target target = annotationType.getAnnotation(Target.class);
						if (Arrays.stream(target.value()).anyMatch(e -> e == elementType)) {
							result.add(annotation.clone());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public <C extends CtType<T>> C setFields(List<CtField<?>> fields) {
		super.setFields(fields);
		for (CtRecordComponent<?> component : components) {
			if (getField(component.getSimpleName()) == null) {
				addField(component.toField());
			}
		}
		return (C) this;
	}

	@Override
	public <C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods) {
		super.setMethods(methods);
		for (CtRecordComponent<?> component : components) {
			if (!hasMethodWithSameNameAndNoParameter(component)) {
				addMethod(component.toMethod());
			}
		}
		return (C) this;
	}

	private boolean hasMethodWithSameNameAndNoParameter(CtRecordComponent<?> component) {
		for (CtMethod<?> method : getMethodsByName(component.getSimpleName())) {
			if (method.getParameters().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private CtMethod<?> getMethodWithSameNameAndNoParameter(CtRecordComponent<?> component) {
		for (CtMethod<?> method : getMethodsByName(component.getSimpleName())) {
			if (method.getParameters().isEmpty()) {
				return method;
			}
		}
		return null;
	}

	@Override
	public <C extends CtType<T>> C setTypeMembers(List<CtTypeMember> members) {
		super.setTypeMembers(members);
		for (CtRecordComponent<?> component : components) {
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
			throw new SpoonException(ABSTRACT_MODIFIER_ERROR);
		}
		return super.addModifier(modifier);
	}

	@Override
	public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
		if (modifiers.contains(ModifierKind.ABSTRACT)) {
			throw new SpoonException(ABSTRACT_MODIFIER_ERROR);
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
				throw new SpoonException(ABSTRACT_MODIFIER_ERROR);
			}
		}
	}

	@Override
	public CtRecord<T> setRecordComponents(Set<CtRecordComponent<?>> components) {
		getRecordComponents().forEach(this::removeRecordComponent);
		components.forEach(this::addRecordComponent);
		return this;
	}

	@Override
	public <E extends CtElement> E setParent(CtElement parent) {
		Set<CtExtendedModifier> extendedModifiers = new HashSet<>(getExtendedModifiers());
		if (parent instanceof CtType) {
			extendedModifiers.add(new CtExtendedModifier(ModifierKind.STATIC, true));
		} else {
			extendedModifiers.remove(new CtExtendedModifier(ModifierKind.STATIC, true));
		}
		return super.setParent(parent);
	}

}
