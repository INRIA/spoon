/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.meta.impl;
import java.lang.annotation.Annotation;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CaseKind;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.code.LiteralBase;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtMultiTypedElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.support.reflect.CtExtendedModifier;
/**
 * Contains implementations of {@link RoleHandler}s for all {@link CtRole}s of all model elements
 */
class ModelRoleHandlers {
	private ModelRoleHandlers() {
	}

	static final RoleHandler[] roleHandlers = new RoleHandler[]{ new CtTypeAccess_ACCESSED_TYPE_RoleHandler(), new CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler(), new CtElement_ANNOTATION_RoleHandler(), new CtAnnotation_ANNOTATION_TYPE_RoleHandler(), new CtAbstractInvocation_ARGUMENT_RoleHandler(), new CtExecutableReference_ARGUMENT_TYPE_RoleHandler(), new CtAssignment_ASSIGNED_RoleHandler(), new CtRHSReceiver_ASSIGNMENT_RoleHandler(), new CtBodyHolder_BODY_RoleHandler(), new CtSynchronized_BODY_RoleHandler(), new CtIntersectionTypeReference_BOUND_RoleHandler(), new CtWildcardReference_BOUNDING_TYPE_RoleHandler(), new CtAbstractSwitch_CASE_RoleHandler(), new CtCase_CASE_KIND_RoleHandler(), new CtExpression_CAST_RoleHandler(), new CtTry_CATCH_RoleHandler(), new CtElement_COMMENT_RoleHandler(), new CtComment_COMMENT_CONTENT_RoleHandler(), new CtJavaDocTag_COMMENT_CONTENT_RoleHandler(), new CtJavaDoc_COMMENT_TAG_RoleHandler(), new CtComment_COMMENT_TYPE_RoleHandler(), new CtAssert_CONDITION_RoleHandler(), new CtConditional_CONDITION_RoleHandler(), new CtIf_CONDITION_RoleHandler(), new CtClass_CONSTRUCTOR_RoleHandler(), new CtPackage_CONTAINED_TYPE_RoleHandler(), new CtCompilationUnit_DECLARED_IMPORT_RoleHandler(), new CtCompilationUnit_DECLARED_MODULE_RoleHandler(), new CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler(), new CtCompilationUnit_DECLARED_TYPE_RoleHandler(), new CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler(), new CtExecutableReference_DECLARING_TYPE_RoleHandler(), new CtFieldReference_DECLARING_TYPE_RoleHandler(), new CtTypeReference_DECLARING_TYPE_RoleHandler(), new CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler(), new CtVariable_DEFAULT_EXPRESSION_RoleHandler(), new CtNewArray_DIMENSION_RoleHandler(), new CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler(), new CtConditional_ELSE_RoleHandler(), new CtIf_ELSE_RoleHandler(), new CtModifiable_EMODIFIER_RoleHandler(), new CtAbstractInvocation_EXECUTABLE_REF_RoleHandler(), new CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler(), new CtModule_EXPORTED_PACKAGE_RoleHandler(), new CtAbstractSwitch_EXPRESSION_RoleHandler(), new CtArrayAccess_EXPRESSION_RoleHandler(), new CtAssert_EXPRESSION_RoleHandler(), new CtCase_EXPRESSION_RoleHandler(), new CtDo_EXPRESSION_RoleHandler(), new CtFor_EXPRESSION_RoleHandler(), new CtForEach_EXPRESSION_RoleHandler(), new CtLambda_EXPRESSION_RoleHandler(), new CtNewArray_EXPRESSION_RoleHandler(), new CtReturn_EXPRESSION_RoleHandler(), new CtSynchronized_EXPRESSION_RoleHandler(), new CtThrow_EXPRESSION_RoleHandler(), new CtUnaryOperator_EXPRESSION_RoleHandler(), new CtWhile_EXPRESSION_RoleHandler(), new CtYieldStatement_EXPRESSION_RoleHandler(), new CtType_FIELD_RoleHandler(), new CtTry_FINALIZER_RoleHandler(), new CtForEach_FOREACH_VARIABLE_RoleHandler(), new CtFor_FOR_INIT_RoleHandler(), new CtFor_FOR_UPDATE_RoleHandler(), new CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler(), new CtImport_IMPORT_REFERENCE_RoleHandler(), new CtType_INTERFACE_RoleHandler(), new CtTypeInformation_INTERFACE_RoleHandler(), new CtMethod_IS_DEFAULT_RoleHandler(), new CtFieldReference_IS_FINAL_RoleHandler(), new CtElement_IS_IMPLICIT_RoleHandler(), new CtLocalVariable_IS_INFERRED_RoleHandler(), new CtParameter_IS_INFERRED_RoleHandler(), new CtShadowable_IS_SHADOW_RoleHandler(), new CtExecutableReference_IS_STATIC_RoleHandler(), new CtFieldReference_IS_STATIC_RoleHandler(), new CtWildcardReference_IS_UPPER_RoleHandler(), new CtParameter_IS_VARARGS_RoleHandler(), new CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler(), new CtStatement_LABEL_RoleHandler(), new CtBinaryOperator_LEFT_OPERAND_RoleHandler(), new CtLiteral_LITERAL_BASE_RoleHandler(), new CtType_METHOD_RoleHandler(), new CtModifiable_MODIFIER_RoleHandler(), new CtModule_MODIFIER_RoleHandler(), new CtModuleRequirement_MODIFIER_RoleHandler(), new CtTypeInformation_MODIFIER_RoleHandler(), new CtModule_MODULE_DIRECTIVE_RoleHandler(), new CtModuleRequirement_MODULE_REF_RoleHandler(), new CtPackageExport_MODULE_REF_RoleHandler(), new CtMultiTypedElement_MULTI_TYPE_RoleHandler(), new CtNamedElement_NAME_RoleHandler(), new CtReference_NAME_RoleHandler(), new CtNewClass_NESTED_TYPE_RoleHandler(), new CtType_NESTED_TYPE_RoleHandler(), new CtModule_OPENED_PACKAGE_RoleHandler(), new CtPackageExport_OPENED_PACKAGE_RoleHandler(), new CtBinaryOperator_OPERATOR_KIND_RoleHandler(), new CtOperatorAssignment_OPERATOR_KIND_RoleHandler(), new CtUnaryOperator_OPERATOR_KIND_RoleHandler(), new CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler(), new CtPackageDeclaration_PACKAGE_REF_RoleHandler(), new CtPackageExport_PACKAGE_REF_RoleHandler(), new CtTypeReference_PACKAGE_REF_RoleHandler(), new CtCatch_PARAMETER_RoleHandler(), new CtExecutable_PARAMETER_RoleHandler(), new CtElement_POSITION_RoleHandler(), new CtModule_PROVIDED_SERVICE_RoleHandler(), new CtModule_REQUIRED_MODULE_RoleHandler(), new CtBinaryOperator_RIGHT_OPERAND_RoleHandler(), new CtModule_SERVICE_TYPE_RoleHandler(), new CtProvidedService_SERVICE_TYPE_RoleHandler(), new CtUsedService_SERVICE_TYPE_RoleHandler(), new CtCodeSnippet_SNIPPET_RoleHandler(), new CtStatementList_STATEMENT_RoleHandler(), new CtModule_SUB_PACKAGE_RoleHandler(), new CtPackage_SUB_PACKAGE_RoleHandler(), new CtType_SUPER_TYPE_RoleHandler(), new CtTypeInformation_SUPER_TYPE_RoleHandler(), new CtTargetedExpression_TARGET_RoleHandler(), new CtLabelledFlowBreak_TARGET_LABEL_RoleHandler(), new CtConditional_THEN_RoleHandler(), new CtIf_THEN_RoleHandler(), new CtExecutable_THROWN_RoleHandler(), new CtTryWithResource_TRY_RESOURCE_RoleHandler(), new CtArrayTypeReference_TYPE_RoleHandler(), new CtExecutableReference_TYPE_RoleHandler(), new CtTypedElement_TYPE_RoleHandler(), new CtVariableReference_TYPE_RoleHandler(), new CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler(), new CtType_TYPE_MEMBER_RoleHandler(), new CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler(), new CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler(), new CtAnnotation_VALUE_RoleHandler(), new CtEnum_VALUE_RoleHandler(), new CtLiteral_VALUE_RoleHandler(), new CtVariableAccess_VARIABLE_RoleHandler() };

