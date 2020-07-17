/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
package spoon.test.api;

import spoon.reflect.code.CtForEach;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtScanner;
import spoon.support.reflect.code.CtForEachImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class enables to reason on the Spoon metamodel directly
 */
public class Metamodel {
	private Metamodel() { }


	public static Collection<Type> getAllMetamodelTypes() {
		return typesByName.values();
	}

	public static Type getMetamodelTypeByClass(Class<? extends CtElement> clazz) {
		return typesByClass.get(clazz);
	}

	/**
	 * Describes a Spoon metamodel type
	 */
	public static class Type {
		/**
		 * Name of the type
		 */
		private final String name;

		/**
		 * The {@link CtClass} linked to this {@link MetamodelConcept}. Is null in case of class without interface
		 */
		private final Class<? extends CtElement> modelClass;
		/**
		 * The {@link CtInterface} linked to this {@link MetamodelConcept}. Is null in case of interface without class
		 */
		private final Class<? extends CtElement> modelInterface;

		private final List<Field> fields;
		private final Map<CtRole, Field> fieldsByRole;

		private Type(String name, Class<? extends CtElement> modelInterface, Class<? extends CtElement> modelClass, Consumer<FieldMaker> fieldsCreator) {
			this.name = name;
			this.modelClass = modelClass;
			this.modelInterface = modelInterface;
			List<Field> fields = new ArrayList<>();
			this.fields = Collections.unmodifiableList(fields);
			fieldsCreator.accept(new FieldMaker() {
				@Override
				public FieldMaker field(CtRole role, boolean derived, boolean unsettable) {
					fields.add(new Field(Type.this, role, derived, unsettable));
					return this;
				}
			});
			Map<CtRole, Field> fieldsByRole = new LinkedHashMap<>(fields.size());
			fields.forEach(f -> fieldsByRole.put(f.getRole(), f));
			this.fieldsByRole = Collections.unmodifiableMap(fieldsByRole);
		}

		/**
		 * @return interface name of Spoon model type. For example CtClass, CtForEach, ...
		 * It is never followed by xxxImpl
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return {@link Class} which implements this type. For example {@link CtForEachImpl}
		 */
		public Class<? extends CtElement> getModelClass() {
			return modelClass;
		}
		/**
		 * @return {@link Class} which defines interface of this type. For example {@link CtForEach}
		 */
		public Class<? extends CtElement> getModelInterface() {
			return modelInterface;
		}

		@Override
		public String toString() {
			return getName();
		}

		/**
		 * @return {@link List} of {@link Field}s of this spoon model {@link Type} in the same order, like they are processed by {@link CtScanner}
		 */
		public List<Field> getFields() {
			return fields;
		}

		/**
		 * @param role the {@link CtRole} of to be returned {@link Field}
		 * @return {@link Field} of this {@link Type} by {@link CtRole} or null if this {@link CtRole} doesn't exist on this {@link Type}
		 */
		public Field getField(CtRole role) {
			return fieldsByRole.get(role);
		}
	}
	/**
	 * Describes a Spoon metamodel Field
	 */
	public static class Field {
		private final Type owner;
		private final CtRole role;
		private final RoleHandler roleHandler;
		private final boolean derived;
		private final boolean unsettable;

		private Field(Type owner, CtRole role, boolean derived, boolean unsettable) {
			this.owner = owner;
			this.role = role;
			this.derived = derived;
			this.unsettable = unsettable;
			this.roleHandler = RoleHandlerHelper.getRoleHandler(owner.modelClass, role);
		}

		/**
		 * @return {@link Type}, which contains this {@link Field}
		 */
		public Type getOwner() {
			return owner;
		}

		/**
		 * @return {@link CtRole} of this {@link Field}
		 */
		public CtRole getRole() {
			return role;
		}

		/**
		 * @return {@link RoleHandler} providing generic access to the value of this Field
		 */
		public RoleHandler getRoleHandler() {
			return roleHandler;
		}

		/**
		 * @return true if this field is derived (value is somehow computed)
		 */
		public boolean isDerived() {
			return derived;
		}

		/**
		 * @return true if it makes no sense to set this field on this type
		 */
		public boolean isUnsettable() {
			return unsettable;
		}

		/**
		 * @param element an instance whose attribute value is read
		 * @return a value of attribute defined by this {@link Field} from the provided `element`
		 */
		public <T, U> U getValue(T element) {
			return roleHandler.getValue(element);
		}

