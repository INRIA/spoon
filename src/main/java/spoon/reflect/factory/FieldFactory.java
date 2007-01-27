/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.factory;

import java.lang.reflect.Field;
import java.util.Set;

import spoon.reflect.Factory;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The {@link CtField} sub-factory.
 */
public class FieldFactory extends SubFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new field sub-factory.
	 * 
	 * @param factory
	 *            the parent factory
	 */
	public FieldFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a field.
	 * 
	 * @param target
	 *            the target type to which the field is added
	 * @param modifiers
	 *            the modifiers
	 * @param type
	 *            the field's type
	 * @param name
	 *            the field's name
	 */
	public <T> CtField<T> create(CtSimpleType<?> target,
			Set<ModifierKind> modifiers, CtTypeReference<T> type, String name) {
		CtField<T> field = factory.Core().createField();
		field.setModifiers(modifiers);
		field.setType(type);
		field.setSimpleName(name);
		field.setParent(target);
		if (target != null)
			target.getFields().add(field);
		return field;
	}

	/**
	 * Creates a field.
	 * 
	 * @param target
	 *            the target type to which the field is added
	 * @param modifiers
	 *            the modifiers
	 * @param type
	 *            the field's type
	 * @param name
	 *            the field's name
	 * @param defaultExpression
	 *            the initializing expression
	 */
	public <T> CtField<T> create(CtSimpleType<?> target,
			Set<ModifierKind> modifiers, CtTypeReference<T> type, String name,
			CtExpression<T> defaultExpression) {
		CtField<T> field = create(target, modifiers, type, name);
		field.setDefaultExpression(defaultExpression);
		return field;
	}

	/**
	 * Creates a field by copying an existing field.
	 * 
	 * @param <T>
	 *            the type of the field
	 * @param target
	 *            the target type where the new field has to be inserted to
	 * @param source
	 *            the source field to be copied
	 * @return the newly created field
	 */
	public <T> CtField<T> create(CtType<?> target, CtField<T> source) {
		CtField<T> newField = factory.Core().clone(source);
		if (target != null)
			target.getFields().add(newField);
		newField.setParent(target);
		return newField;
	}

	/**
	 * Creates a field reference from an existing field.
	 */
	public <T> CtFieldReference<T> createReference(CtField<T> field) {
		return createReference(factory.Type().createReference(
				field.getDeclaringType()), field.getType(), field
				.getSimpleName());
	}

	/**
	 * Creates a field reference.
	 */
	public <T> CtFieldReference<T> createReference(
			CtTypeReference<?> declaringType, CtTypeReference<T> type,
			String fieldName) {
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
		CtFieldReference fieldRef = factory.Core().createFieldReference();
		fieldRef.setSimpleName(field.getName());
		fieldRef.setDeclaringType(factory.Type().createReference(
				field.getDeclaringClass()));
		fieldRef.setType(factory.Type().createReference(field.getType()));
		return fieldRef;
	}

	/**
	 * Creates a field reference from its signature, as defined by the field
	 * reference's toString.
	 */
	public <T> CtFieldReference<T> createReference(String signature) {
		CtFieldReference<T> fieldRef = factory.Core().createFieldReference();
		String type = signature.substring(0, signature.indexOf(" "));
		String declaringType = signature.substring(signature.indexOf(" ") + 1,
				signature.indexOf(CtField.FIELD_SEPARATOR));
		String fieldName = signature.substring(signature
				.indexOf(CtField.FIELD_SEPARATOR) + 1);
		fieldRef.setSimpleName(fieldName);
		fieldRef
				.setDeclaringType(factory.Type().createReference(declaringType));
		CtTypeReference<T> typeRef = factory.Type().createReference(type);
		fieldRef.setType(typeRef);
		return fieldRef;
	}

}