	static class CtTypeAccess_ACCESSED_TYPE_RoleHandler extends SingleHandler<CtTypeAccess, CtTypeReference<?>> {
		private CtTypeAccess_ACCESSED_TYPE_RoleHandler() {
			super(CtRole.ACCESSED_TYPE, CtTypeAccess.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAccessedType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAccessedType(castValue(value));
		}
	}

	static class CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler extends ListHandler<CtClass, CtAnonymousExecutable> {
		private CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler() {
			super(CtRole.ANNONYMOUS_EXECUTABLE, CtClass.class, CtAnonymousExecutable.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAnonymousExecutables())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnonymousExecutables(castValue(value));
		}
	}

	static class CtElement_ANNOTATION_RoleHandler extends ListHandler<CtElement, CtAnnotation<? extends Annotation>> {
		private CtElement_ANNOTATION_RoleHandler() {
			super(CtRole.ANNOTATION, CtElement.class, CtAnnotation.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAnnotations())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnnotations(castValue(value));
		}
	}

	static class CtAnnotation_ANNOTATION_TYPE_RoleHandler extends SingleHandler<CtAnnotation, CtTypeReference<? extends Annotation>> {
		private CtAnnotation_ANNOTATION_TYPE_RoleHandler() {
			super(CtRole.ANNOTATION_TYPE, CtAnnotation.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAnnotationType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnnotationType(castValue(value));
		}
	}

	static class CtAbstractInvocation_ARGUMENT_RoleHandler extends ListHandler<CtAbstractInvocation, CtExpression<?>> {
		private CtAbstractInvocation_ARGUMENT_RoleHandler() {
			super(CtRole.ARGUMENT, CtAbstractInvocation.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getArguments())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setArguments(castValue(value));
		}
	}

	static class CtExecutableReference_ARGUMENT_TYPE_RoleHandler extends ListHandler<CtExecutableReference, CtTypeReference<? extends Object>> {
		private CtExecutableReference_ARGUMENT_TYPE_RoleHandler() {
			super(CtRole.ARGUMENT_TYPE, CtExecutableReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getParameters())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameters(castValue(value));
		}
	}

	static class CtAssignment_ASSIGNED_RoleHandler extends SingleHandler<CtAssignment, CtExpression<?>> {
		private CtAssignment_ASSIGNED_RoleHandler() {
			super(CtRole.ASSIGNED, CtAssignment.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAssigned())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssigned(castValue(value));
		}
	}

	static class CtRHSReceiver_ASSIGNMENT_RoleHandler extends SingleHandler<CtRHSReceiver, CtExpression<?>> {
		private CtRHSReceiver_ASSIGNMENT_RoleHandler() {
			super(CtRole.ASSIGNMENT, CtRHSReceiver.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAssignment())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssignment(castValue(value));
		}
	}

	static class CtBodyHolder_BODY_RoleHandler extends SingleHandler<CtBodyHolder, CtStatement> {
		private CtBodyHolder_BODY_RoleHandler() {
			super(CtRole.BODY, CtBodyHolder.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getBody())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBody(castValue(value));
		}
	}

	static class CtSynchronized_BODY_RoleHandler extends SingleHandler<CtSynchronized, CtBlock<? extends Object>> {
		private CtSynchronized_BODY_RoleHandler() {
			super(CtRole.BODY, CtSynchronized.class, CtBlock.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getBlock())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBlock(castValue(value));
		}
	}

	static class CtIntersectionTypeReference_BOUND_RoleHandler extends ListHandler<CtIntersectionTypeReference, CtTypeReference<? extends Object>> {
		private CtIntersectionTypeReference_BOUND_RoleHandler() {
			super(CtRole.BOUND, CtIntersectionTypeReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getBounds())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBounds(castValue(value));
		}
	}

	static class CtWildcardReference_BOUNDING_TYPE_RoleHandler extends SingleHandler<CtWildcardReference, CtTypeReference<? extends Object>> {
		private CtWildcardReference_BOUNDING_TYPE_RoleHandler() {
			super(CtRole.BOUNDING_TYPE, CtWildcardReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getBoundingType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBoundingType(castValue(value));
		}
	}

	static class CtAbstractSwitch_CASE_RoleHandler extends ListHandler<CtAbstractSwitch, CtCase<?>> {
		private CtAbstractSwitch_CASE_RoleHandler() {
			super(CtRole.CASE, CtAbstractSwitch.class, CtCase.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCases())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCases(castValue(value));
		}
	}