		/**
		 * @param element an instance whose attribute value is set
		 * @param value to be set value of attribute defined by this {@link Field} on the provided `element`
		 */
		public <T, U> void setValue(T element, U value) {
			roleHandler.setValue(element, value);
		}

		/**
		 * @return {@link Class} of {@link Field}'s value.
		 */
		public Class<?> getValueClass() {
			return roleHandler.getValueClass();
		}

		/**
		 * @return the container kind, to know whether an element, a list, a map, etc is returned.
		 */
		public ContainerKind getContainerKind() {
			return roleHandler.getContainerKind();
		}

		@Override
		public String toString() {
			return getOwner().toString() + "#" + getRole().getCamelCaseName();
		}
	}

	private interface FieldMaker {
		/**
		 * Creates a instance of Field in Type
		 * @param role a role of the {@link Field}
		 * @param derived marker if field is derived
		 * @param unsettable marker if field is unsettable
		 * @return this to support fluent API
		 */
		FieldMaker field(CtRole role, boolean derived, boolean unsettable);
	}

	private static final Map<String, Type> typesByName = new HashMap<>();
	private static final Map<Class<?>, Type> typesByClass = new HashMap<>();

	static {
		List<Type> types = new ArrayList<>();
		initTypes(types);
		types.forEach(type -> {
			typesByName.put(type.getName(), type);
			typesByClass.put(type.getModelClass(), type);
			typesByClass.put(type.getModelInterface(), type);
		});
	}
	private static void initTypes(List<Type> types) {
		/**
		 * body of this method was generated by /spoon-core/src/test/java/spoon/generating/MetamodelGenerator.java
		 * Run the method main and copy the System output here
		 */
		types.add(new Type("CtConditional", spoon.reflect.code.CtConditional.class, spoon.support.reflect.code.CtConditionalImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CONDITION, false, false)
				.field(CtRole.THEN, false, false)
				.field(CtRole.ELSE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.CAST, false, false)

			));

			types.add(new Type("CtProvidedService", spoon.reflect.declaration.CtProvidedService.class, spoon.support.reflect.declaration.CtProvidedServiceImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.SERVICE_TYPE, false, false)
				.field(CtRole.IMPLEMENTATION_TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtParameter", spoon.reflect.declaration.CtParameter.class, spoon.support.reflect.declaration.CtParameterImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.IS_VARARGS, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.IS_INFERRED, false, false)

			));

			types.add(new Type("CtWhile", spoon.reflect.code.CtWhile.class, spoon.support.reflect.code.CtWhileImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtTypeReference", spoon.reflect.reference.CtTypeReference.class, spoon.support.reflect.reference.CtTypeReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.TYPE_ARGUMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, true, true)

			));

