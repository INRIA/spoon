/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * The {@link CtField} sub-factory.
 */
public class FieldFactory extends SubFactory {

	/**
	 * Creates a new field sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public FieldFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a field.
	 *
	 * @param target
	 * 		the target type to which the field is added
	 * @param modifiers
	 * 		the modifiers
	 * @param type
	 * 		the field's type
	 * @param name
	 * 		the field's name
	 */
	public <T> CtField<T> create(CtType<?> target, Set<ModifierKind> modifiers, CtTypeReference<T> type, String name) {
		CtField<T> field = factory.Core().createField();
		field.setModifiers(modifiers);
		field.setType(type);
		field.setSimpleName(name);
		if (target != null) {
			target.addField(field);
		}
		return field;
	}

	/**
	 * Creates a field.
	 *
	 * @param target
	 * 		the target type to which the field is added
	 * @param modifiers
	 * 		the modifiers
	 * @param type
	 * 		the field's type
	 * @param name
	 * 		the field's name
	 * @param defaultExpression
	 * 		the initializing expression
	 */
	public <T> CtField<T> create(CtType<?> target, Set<ModifierKind> modifiers, CtTypeReference<T> type, String name, CtExpression<T> defaultExpression) {
		CtField<T> field = create(target, modifiers, type, name);
		field.setDefaultExpression(defaultExpression);
		return field;
	}

	/**
	 * Creates a field by copying an existing field.
	 *
	 * @param <T>
	 * 		the type of the field
	 * @param target
	 * 		the target type where the new field has to be inserted to
	 * @param source
	 * 		the source field to be copied
	 * @return the newly created field
	 */
	public <T> CtField<T> create(CtType<?> target, CtField<T> source) {
		CtField<T> newField = source.clone();
		if (target != null) {
			target.addField(newField);
		}
		return newField;
	}

	/**
	 * Creates a field reference from an existing field.
	 */
	public <T> CtFieldReference<T> createReference(CtField<T> field) {
		final CtFieldReference<T> reference = createReference(factory.Type().createReference(field.getDeclaringType()), field.getType().clone(), field.getSimpleName());
		reference.setFinal(field.hasModifier(ModifierKind.FINAL));
		reference.setStatic(field.hasModifier(ModifierKind.STATIC));
		return reference;
	}

	/**
	 * Creates a field reference.
	 */
	public <T> CtFieldReference<T> createReference(CtTypeReference<?> declaringType, CtTypeReference<T> type, String fieldName) {
		CtFieldReference<T> fieldRef = factory.Core().createFieldReference();
		fieldRef.setSimpleName(fieldName);
		fieldRef.setDeclaringType(declaringType);
		fieldRef.setType(type);
		return fieldRef;
	}

	/**
	 * Creates a field reference from a <code>java.lang.reflect</code> field.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtFieldReference<T> createReference(Field field) {
		CtFieldReference<T> fieldRef = factory.Core().createFieldReference();
		fieldRef.setSimpleName(field.getName());
		fieldRef.setDeclaringType(factory.Type().createReference(field.getDeclaringClass()));
		CtTypeReference<T> t = factory.Type().createReference((Class<T>) field.getType());
		fieldRef.setType(t);
		return fieldRef;
	}

	/**
	 * Creates a field reference from its signature, as defined by the field
	 * reference's toString.
	 */
	public <T> CtFieldReference<T> createReference(String signature) {
		CtFieldReference<T> fieldRef = factory.Core().createFieldReference();
		String type = signature.substring(0, signature.indexOf(' '));
		String declaringType = signature.substring(signature.indexOf(' ') + 1, signature.indexOf(CtField.FIELD_SEPARATOR));
		String fieldName = signature.substring(signature.indexOf(CtField.FIELD_SEPARATOR) + 1);
		fieldRef.setSimpleName(fieldName);
		fieldRef.setDeclaringType(factory.Type().createReference(declaringType));
		CtTypeReference<T> typeRef = factory.Type().createReference(type);
		fieldRef.setType(typeRef);
		return fieldRef;
	}

}