	static class CtCase_CASE_KIND_RoleHandler extends SingleHandler<CtCase, CaseKind> {
		private CtCase_CASE_KIND_RoleHandler() {
			super(CtRole.CASE_KIND, CtCase.class, CaseKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCaseKind())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCaseKind(castValue(value));
		}
	}

	static class CtExpression_CAST_RoleHandler extends ListHandler<CtExpression, CtTypeReference<? extends Object>> {
		private CtExpression_CAST_RoleHandler() {
			super(CtRole.CAST, CtExpression.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTypeCasts())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeCasts(castValue(value));
		}
	}

	static class CtTry_CATCH_RoleHandler extends ListHandler<CtTry, CtCatch> {
		private CtTry_CATCH_RoleHandler() {
			super(CtRole.CATCH, CtTry.class, CtCatch.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCatchers())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCatchers(castValue(value));
		}
	}

	static class CtElement_COMMENT_RoleHandler extends ListHandler<CtElement, CtComment> {
		private CtElement_COMMENT_RoleHandler() {
			super(CtRole.COMMENT, CtElement.class, CtComment.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getComments())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setComments(castValue(value));
		}
	}

	static class CtComment_COMMENT_CONTENT_RoleHandler extends SingleHandler<CtComment, String> {
		private CtComment_COMMENT_CONTENT_RoleHandler() {
			super(CtRole.COMMENT_CONTENT, CtComment.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getContent())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setContent(castValue(value));
		}
	}

	static class CtJavaDocTag_COMMENT_CONTENT_RoleHandler extends SingleHandler<CtJavaDocTag, String> {
		private CtJavaDocTag_COMMENT_CONTENT_RoleHandler() {
			super(CtRole.COMMENT_CONTENT, CtJavaDocTag.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getContent())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setContent(castValue(value));
		}
	}

	static class CtJavaDoc_COMMENT_TAG_RoleHandler extends ListHandler<CtJavaDoc, CtJavaDocTag> {
		private CtJavaDoc_COMMENT_TAG_RoleHandler() {
			super(CtRole.COMMENT_TAG, CtJavaDoc.class, CtJavaDocTag.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTags())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTags(castValue(value));
		}
	}

	static class CtComment_COMMENT_TYPE_RoleHandler extends SingleHandler<CtComment, CtComment.CommentType> {
		private CtComment_COMMENT_TYPE_RoleHandler() {
			super(CtRole.COMMENT_TYPE, CtComment.class, CtComment.CommentType.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCommentType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCommentType(castValue(value));
		}
	}

	static class CtAssert_CONDITION_RoleHandler extends SingleHandler<CtAssert, CtExpression<Boolean>> {
		private CtAssert_CONDITION_RoleHandler() {
			super(CtRole.CONDITION, CtAssert.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAssertExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssertExpression(castValue(value));
		}
	}

	static class CtConditional_CONDITION_RoleHandler extends SingleHandler<CtConditional, CtExpression<Boolean>> {
		private CtConditional_CONDITION_RoleHandler() {
			super(CtRole.CONDITION, CtConditional.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCondition())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCondition(castValue(value));
		}
	}

	static class CtIf_CONDITION_RoleHandler extends SingleHandler<CtIf, CtExpression<Boolean>> {
		private CtIf_CONDITION_RoleHandler() {
			super(CtRole.CONDITION, CtIf.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCondition())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCondition(castValue(value));
		}
	}

	static class CtClass_CONSTRUCTOR_RoleHandler extends SetHandler<CtClass, CtConstructor<?>> {
		private CtClass_CONSTRUCTOR_RoleHandler() {
			super(CtRole.CONSTRUCTOR, CtClass.class, CtConstructor.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getConstructors())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setConstructors(castValue(value));
		}
	}