			types.add(new Type("CtCatchVariableReference", spoon.reflect.reference.CtCatchVariableReference.class, spoon.support.reflect.reference.CtCatchVariableReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtContinue", spoon.reflect.code.CtContinue.class, spoon.support.reflect.code.CtContinueImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.TARGET_LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtInterface", spoon.reflect.declaration.CtInterface.class, spoon.support.reflect.declaration.CtInterfaceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.NESTED_TYPE, true, false)
				.field(CtRole.METHOD, true, false)
				.field(CtRole.FIELD, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.INTERFACE, false, false)
				.field(CtRole.TYPE_PARAMETER, false, false)
				.field(CtRole.TYPE_MEMBER, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtAssignment", spoon.reflect.code.CtAssignment.class, spoon.support.reflect.code.CtAssignmentImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.ASSIGNED, false, false)
				.field(CtRole.ASSIGNMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtBinaryOperator", spoon.reflect.code.CtBinaryOperator.class, spoon.support.reflect.code.CtBinaryOperatorImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.OPERATOR_KIND, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.LEFT_OPERAND, false, false)
				.field(CtRole.RIGHT_OPERAND, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtEnumValue", spoon.reflect.declaration.CtEnumValue.class, spoon.support.reflect.declaration.CtEnumValueImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.ASSIGNMENT, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtModuleRequirement", spoon.reflect.declaration.CtModuleRequirement.class, spoon.support.reflect.declaration.CtModuleRequirementImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.MODULE_REF, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtForEach", spoon.reflect.code.CtForEach.class, spoon.support.reflect.code.CtForEachImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.FOREACH_VARIABLE, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtConstructor", spoon.reflect.declaration.CtConstructor.class, spoon.support.reflect.declaration.CtConstructorImpl.class, fm -> fm
				.field(CtRole.NAME, true, true)
				.field(CtRole.TYPE, true, true)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.PARAMETER, false, false)
				.field(CtRole.THROWN, false, false)
				.field(CtRole.TYPE_PARAMETER, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtSuperAccess", spoon.reflect.code.CtSuperAccess.class, spoon.support.reflect.code.CtSuperAccessImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.VARIABLE, false, false)

			));

			types.add(new Type("CtAnonymousExecutable", spoon.reflect.declaration.CtAnonymousExecutable.class, spoon.support.reflect.declaration.CtAnonymousExecutableImpl.class, fm -> fm
				.field(CtRole.NAME, true, true)
				.field(CtRole.TYPE, true, true)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.PARAMETER, true, true)
				.field(CtRole.THROWN, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtComment", spoon.reflect.code.CtComment.class, spoon.support.reflect.code.CtCommentImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.COMMENT_CONTENT, false, false)
				.field(CtRole.COMMENT_TYPE, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtWildcardReference", spoon.reflect.reference.CtWildcardReference.class, spoon.support.reflect.reference.CtWildcardReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, true, true)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_UPPER, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.TYPE_ARGUMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.BOUNDING_TYPE, false, false)

			));

			types.add(new Type("CtTypeMemberWildcardImportReference", spoon.reflect.reference.CtTypeMemberWildcardImportReference.class, spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, true, true)
				.field(CtRole.IS_IMPLICIT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.ANNOTATION, true, true)
				.field(CtRole.TYPE_REF, false, false)
			));

			types.add(new Type("CtThisAccess", spoon.reflect.code.CtThisAccess.class, spoon.support.reflect.code.CtThisAccessImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)

			));

			types.add(new Type("CtArrayWrite", spoon.reflect.code.CtArrayWrite.class, spoon.support.reflect.code.CtArrayWriteImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtPackageReference", spoon.reflect.reference.CtPackageReference.class, spoon.support.reflect.reference.CtPackageReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtJavaDoc", spoon.reflect.code.CtJavaDoc.class, spoon.support.reflect.code.CtJavaDocImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.COMMENT_CONTENT, false, false)
				.field(CtRole.COMMENT_TYPE, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT_TAG, false, false)

			));

			types.add(new Type("CtArrayRead", spoon.reflect.code.CtArrayRead.class, spoon.support.reflect.code.CtArrayReadImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtStatementList", spoon.reflect.code.CtStatementList.class, spoon.support.reflect.code.CtStatementListImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.STATEMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtVariableWrite", spoon.reflect.code.CtVariableWrite.class, spoon.support.reflect.code.CtVariableWriteImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.VARIABLE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtParameterReference", spoon.reflect.reference.CtParameterReference.class, spoon.support.reflect.reference.CtParameterReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtOperatorAssignment", spoon.reflect.code.CtOperatorAssignment.class, spoon.support.reflect.code.CtOperatorAssignmentImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.OPERATOR_KIND, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.ASSIGNED, false, false)
				.field(CtRole.ASSIGNMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtAnnotationFieldAccess", spoon.reflect.code.CtAnnotationFieldAccess.class, spoon.support.reflect.code.CtAnnotationFieldAccessImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.VARIABLE, false, false)

			));