	static class CtPackage_CONTAINED_TYPE_RoleHandler extends SetHandler<CtPackage, CtType<? extends Object>> {
		private CtPackage_CONTAINED_TYPE_RoleHandler() {
			super(CtRole.CONTAINED_TYPE, CtPackage.class, CtType.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTypes())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypes(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_IMPORT_RoleHandler extends ListHandler<CtCompilationUnit, CtImport> {
		private CtCompilationUnit_DECLARED_IMPORT_RoleHandler() {
			super(CtRole.DECLARED_IMPORT, CtCompilationUnit.class, CtImport.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getImports())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImports(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_MODULE_RoleHandler extends SingleHandler<CtCompilationUnit, CtModule> {
		private CtCompilationUnit_DECLARED_MODULE_RoleHandler() {
			super(CtRole.DECLARED_MODULE, CtCompilationUnit.class, CtModule.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaredModule())));
		}
	}

	static class CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler extends SingleHandler<CtCompilationUnit, CtModuleReference> {
		private CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler() {
			super(CtRole.DECLARED_MODULE_REF, CtCompilationUnit.class, CtModuleReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaredModuleReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaredModuleReference(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_TYPE_RoleHandler extends ListHandler<CtCompilationUnit, CtType<? extends Object>> {
		private CtCompilationUnit_DECLARED_TYPE_RoleHandler() {
			super(CtRole.DECLARED_TYPE, CtCompilationUnit.class, CtType.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaredTypes())));
		}
	}

	static class CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler extends ListHandler<CtCompilationUnit, CtTypeReference<? extends Object>> {
		private CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler() {
			super(CtRole.DECLARED_TYPE_REF, CtCompilationUnit.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaredTypeReferences())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaredTypeReferences(castValue(value));
		}
	}

	static class CtExecutableReference_DECLARING_TYPE_RoleHandler extends SingleHandler<CtExecutableReference, CtTypeReference<? extends Object>> {
		private CtExecutableReference_DECLARING_TYPE_RoleHandler() {
			super(CtRole.DECLARING_TYPE, CtExecutableReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaringType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtFieldReference_DECLARING_TYPE_RoleHandler extends SingleHandler<CtFieldReference, CtTypeReference<? extends Object>> {
		private CtFieldReference_DECLARING_TYPE_RoleHandler() {
			super(CtRole.DECLARING_TYPE, CtFieldReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaringType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtTypeReference_DECLARING_TYPE_RoleHandler extends SingleHandler<CtTypeReference, CtTypeReference<? extends Object>> {
		private CtTypeReference_DECLARING_TYPE_RoleHandler() {
			super(CtRole.DECLARING_TYPE, CtTypeReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDeclaringType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler extends SingleHandler<CtAnnotationMethod, CtExpression<?>> {
		private CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler() {
			super(CtRole.DEFAULT_EXPRESSION, CtAnnotationMethod.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDefaultExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultExpression(castValue(value));
		}
	}

	static class CtVariable_DEFAULT_EXPRESSION_RoleHandler extends SingleHandler<CtVariable, CtExpression<?>> {
		private CtVariable_DEFAULT_EXPRESSION_RoleHandler() {
			super(CtRole.DEFAULT_EXPRESSION, CtVariable.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDefaultExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultExpression(castValue(value));
		}
	}

	static class CtNewArray_DIMENSION_RoleHandler extends ListHandler<CtNewArray, CtExpression<Integer>> {
		private CtNewArray_DIMENSION_RoleHandler() {
			super(CtRole.DIMENSION, CtNewArray.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getDimensionExpressions())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDimensionExpressions(castValue(value));
		}
	}

	static class CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler extends SingleHandler<CtJavaDocTag, CtJavaDocTag.TagType> {
		private CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler() {
			super(CtRole.DOCUMENTATION_TYPE, CtJavaDocTag.class, CtJavaDocTag.TagType.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtConditional_ELSE_RoleHandler extends SingleHandler<CtConditional, CtExpression<?>> {
		private CtConditional_ELSE_RoleHandler() {
			super(CtRole.ELSE, CtConditional.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getElseExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElseExpression(castValue(value));
		}
	}

	static class CtIf_ELSE_RoleHandler extends SingleHandler<CtIf, CtStatement> {
		private CtIf_ELSE_RoleHandler() {
			super(CtRole.ELSE, CtIf.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getElseStatement())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElseStatement(castValue(value));
		}
	}

	static class CtModifiable_EMODIFIER_RoleHandler extends SetHandler<CtModifiable, CtExtendedModifier> {
		private CtModifiable_EMODIFIER_RoleHandler() {
			super(CtRole.EMODIFIER, CtModifiable.class, CtExtendedModifier.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExtendedModifiers())));
		}
	}

	static class CtAbstractInvocation_EXECUTABLE_REF_RoleHandler extends SingleHandler<CtAbstractInvocation, CtExecutableReference<?>> {
		private CtAbstractInvocation_EXECUTABLE_REF_RoleHandler() {
			super(CtRole.EXECUTABLE_REF, CtAbstractInvocation.class, CtExecutableReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExecutable())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExecutable(castValue(value));
		}
	}

	static class CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler extends SingleHandler<CtExecutableReferenceExpression, CtExecutableReference<?>> {
		private CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler() {
			super(CtRole.EXECUTABLE_REF, CtExecutableReferenceExpression.class, CtExecutableReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExecutable())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExecutable(castValue(value));
		}
	}

	static class CtModule_EXPORTED_PACKAGE_RoleHandler extends ListHandler<CtModule, CtPackageExport> {
		private CtModule_EXPORTED_PACKAGE_RoleHandler() {
			super(CtRole.EXPORTED_PACKAGE, CtModule.class, CtPackageExport.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExportedPackages())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExportedPackages(castValue(value));
		}
	}

	static class CtAbstractSwitch_EXPRESSION_RoleHandler extends SingleHandler<CtAbstractSwitch, CtExpression<?>> {
		private CtAbstractSwitch_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtAbstractSwitch.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSelector())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSelector(castValue(value));
		}
	}

	static class CtArrayAccess_EXPRESSION_RoleHandler extends SingleHandler<CtArrayAccess, CtExpression<Integer>> {
		private CtArrayAccess_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtArrayAccess.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getIndexExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setIndexExpression(castValue(value));
		}
	}

	static class CtAssert_EXPRESSION_RoleHandler extends SingleHandler<CtAssert, CtExpression<?>> {
		private CtAssert_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtAssert.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtCase_EXPRESSION_RoleHandler extends ListHandler<CtCase, CtExpression<?>> {
		private CtCase_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtCase.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getCaseExpressions())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCaseExpressions(castValue(value));
		}
	}

	static class CtDo_EXPRESSION_RoleHandler extends SingleHandler<CtDo, CtExpression<Boolean>> {
		private CtDo_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtDo.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getLoopingExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLoopingExpression(castValue(value));
		}
	}

	static class CtFor_EXPRESSION_RoleHandler extends SingleHandler<CtFor, CtExpression<Boolean>> {
		private CtFor_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtFor.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtForEach_EXPRESSION_RoleHandler extends SingleHandler<CtForEach, CtExpression<? extends Object>> {
		private CtForEach_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtForEach.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtLambda_EXPRESSION_RoleHandler extends SingleHandler<CtLambda, CtExpression<?>> {
		private CtLambda_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtLambda.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtNewArray_EXPRESSION_RoleHandler extends ListHandler<CtNewArray, CtExpression<? extends Object>> {
		private CtNewArray_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtNewArray.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getElements())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElements(castValue(value));
		}
	}

	static class CtReturn_EXPRESSION_RoleHandler extends SingleHandler<CtReturn, CtExpression<?>> {
		private CtReturn_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtReturn.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getReturnedExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReturnedExpression(castValue(value));
		}
	}

	static class CtSynchronized_EXPRESSION_RoleHandler extends SingleHandler<CtSynchronized, CtExpression<? extends Object>> {
		private CtSynchronized_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtSynchronized.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtThrow_EXPRESSION_RoleHandler extends SingleHandler<CtThrow, CtExpression<? extends Throwable>> {
		private CtThrow_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtThrow.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getThrownExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThrownExpression(castValue(value));
		}
	}

	static class CtUnaryOperator_EXPRESSION_RoleHandler extends SingleHandler<CtUnaryOperator, CtExpression<?>> {
		private CtUnaryOperator_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtUnaryOperator.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getOperand())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOperand(castValue(value));
		}
	}

	static class CtWhile_EXPRESSION_RoleHandler extends SingleHandler<CtWhile, CtExpression<Boolean>> {
		private CtWhile_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtWhile.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getLoopingExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLoopingExpression(castValue(value));
		}
	}

	static class CtYieldStatement_EXPRESSION_RoleHandler extends SingleHandler<CtYieldStatement, CtExpression<? extends Object>> {
		private CtYieldStatement_EXPRESSION_RoleHandler() {
			super(CtRole.EXPRESSION, CtYieldStatement.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtType_FIELD_RoleHandler extends ListHandler<CtType, CtField<? extends Object>> {
		private CtType_FIELD_RoleHandler() {
			super(CtRole.FIELD, CtType.class, CtField.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getFields())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFields(castValue(value));
		}
	}

	static class CtTry_FINALIZER_RoleHandler extends SingleHandler<CtTry, CtBlock<? extends Object>> {
		private CtTry_FINALIZER_RoleHandler() {
			super(CtRole.FINALIZER, CtTry.class, CtBlock.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getFinalizer())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFinalizer(castValue(value));
		}
	}

	static class CtForEach_FOREACH_VARIABLE_RoleHandler extends SingleHandler<CtForEach, CtLocalVariable<? extends Object>> {
		private CtForEach_FOREACH_VARIABLE_RoleHandler() {
			super(CtRole.FOREACH_VARIABLE, CtForEach.class, CtLocalVariable.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getVariable())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVariable(castValue(value));
		}
	}

	static class CtFor_FOR_INIT_RoleHandler extends ListHandler<CtFor, CtStatement> {
		private CtFor_FOR_INIT_RoleHandler() {
			super(CtRole.FOR_INIT, CtFor.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getForInit())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setForInit(castValue(value));
		}
	}

	static class CtFor_FOR_UPDATE_RoleHandler extends ListHandler<CtFor, CtStatement> {
		private CtFor_FOR_UPDATE_RoleHandler() {
			super(CtRole.FOR_UPDATE, CtFor.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getForUpdate())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setForUpdate(castValue(value));
		}
	}

	static class CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler extends ListHandler<CtProvidedService, CtTypeReference> {
		private CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler() {
			super(CtRole.IMPLEMENTATION_TYPE, CtProvidedService.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getImplementationTypes())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImplementationTypes(castValue(value));
		}
	}

	static class CtImport_IMPORT_REFERENCE_RoleHandler extends SingleHandler<CtImport, CtReference> {
		private CtImport_IMPORT_REFERENCE_RoleHandler() {
			super(CtRole.IMPORT_REFERENCE, CtImport.class, CtReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReference(castValue(value));
		}
	}

	static class CtType_INTERFACE_RoleHandler extends SetHandler<CtType, CtTypeReference<? extends Object>> {
		private CtType_INTERFACE_RoleHandler() {
			super(CtRole.INTERFACE, CtType.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSuperInterfaces())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSuperInterfaces(castValue(value));
		}
	}

	static class CtTypeInformation_INTERFACE_RoleHandler extends SetHandler<CtTypeInformation, CtTypeReference<?>> {
		private CtTypeInformation_INTERFACE_RoleHandler() {
			super(CtRole.INTERFACE, CtTypeInformation.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSuperInterfaces())));
		}
	}

	static class CtMethod_IS_DEFAULT_RoleHandler extends SingleHandler<CtMethod, Boolean> {
		private CtMethod_IS_DEFAULT_RoleHandler() {
			super(CtRole.IS_DEFAULT, CtMethod.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isDefaultMethod())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultMethod(castValue(value));
		}
	}

	static class CtFieldReference_IS_FINAL_RoleHandler extends SingleHandler<CtFieldReference, Boolean> {
		private CtFieldReference_IS_FINAL_RoleHandler() {
			super(CtRole.IS_FINAL, CtFieldReference.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isFinal())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFinal(castValue(value));
		}
	}

	static class CtElement_IS_IMPLICIT_RoleHandler extends SingleHandler<CtElement, Boolean> {
		private CtElement_IS_IMPLICIT_RoleHandler() {
			super(CtRole.IS_IMPLICIT, CtElement.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isImplicit())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImplicit(castValue(value));
		}
	}

	static class CtLocalVariable_IS_INFERRED_RoleHandler extends SingleHandler<CtLocalVariable, Boolean> {
		private CtLocalVariable_IS_INFERRED_RoleHandler() {
			super(CtRole.IS_INFERRED, CtLocalVariable.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isInferred())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setInferred(castValue(value));
		}
	}

	static class CtParameter_IS_INFERRED_RoleHandler extends SingleHandler<CtParameter, Boolean> {
		private CtParameter_IS_INFERRED_RoleHandler() {
			super(CtRole.IS_INFERRED, CtParameter.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isInferred())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setInferred(castValue(value));
		}
	}

	static class CtShadowable_IS_SHADOW_RoleHandler extends SingleHandler<CtShadowable, Boolean> {
		private CtShadowable_IS_SHADOW_RoleHandler() {
			super(CtRole.IS_SHADOW, CtShadowable.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isShadow())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setShadow(castValue(value));
		}
	}

	static class CtExecutableReference_IS_STATIC_RoleHandler extends SingleHandler<CtExecutableReference, Boolean> {
		private CtExecutableReference_IS_STATIC_RoleHandler() {
			super(CtRole.IS_STATIC, CtExecutableReference.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isStatic())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatic(castValue(value));
		}
	}

	static class CtFieldReference_IS_STATIC_RoleHandler extends SingleHandler<CtFieldReference, Boolean> {
		private CtFieldReference_IS_STATIC_RoleHandler() {
			super(CtRole.IS_STATIC, CtFieldReference.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isStatic())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatic(castValue(value));
		}
	}

	static class CtWildcardReference_IS_UPPER_RoleHandler extends SingleHandler<CtWildcardReference, Boolean> {
		private CtWildcardReference_IS_UPPER_RoleHandler() {
			super(CtRole.IS_UPPER, CtWildcardReference.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isUpper())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setUpper(castValue(value));
		}
	}

	static class CtParameter_IS_VARARGS_RoleHandler extends SingleHandler<CtParameter, Boolean> {
		private CtParameter_IS_VARARGS_RoleHandler() {
			super(CtRole.IS_VARARGS, CtParameter.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isVarArgs())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVarArgs(castValue(value));
		}
	}

	static class CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler extends SingleHandler<CtJavaDocTag, String> {
		private CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler() {
			super(CtRole.JAVADOC_TAG_VALUE, CtJavaDocTag.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getParam())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParam(castValue(value));
		}
	}

	static class CtStatement_LABEL_RoleHandler extends SingleHandler<CtStatement, String> {
		private CtStatement_LABEL_RoleHandler() {
			super(CtRole.LABEL, CtStatement.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getLabel())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLabel(castValue(value));
		}
	}

	static class CtBinaryOperator_LEFT_OPERAND_RoleHandler extends SingleHandler<CtBinaryOperator, CtExpression<? extends Object>> {
		private CtBinaryOperator_LEFT_OPERAND_RoleHandler() {
			super(CtRole.LEFT_OPERAND, CtBinaryOperator.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getLeftHandOperand())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLeftHandOperand(castValue(value));
		}
	}

	static class CtLiteral_LITERAL_BASE_RoleHandler extends SingleHandler<CtLiteral, LiteralBase> {
		private CtLiteral_LITERAL_BASE_RoleHandler() {
			super(CtRole.LITERAL_BASE, CtLiteral.class, LiteralBase.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getBase())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBase(castValue(value));
		}
	}

	static class CtType_METHOD_RoleHandler extends SetHandler<CtType, CtMethod<? extends Object>> {
		private CtType_METHOD_RoleHandler() {
			super(CtRole.METHOD, CtType.class, CtMethod.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getMethods())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setMethods(castValue(value));
		}
	}

	static class CtModifiable_MODIFIER_RoleHandler extends SetHandler<CtModifiable, ModifierKind> {
		private CtModifiable_MODIFIER_RoleHandler() {
			super(CtRole.MODIFIER, CtModifiable.class, ModifierKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getModifiers())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModifiers(castValue(value));
		}
	}

	static class CtModule_MODIFIER_RoleHandler extends SingleHandler<CtModule, Boolean> {
		private CtModule_MODIFIER_RoleHandler() {
			super(CtRole.MODIFIER, CtModule.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isOpenModule())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setIsOpenModule(castValue(value));
		}
	}

	static class CtModuleRequirement_MODIFIER_RoleHandler extends SetHandler<CtModuleRequirement, CtModuleRequirement.RequiresModifier> {
		private CtModuleRequirement_MODIFIER_RoleHandler() {
			super(CtRole.MODIFIER, CtModuleRequirement.class, CtModuleRequirement.RequiresModifier.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getRequiresModifiers())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRequiresModifiers(castValue(value));
		}
	}

	static class CtTypeInformation_MODIFIER_RoleHandler extends SetHandler<CtTypeInformation, ModifierKind> {
		private CtTypeInformation_MODIFIER_RoleHandler() {
			super(CtRole.MODIFIER, CtTypeInformation.class, ModifierKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getModifiers())));
		}
	}

	static class CtModule_MODULE_DIRECTIVE_RoleHandler extends ListHandler<CtModule, CtModuleDirective> {
		private CtModule_MODULE_DIRECTIVE_RoleHandler() {
			super(CtRole.MODULE_DIRECTIVE, CtModule.class, CtModuleDirective.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getModuleDirectives())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModuleDirectives(castValue(value));
		}
	}

	static class CtModuleRequirement_MODULE_REF_RoleHandler extends SingleHandler<CtModuleRequirement, CtModuleReference> {
		private CtModuleRequirement_MODULE_REF_RoleHandler() {
			super(CtRole.MODULE_REF, CtModuleRequirement.class, CtModuleReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getModuleReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModuleReference(castValue(value));
		}
	}

	static class CtPackageExport_MODULE_REF_RoleHandler extends ListHandler<CtPackageExport, CtModuleReference> {
		private CtPackageExport_MODULE_REF_RoleHandler() {
			super(CtRole.MODULE_REF, CtPackageExport.class, CtModuleReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTargetExport())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTargetExport(castValue(value));
		}
	}

	static class CtMultiTypedElement_MULTI_TYPE_RoleHandler extends ListHandler<CtMultiTypedElement, CtTypeReference<?>> {
		private CtMultiTypedElement_MULTI_TYPE_RoleHandler() {
			super(CtRole.MULTI_TYPE, CtMultiTypedElement.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getMultiTypes())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setMultiTypes(castValue(value));
		}
	}

	static class CtNamedElement_NAME_RoleHandler extends SingleHandler<CtNamedElement, String> {
		private CtNamedElement_NAME_RoleHandler() {
			super(CtRole.NAME, CtNamedElement.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSimpleName())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSimpleName(castValue(value));
		}
	}

	static class CtReference_NAME_RoleHandler extends SingleHandler<CtReference, String> {
		private CtReference_NAME_RoleHandler() {
			super(CtRole.NAME, CtReference.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSimpleName())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSimpleName(castValue(value));
		}
	}

	static class CtNewClass_NESTED_TYPE_RoleHandler extends SingleHandler<CtNewClass, CtClass<? extends Object>> {
		private CtNewClass_NESTED_TYPE_RoleHandler() {
			super(CtRole.NESTED_TYPE, CtNewClass.class, CtClass.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getAnonymousClass())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnonymousClass(castValue(value));
		}
	}

	static class CtType_NESTED_TYPE_RoleHandler extends SetHandler<CtType, CtType<? extends Object>> {
		private CtType_NESTED_TYPE_RoleHandler() {
			super(CtRole.NESTED_TYPE, CtType.class, CtType.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getNestedTypes())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setNestedTypes(castValue(value));
		}
	}

	static class CtModule_OPENED_PACKAGE_RoleHandler extends ListHandler<CtModule, CtPackageExport> {
		private CtModule_OPENED_PACKAGE_RoleHandler() {
			super(CtRole.OPENED_PACKAGE, CtModule.class, CtPackageExport.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getOpenedPackages())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOpenedPackages(castValue(value));
		}
	}

	static class CtPackageExport_OPENED_PACKAGE_RoleHandler extends SingleHandler<CtPackageExport, Boolean> {
		private CtPackageExport_OPENED_PACKAGE_RoleHandler() {
			super(CtRole.OPENED_PACKAGE, CtPackageExport.class, Boolean.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).isOpenedPackage())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOpenedPackage(castValue(value));
		}
	}

	static class CtBinaryOperator_OPERATOR_KIND_RoleHandler extends SingleHandler<CtBinaryOperator, BinaryOperatorKind> {
		private CtBinaryOperator_OPERATOR_KIND_RoleHandler() {
			super(CtRole.OPERATOR_KIND, CtBinaryOperator.class, BinaryOperatorKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getKind())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtOperatorAssignment_OPERATOR_KIND_RoleHandler extends SingleHandler<CtOperatorAssignment, BinaryOperatorKind> {
		private CtOperatorAssignment_OPERATOR_KIND_RoleHandler() {
			super(CtRole.OPERATOR_KIND, CtOperatorAssignment.class, BinaryOperatorKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getKind())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtUnaryOperator_OPERATOR_KIND_RoleHandler extends SingleHandler<CtUnaryOperator, UnaryOperatorKind> {
		private CtUnaryOperator_OPERATOR_KIND_RoleHandler() {
			super(CtRole.OPERATOR_KIND, CtUnaryOperator.class, UnaryOperatorKind.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getKind())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler extends SingleHandler<CtCompilationUnit, CtPackageDeclaration> {
		private CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler() {
			super(CtRole.PACKAGE_DECLARATION, CtCompilationUnit.class, CtPackageDeclaration.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getPackageDeclaration())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackageDeclaration(castValue(value));
		}
	}

	static class CtPackageDeclaration_PACKAGE_REF_RoleHandler extends SingleHandler<CtPackageDeclaration, CtPackageReference> {
		private CtPackageDeclaration_PACKAGE_REF_RoleHandler() {
			super(CtRole.PACKAGE_REF, CtPackageDeclaration.class, CtPackageReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReference(castValue(value));
		}
	}

	static class CtPackageExport_PACKAGE_REF_RoleHandler extends SingleHandler<CtPackageExport, CtPackageReference> {
		private CtPackageExport_PACKAGE_REF_RoleHandler() {
			super(CtRole.PACKAGE_REF, CtPackageExport.class, CtPackageReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getPackageReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackageReference(castValue(value));
		}
	}

	static class CtTypeReference_PACKAGE_REF_RoleHandler extends SingleHandler<CtTypeReference, CtPackageReference> {
		private CtTypeReference_PACKAGE_REF_RoleHandler() {
			super(CtRole.PACKAGE_REF, CtTypeReference.class, CtPackageReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getPackage())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackage(castValue(value));
		}
	}

	static class CtCatch_PARAMETER_RoleHandler extends SingleHandler<CtCatch, CtCatchVariable<? extends Throwable>> {
		private CtCatch_PARAMETER_RoleHandler() {
			super(CtRole.PARAMETER, CtCatch.class, CtCatchVariable.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getParameter())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameter(castValue(value));
		}
	}

	static class CtExecutable_PARAMETER_RoleHandler extends ListHandler<CtExecutable, CtParameter<? extends Object>> {
		private CtExecutable_PARAMETER_RoleHandler() {
			super(CtRole.PARAMETER, CtExecutable.class, CtParameter.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getParameters())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameters(castValue(value));
		}
	}

	static class CtElement_POSITION_RoleHandler extends SingleHandler<CtElement, SourcePosition> {
		private CtElement_POSITION_RoleHandler() {
			super(CtRole.POSITION, CtElement.class, SourcePosition.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getPosition())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPosition(castValue(value));
		}
	}

	static class CtModule_PROVIDED_SERVICE_RoleHandler extends ListHandler<CtModule, CtProvidedService> {
		private CtModule_PROVIDED_SERVICE_RoleHandler() {
			super(CtRole.PROVIDED_SERVICE, CtModule.class, CtProvidedService.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getProvidedServices())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setProvidedServices(castValue(value));
		}
	}

	static class CtModule_REQUIRED_MODULE_RoleHandler extends ListHandler<CtModule, CtModuleRequirement> {
		private CtModule_REQUIRED_MODULE_RoleHandler() {
			super(CtRole.REQUIRED_MODULE, CtModule.class, CtModuleRequirement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getRequiredModules())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRequiredModules(castValue(value));
		}
	}

	static class CtBinaryOperator_RIGHT_OPERAND_RoleHandler extends SingleHandler<CtBinaryOperator, CtExpression<? extends Object>> {
		private CtBinaryOperator_RIGHT_OPERAND_RoleHandler() {
			super(CtRole.RIGHT_OPERAND, CtBinaryOperator.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getRightHandOperand())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRightHandOperand(castValue(value));
		}
	}

	static class CtModule_SERVICE_TYPE_RoleHandler extends ListHandler<CtModule, CtUsedService> {
		private CtModule_SERVICE_TYPE_RoleHandler() {
			super(CtRole.SERVICE_TYPE, CtModule.class, CtUsedService.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getUsedServices())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setUsedServices(castValue(value));
		}
	}

	static class CtProvidedService_SERVICE_TYPE_RoleHandler extends SingleHandler<CtProvidedService, CtTypeReference> {
		private CtProvidedService_SERVICE_TYPE_RoleHandler() {
			super(CtRole.SERVICE_TYPE, CtProvidedService.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getServiceType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setServiceType(castValue(value));
		}
	}

	static class CtUsedService_SERVICE_TYPE_RoleHandler extends SingleHandler<CtUsedService, CtTypeReference> {
		private CtUsedService_SERVICE_TYPE_RoleHandler() {
			super(CtRole.SERVICE_TYPE, CtUsedService.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getServiceType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setServiceType(castValue(value));
		}
	}

	static class CtCodeSnippet_SNIPPET_RoleHandler extends SingleHandler<CtCodeSnippet, String> {
		private CtCodeSnippet_SNIPPET_RoleHandler() {
			super(CtRole.SNIPPET, CtCodeSnippet.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getValue())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValue(castValue(value));
		}
	}

	static class CtStatementList_STATEMENT_RoleHandler extends ListHandler<CtStatementList, CtStatement> {
		private CtStatementList_STATEMENT_RoleHandler() {
			super(CtRole.STATEMENT, CtStatementList.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getStatements())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatements(castValue(value));
		}
	}

	static class CtModule_SUB_PACKAGE_RoleHandler extends SingleHandler<CtModule, CtPackage> {
		private CtModule_SUB_PACKAGE_RoleHandler() {
			super(CtRole.SUB_PACKAGE, CtModule.class, CtPackage.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getRootPackage())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRootPackage(castValue(value));
		}
	}

	static class CtPackage_SUB_PACKAGE_RoleHandler extends SetHandler<CtPackage, CtPackage> {
		private CtPackage_SUB_PACKAGE_RoleHandler() {
			super(CtRole.SUB_PACKAGE, CtPackage.class, CtPackage.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getPackages())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackages(castValue(value));
		}
	}

	static class CtType_SUPER_TYPE_RoleHandler extends SingleHandler<CtType, CtTypeReference<? extends Object>> {
		private CtType_SUPER_TYPE_RoleHandler() {
			super(CtRole.SUPER_TYPE, CtType.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSuperclass())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSuperclass(castValue(value));
		}
	}

	static class CtTypeInformation_SUPER_TYPE_RoleHandler extends SingleHandler<CtTypeInformation, CtTypeReference<?>> {
		private CtTypeInformation_SUPER_TYPE_RoleHandler() {
			super(CtRole.SUPER_TYPE, CtTypeInformation.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getSuperclass())));
		}
	}

	static class CtTargetedExpression_TARGET_RoleHandler extends SingleHandler<CtTargetedExpression, CtExpression<?>> {
		private CtTargetedExpression_TARGET_RoleHandler() {
			super(CtRole.TARGET, CtTargetedExpression.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTarget())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTarget(castValue(value));
		}
	}

	static class CtLabelledFlowBreak_TARGET_LABEL_RoleHandler extends SingleHandler<CtLabelledFlowBreak, String> {
		private CtLabelledFlowBreak_TARGET_LABEL_RoleHandler() {
			super(CtRole.TARGET_LABEL, CtLabelledFlowBreak.class, String.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTargetLabel())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTargetLabel(castValue(value));
		}
	}

	static class CtConditional_THEN_RoleHandler extends SingleHandler<CtConditional, CtExpression<?>> {
		private CtConditional_THEN_RoleHandler() {
			super(CtRole.THEN, CtConditional.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getThenExpression())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThenExpression(castValue(value));
		}
	}

	static class CtIf_THEN_RoleHandler extends SingleHandler<CtIf, CtStatement> {
		private CtIf_THEN_RoleHandler() {
			super(CtRole.THEN, CtIf.class, CtStatement.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getThenStatement())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThenStatement(castValue(value));
		}
	}

	static class CtExecutable_THROWN_RoleHandler extends SetHandler<CtExecutable, CtTypeReference<? extends Throwable>> {
		private CtExecutable_THROWN_RoleHandler() {
			super(CtRole.THROWN, CtExecutable.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getThrownTypes())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThrownTypes(castValue(value));
		}
	}

	static class CtTryWithResource_TRY_RESOURCE_RoleHandler extends ListHandler<CtTryWithResource, CtLocalVariable<? extends Object>> {
		private CtTryWithResource_TRY_RESOURCE_RoleHandler() {
			super(CtRole.TRY_RESOURCE, CtTryWithResource.class, CtLocalVariable.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getResources())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setResources(castValue(value));
		}
	}

	static class CtArrayTypeReference_TYPE_RoleHandler extends SingleHandler<CtArrayTypeReference, CtTypeReference<? extends Object>> {
		private CtArrayTypeReference_TYPE_RoleHandler() {
			super(CtRole.TYPE, CtArrayTypeReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getComponentType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setComponentType(castValue(value));
		}
	}

	static class CtExecutableReference_TYPE_RoleHandler extends SingleHandler<CtExecutableReference, CtTypeReference<?>> {
		private CtExecutableReference_TYPE_RoleHandler() {
			super(CtRole.TYPE, CtExecutableReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtTypedElement_TYPE_RoleHandler extends SingleHandler<CtTypedElement, CtTypeReference<?>> {
		private CtTypedElement_TYPE_RoleHandler() {
			super(CtRole.TYPE, CtTypedElement.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtVariableReference_TYPE_RoleHandler extends SingleHandler<CtVariableReference, CtTypeReference<?>> {
		private CtVariableReference_TYPE_RoleHandler() {
			super(CtRole.TYPE, CtVariableReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getType())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler extends ListHandler<CtActualTypeContainer, CtTypeReference<?>> {
		private CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler() {
			super(CtRole.TYPE_ARGUMENT, CtActualTypeContainer.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getActualTypeArguments())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setActualTypeArguments(castValue(value));
		}
	}

	static class CtType_TYPE_MEMBER_RoleHandler extends ListHandler<CtType, CtTypeMember> {
		private CtType_TYPE_MEMBER_RoleHandler() {
			super(CtRole.TYPE_MEMBER, CtType.class, CtTypeMember.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTypeMembers())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeMembers(castValue(value));
		}
	}

	static class CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler extends ListHandler<CtFormalTypeDeclarer, CtTypeParameter> {
		private CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler() {
			super(CtRole.TYPE_PARAMETER, CtFormalTypeDeclarer.class, CtTypeParameter.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getFormalCtTypeParameters())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFormalCtTypeParameters(castValue(value));
		}
	}

	static class CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler extends SingleHandler<CtTypeMemberWildcardImportReference, CtTypeReference<? extends Object>> {
		private CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler() {
			super(CtRole.TYPE_REF, CtTypeMemberWildcardImportReference.class, CtTypeReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getTypeReference())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeReference(castValue(value));
		}
	}

	static class CtAnnotation_VALUE_RoleHandler extends MapHandler<CtAnnotation, CtExpression> {
		private CtAnnotation_VALUE_RoleHandler() {
			super(CtRole.VALUE, CtAnnotation.class, CtExpression.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getValues())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValues(castValue(value));
		}
	}

	static class CtEnum_VALUE_RoleHandler extends ListHandler<CtEnum, CtEnumValue<? extends Object>> {
		private CtEnum_VALUE_RoleHandler() {
			super(CtRole.VALUE, CtEnum.class, CtEnumValue.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getEnumValues())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setEnumValues(castValue(value));
		}
	}

	static class CtLiteral_VALUE_RoleHandler extends SingleHandler<CtLiteral, Object> {
		private CtLiteral_VALUE_RoleHandler() {
			super(CtRole.VALUE, CtLiteral.class, Object.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getValue())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValue(castValue(value));
		}
	}

	static class CtVariableAccess_VARIABLE_RoleHandler extends SingleHandler<CtVariableAccess, CtVariableReference<?>> {
		private CtVariableAccess_VARIABLE_RoleHandler() {
			super(CtRole.VARIABLE, CtVariableAccess.class, CtVariableReference.class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T, U> U getValue(T element) {
			return ((U) ((Object) (castTarget(element).getVariable())));
		}

		@Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVariable(castValue(value));
		}
	}
}