			types.add(new Type("CtUnboundVariableReference", spoon.reflect.reference.CtUnboundVariableReference.class, spoon.support.reflect.reference.CtUnboundVariableReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.ANNOTATION, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)

			));

			types.add(new Type("CtAnnotationMethod", spoon.reflect.declaration.CtAnnotationMethod.class, spoon.support.reflect.declaration.CtAnnotationMethodImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.BODY, true, true)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.IS_DEFAULT, false, false)
				.field(CtRole.PARAMETER, true, true)
				.field(CtRole.THROWN, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.TYPE_PARAMETER, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtClass", spoon.reflect.declaration.CtClass.class, spoon.support.reflect.declaration.CtClassImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.NESTED_TYPE, true, false)
				.field(CtRole.CONSTRUCTOR, true, false)
				.field(CtRole.METHOD, true, false)
				.field(CtRole.ANNONYMOUS_EXECUTABLE, true, false)
				.field(CtRole.FIELD, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.SUPER_TYPE, false, false)
				.field(CtRole.INTERFACE, false, false)
				.field(CtRole.TYPE_PARAMETER, false, false)
				.field(CtRole.TYPE_MEMBER, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtBlock", spoon.reflect.code.CtBlock.class, spoon.support.reflect.code.CtBlockImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.STATEMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtPackage", spoon.reflect.declaration.CtPackage.class, spoon.support.reflect.declaration.CtPackageImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.SUB_PACKAGE, false, false)
				.field(CtRole.CONTAINED_TYPE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtTryWithResource", spoon.reflect.code.CtTryWithResource.class, spoon.support.reflect.code.CtTryWithResourceImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TRY_RESOURCE, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.CATCH, false, false)
				.field(CtRole.FINALIZER, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtAssert", spoon.reflect.code.CtAssert.class, spoon.support.reflect.code.CtAssertImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CONDITION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtSwitch", spoon.reflect.code.CtSwitch.class, spoon.support.reflect.code.CtSwitchImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.CASE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtSwitchExpression", spoon.reflect.code.CtSwitchExpression.class, spoon.support.reflect.code.CtSwitchExpressionImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.CASE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.CAST, false, false)

			));

			types.add(new Type("CtTry", spoon.reflect.code.CtTry.class, spoon.support.reflect.code.CtTryImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.CATCH, false, false)
				.field(CtRole.FINALIZER, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtSynchronized", spoon.reflect.code.CtSynchronized.class, spoon.support.reflect.code.CtSynchronizedImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtCompilationUnit", spoon.reflect.declaration.CtCompilationUnit.class, spoon.support.reflect.declaration.CtCompilationUnitImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, true, true)
				.field(CtRole.DECLARED_TYPE_REF, false, false)
				.field(CtRole.DECLARED_TYPE, true, true)
				.field(CtRole.DECLARED_MODULE_REF, false, false)
				.field(CtRole.DECLARED_MODULE, true, true)
				.field(CtRole.PACKAGE_DECLARATION, false, false)
				.field(CtRole.DECLARED_IMPORT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtImport", spoon.reflect.declaration.CtImport.class, spoon.support.reflect.declaration.CtImportImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.IMPORT_REFERENCE, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtPackageDeclaration", spoon.reflect.declaration.CtPackageDeclaration.class, spoon.support.reflect.declaration.CtPackageDeclarationImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtTypeParameterReference", spoon.reflect.reference.CtTypeParameterReference.class, spoon.support.reflect.reference.CtTypeParameterReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.TYPE_ARGUMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtInvocation", spoon.reflect.code.CtInvocation.class, spoon.support.reflect.code.CtInvocationImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.TYPE_ARGUMENT, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.EXECUTABLE_REF, false, false)
				.field(CtRole.ARGUMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtCodeSnippetExpression", spoon.reflect.code.CtCodeSnippetExpression.class, spoon.support.reflect.code.CtCodeSnippetExpressionImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.SNIPPET, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)

			));

			types.add(new Type("CtFieldWrite", spoon.reflect.code.CtFieldWrite.class, spoon.support.reflect.code.CtFieldWriteImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.VARIABLE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtUnaryOperator", spoon.reflect.code.CtUnaryOperator.class, spoon.support.reflect.code.CtUnaryOperatorImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.OPERATOR_KIND, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtExecutableReference", spoon.reflect.reference.CtExecutableReference.class, spoon.support.reflect.reference.CtExecutableReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_STATIC, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ARGUMENT_TYPE, false, false)
				.field(CtRole.TYPE_ARGUMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, true, true)

			));

			types.add(new Type("CtFor", spoon.reflect.code.CtFor.class, spoon.support.reflect.code.CtForImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.FOR_INIT, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.FOR_UPDATE, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtVariableRead", spoon.reflect.code.CtVariableRead.class, spoon.support.reflect.code.CtVariableReadImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.VARIABLE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtTypeParameter", spoon.reflect.declaration.CtTypeParameter.class, spoon.support.reflect.declaration.CtTypeParameterImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.TYPE_MEMBER, true, true)
				.field(CtRole.NESTED_TYPE, true, true)
				.field(CtRole.METHOD, true, true)
				.field(CtRole.FIELD, true, true)
				.field(CtRole.TYPE_PARAMETER, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.SUPER_TYPE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtLocalVariable", spoon.reflect.code.CtLocalVariable.class, spoon.support.reflect.code.CtLocalVariableImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.ASSIGNMENT, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.IS_INFERRED, false, false)
			));

			types.add(new Type("CtIf", spoon.reflect.code.CtIf.class, spoon.support.reflect.code.CtIfImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CONDITION, false, false)
				.field(CtRole.THEN, false, false)
				.field(CtRole.ELSE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtModule", spoon.reflect.declaration.CtModule.class, spoon.support.reflect.declaration.CtModuleImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.REQUIRED_MODULE, true, false)
				.field(CtRole.EXPORTED_PACKAGE, true, false)
				.field(CtRole.OPENED_PACKAGE, true, false)
				.field(CtRole.SERVICE_TYPE, true, false)
				.field(CtRole.PROVIDED_SERVICE, true, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.MODULE_DIRECTIVE, false, false)
				.field(CtRole.SUB_PACKAGE, false, false)

			));

			types.add(new Type("CtPackageExport", spoon.reflect.declaration.CtPackageExport.class, spoon.support.reflect.declaration.CtPackageExportImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.OPENED_PACKAGE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.MODULE_REF, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtConstructorCall", spoon.reflect.code.CtConstructorCall.class, spoon.support.reflect.code.CtConstructorCallImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.TYPE_ARGUMENT, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.EXECUTABLE_REF, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.ARGUMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtCase", spoon.reflect.code.CtCase.class, spoon.support.reflect.code.CtCaseImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.CASE_KIND, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.STATEMENT, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtModuleReference", spoon.reflect.reference.CtModuleReference.class, spoon.support.reflect.reference.CtModuleReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtCatch", spoon.reflect.code.CtCatch.class, spoon.support.reflect.code.CtCatchImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.PARAMETER, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtArrayTypeReference", spoon.reflect.reference.CtArrayTypeReference.class, spoon.support.reflect.reference.CtArrayTypeReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, true, true)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.TYPE_ARGUMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtMethod", spoon.reflect.declaration.CtMethod.class, spoon.support.reflect.declaration.CtMethodImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.IS_DEFAULT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE_PARAMETER, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.PARAMETER, false, false)
				.field(CtRole.THROWN, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtLambda", spoon.reflect.code.CtLambda.class, spoon.support.reflect.code.CtLambdaImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.PARAMETER, false, false)
				.field(CtRole.THROWN, true, true)
				.field(CtRole.BODY, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtNewArray", spoon.reflect.code.CtNewArray.class, spoon.support.reflect.code.CtNewArrayImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.DIMENSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtUsedService", spoon.reflect.declaration.CtUsedService.class, spoon.support.reflect.declaration.CtUsedServiceImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.SERVICE_TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtIntersectionTypeReference", spoon.reflect.reference.CtIntersectionTypeReference.class, spoon.support.reflect.reference.CtIntersectionTypeReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, true, true)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.PACKAGE_REF, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.TYPE_ARGUMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.BOUND, false, false)

			));

			types.add(new Type("CtThrow", spoon.reflect.code.CtThrow.class, spoon.support.reflect.code.CtThrowImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtLiteral", spoon.reflect.code.CtLiteral.class, spoon.support.reflect.code.CtLiteralImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.VALUE, false, false)
				.field(CtRole.LITERAL_BASE, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtReturn", spoon.reflect.code.CtReturn.class, spoon.support.reflect.code.CtReturnImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtJavaDocTag", spoon.reflect.code.CtJavaDocTag.class, spoon.support.reflect.code.CtJavaDocTagImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT_CONTENT, false, false)
				.field(CtRole.DOCUMENTATION_TYPE, false, false)
				.field(CtRole.JAVADOC_TAG_VALUE, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtField", spoon.reflect.declaration.CtField.class, spoon.support.reflect.declaration.CtFieldImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.ASSIGNMENT, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtTypeAccess", spoon.reflect.code.CtTypeAccess.class, spoon.support.reflect.code.CtTypeAccessImpl.class, fm -> fm
				.field(CtRole.TYPE, true, true)
				.field(CtRole.IS_IMPLICIT, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.ACCESSED_TYPE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtCodeSnippetStatement", spoon.reflect.code.CtCodeSnippetStatement.class, spoon.support.reflect.code.CtCodeSnippetStatementImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.SNIPPET, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtDo", spoon.reflect.code.CtDo.class, spoon.support.reflect.code.CtDoImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.EXPRESSION, false, false)
				.field(CtRole.BODY, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtAnnotation", spoon.reflect.declaration.CtAnnotation.class, spoon.support.reflect.declaration.CtAnnotationImpl.class, fm -> fm
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.CAST, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION_TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.VALUE, false, false)

			));

			types.add(new Type("CtFieldRead", spoon.reflect.code.CtFieldRead.class, spoon.support.reflect.code.CtFieldReadImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.VARIABLE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtBreak", spoon.reflect.code.CtBreak.class, spoon.support.reflect.code.CtBreakImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.TARGET_LABEL, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtFieldReference", spoon.reflect.reference.CtFieldReference.class, spoon.support.reflect.reference.CtFieldReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_FINAL, false, false)
				.field(CtRole.IS_STATIC, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.DECLARING_TYPE, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtEnum", spoon.reflect.declaration.CtEnum.class, spoon.support.reflect.declaration.CtEnumImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.NESTED_TYPE, true, false)
				.field(CtRole.CONSTRUCTOR, true, false)
				.field(CtRole.METHOD, true, false)
				.field(CtRole.ANNONYMOUS_EXECUTABLE, true, false)
				.field(CtRole.FIELD, true, false)
				.field(CtRole.TYPE_PARAMETER, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.INTERFACE, false, false)
				.field(CtRole.TYPE_MEMBER, false, false)
				.field(CtRole.VALUE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtNewClass", spoon.reflect.code.CtNewClass.class, spoon.support.reflect.code.CtNewClassImpl.class, fm -> fm
				.field(CtRole.TYPE, true, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.LABEL, false, false)
				.field(CtRole.TYPE_ARGUMENT, true, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.EXECUTABLE_REF, false, false)
				.field(CtRole.TARGET, false, false)
				.field(CtRole.ARGUMENT, false, false)
				.field(CtRole.NESTED_TYPE, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtLocalVariableReference", spoon.reflect.reference.CtLocalVariableReference.class, spoon.support.reflect.reference.CtLocalVariableReferenceImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.COMMENT, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.ANNOTATION, false, false)

			));

			types.add(new Type("CtAnnotationType", spoon.reflect.declaration.CtAnnotationType.class, spoon.support.reflect.declaration.CtAnnotationTypeImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.IS_SHADOW, false, false)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.INTERFACE, true, true)
				.field(CtRole.SUPER_TYPE, true, true)
				.field(CtRole.NESTED_TYPE, true, false)
				.field(CtRole.METHOD, true, false)
				.field(CtRole.FIELD, true, false)
				.field(CtRole.TYPE_PARAMETER, true, true)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE_MEMBER, false, false)
				.field(CtRole.COMMENT, false, false)

			));

			types.add(new Type("CtCatchVariable", spoon.reflect.code.CtCatchVariable.class, spoon.support.reflect.code.CtCatchVariableImpl.class, fm -> fm
				.field(CtRole.NAME, false, false)
				.field(CtRole.TYPE, true, true)
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.DEFAULT_EXPRESSION, true, true)
				.field(CtRole.MODIFIER, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.MULTI_TYPE, false, false)

			));

			types.add(new Type("CtExecutableReferenceExpression", spoon.reflect.code.CtExecutableReferenceExpression.class, spoon.support.reflect.code.CtExecutableReferenceExpressionImpl.class, fm -> fm
				.field(CtRole.IS_IMPLICIT, false, false)
				.field(CtRole.POSITION, false, false)
				.field(CtRole.COMMENT, false, false)
				.field(CtRole.ANNOTATION, false, false)
				.field(CtRole.TYPE, false, false)
				.field(CtRole.CAST, false, false)
				.field(CtRole.EXECUTABLE_REF, false, false)
				.field(CtRole.TARGET, false, false)

			));
			types.add(new Type("CtYieldStatement", spoon.reflect.code.CtYieldStatement.class, spoon.support.reflect.code.CtYieldStatementImpl.class, fm -> fm
			.field(CtRole.IS_IMPLICIT, false, false)
			.field(CtRole.POSITION, false, false)
			.field(CtRole.ANNOTATION, false, false)
			.field(CtRole.EXPRESSION, false, false)
			.field(CtRole.COMMENT, false, false)));
	}
}
