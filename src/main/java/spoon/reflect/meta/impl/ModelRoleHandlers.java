package spoon.reflect.meta.impl;

import spoon.reflect.meta.RoleHandler;
import spoon.reflect.path.CtRole;

/**
 * Contains implementations of {@link RoleHandler}s for all {@link CtRole}s of all model elements
 */
class ModelRoleHandlers {
	private ModelRoleHandlers() {
	}

	static final spoon.reflect.meta.RoleHandler[] roleHandlers = new spoon.reflect.meta.RoleHandler[]{ new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeAccess_ACCESSED_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtElement_ANNOTATION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAnnotation_ANNOTATION_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAbstractInvocation_ARGUMENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutableReference_ARGUMENT_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAssignment_ASSIGNED_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtRHSReceiver_ASSIGNMENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtBodyHolder_BODY_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtSynchronized_BODY_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtIntersectionTypeReference_BOUND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtWildcardReference_BOUNDING_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAbstractSwitch_CASE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCase_CASE_KIND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExpression_CAST_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTry_CATCH_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtElement_COMMENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtComment_COMMENT_CONTENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtJavaDocTag_COMMENT_CONTENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtJavaDoc_COMMENT_TAG_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtComment_COMMENT_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtConstructor_COMPACT_CONSTRUCTOR_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAssert_CONDITION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtConditional_CONDITION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtIf_CONDITION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtClass_CONSTRUCTOR_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackage_CONTAINED_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_DECLARED_IMPORT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_DECLARED_MODULE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_DECLARED_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutableReference_DECLARING_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFieldReference_DECLARING_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeReference_DECLARING_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtVariable_DEFAULT_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtNewArray_DIMENSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtJavaDocTag_DOCUMENTATION_TYPE_REALNAME_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtConditional_ELSE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtIf_ELSE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModifiable_EMODIFIER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAbstractInvocation_EXECUTABLE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_EXPORTED_PACKAGE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAbstractSwitch_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtArrayAccess_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAssert_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCase_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtDo_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFor_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtForEach_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtLambda_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtNewArray_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtReturn_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtSynchronized_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtThrow_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtUnaryOperator_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtWhile_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtYieldStatement_EXPRESSION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_FIELD_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTry_FINALIZER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtForEach_FOREACH_VARIABLE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFor_FOR_INIT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFor_FOR_UPDATE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtImport_IMPORT_REFERENCE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_INTERFACE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeInformation_INTERFACE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtMethod_IS_DEFAULT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFieldReference_IS_FINAL_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtElement_IS_IMPLICIT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtLocalVariable_IS_INFERRED_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtParameter_IS_INFERRED_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtShadowable_IS_SHADOW_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutableReference_IS_STATIC_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFieldReference_IS_STATIC_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtWildcardReference_IS_UPPER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtParameter_IS_VARARGS_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtStatement_LABEL_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtBinaryOperator_LEFT_OPERAND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtLiteral_LITERAL_BASE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_METHOD_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModifiable_MODIFIER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_MODIFIER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModuleRequirement_MODIFIER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeInformation_MODIFIER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_MODULE_DIRECTIVE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModuleRequirement_MODULE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackageExport_MODULE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtMultiTypedElement_MULTI_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtNamedElement_NAME_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtReference_NAME_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtNewClass_NESTED_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_NESTED_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_OPENED_PACKAGE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackageExport_OPENED_PACKAGE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtBinaryOperator_OPERATOR_KIND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtOperatorAssignment_OPERATOR_KIND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtUnaryOperator_OPERATOR_KIND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackageDeclaration_PACKAGE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackageExport_PACKAGE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeReference_PACKAGE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCatch_PARAMETER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutable_PARAMETER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtElement_POSITION_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_PROVIDED_SERVICE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtRecord_RECORD_COMPONENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_REQUIRED_MODULE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtBinaryOperator_RIGHT_OPERAND_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_SERVICE_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtProvidedService_SERVICE_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtUsedService_SERVICE_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtCodeSnippet_SNIPPET_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtStatementList_STATEMENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtModule_SUB_PACKAGE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtPackage_SUB_PACKAGE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_SUPER_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeInformation_SUPER_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTargetedExpression_TARGET_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtLabelledFlowBreak_TARGET_LABEL_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtConditional_THEN_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtIf_THEN_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutable_THROWN_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTryWithResource_TRY_RESOURCE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtArrayTypeReference_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtExecutableReference_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypedElement_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtVariableReference_TYPE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtType_TYPE_MEMBER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtAnnotation_VALUE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtEnum_VALUE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtLiteral_VALUE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTextBlock_VALUE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtTypePattern_VARIABLE_RoleHandler(), new spoon.reflect.meta.impl.ModelRoleHandlers.CtVariableAccess_VARIABLE_RoleHandler() };

	static class CtVariableAccess_VARIABLE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtVariableAccess, spoon.reflect.reference.CtVariableReference<?>> {
		private CtVariableAccess_VARIABLE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VARIABLE, spoon.reflect.code.CtVariableAccess.class, spoon.reflect.reference.CtVariableReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getVariable())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVariable(castValue(value));
		}
	}

	static class CtTypePattern_VARIABLE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtTypePattern, spoon.reflect.code.CtLocalVariable<? extends java.lang.Object>> {
		private CtTypePattern_VARIABLE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VARIABLE, spoon.reflect.code.CtTypePattern.class, spoon.reflect.code.CtLocalVariable.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getVariable())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVariable(castValue(value));
		}
	}

	static class CtTextBlock_VALUE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtTextBlock, java.lang.String> {
		private CtTextBlock_VALUE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VALUE, spoon.reflect.code.CtTextBlock.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getValue())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValue(castValue(value));
		}
	}

	static class CtLiteral_VALUE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtLiteral, java.lang.Object> {
		private CtLiteral_VALUE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VALUE, spoon.reflect.code.CtLiteral.class, java.lang.Object.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getValue())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValue(castValue(value));
		}
	}

	static class CtEnum_VALUE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtEnum, spoon.reflect.declaration.CtEnumValue<? extends java.lang.Object>> {
		private CtEnum_VALUE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VALUE, spoon.reflect.declaration.CtEnum.class, spoon.reflect.declaration.CtEnumValue.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getEnumValues())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setEnumValues(castValue(value));
		}
	}

	static class CtAnnotation_VALUE_RoleHandler extends spoon.reflect.meta.impl.MapHandler<spoon.reflect.declaration.CtAnnotation, spoon.reflect.code.CtExpression> {
		private CtAnnotation_VALUE_RoleHandler() {
			super(spoon.reflect.path.CtRole.VALUE, spoon.reflect.declaration.CtAnnotation.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getValues())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValues(castValue(value));
		}
	}

	static class CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtTypeMemberWildcardImportReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtTypeMemberWildcardImportReference_TYPE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE_REF, spoon.reflect.reference.CtTypeMemberWildcardImportReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTypeReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeReference(castValue(value));
		}
	}

	static class CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtFormalTypeDeclarer, spoon.reflect.declaration.CtTypeParameter> {
		private CtFormalTypeDeclarer_TYPE_PARAMETER_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE_PARAMETER, spoon.reflect.declaration.CtFormalTypeDeclarer.class, spoon.reflect.declaration.CtTypeParameter.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getFormalCtTypeParameters())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFormalCtTypeParameters(castValue(value));
		}
	}

	static class CtType_TYPE_MEMBER_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtType, spoon.reflect.declaration.CtTypeMember> {
		private CtType_TYPE_MEMBER_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE_MEMBER, spoon.reflect.declaration.CtType.class, spoon.reflect.declaration.CtTypeMember.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTypeMembers())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeMembers(castValue(value));
		}
	}

	static class CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.reference.CtActualTypeContainer, spoon.reflect.reference.CtTypeReference<?>> {
		private CtActualTypeContainer_TYPE_ARGUMENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE_ARGUMENT, spoon.reflect.reference.CtActualTypeContainer.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getActualTypeArguments())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setActualTypeArguments(castValue(value));
		}
	}

	static class CtVariableReference_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtVariableReference, spoon.reflect.reference.CtTypeReference<?>> {
		private CtVariableReference_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE, spoon.reflect.reference.CtVariableReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtTypedElement_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtTypedElement, spoon.reflect.reference.CtTypeReference<?>> {
		private CtTypedElement_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE, spoon.reflect.declaration.CtTypedElement.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtExecutableReference_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtExecutableReference, spoon.reflect.reference.CtTypeReference<?>> {
		private CtExecutableReference_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE, spoon.reflect.reference.CtExecutableReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtArrayTypeReference_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtArrayTypeReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtArrayTypeReference_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.TYPE, spoon.reflect.reference.CtArrayTypeReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getComponentType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setComponentType(castValue(value));
		}
	}

	static class CtTryWithResource_TRY_RESOURCE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtTryWithResource, spoon.reflect.code.CtResource<? extends java.lang.Object>> {
		private CtTryWithResource_TRY_RESOURCE_RoleHandler() {
			super(spoon.reflect.path.CtRole.TRY_RESOURCE, spoon.reflect.code.CtTryWithResource.class, spoon.reflect.code.CtResource.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getResources())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setResources(castValue(value));
		}
	}

	static class CtExecutable_THROWN_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtExecutable, spoon.reflect.reference.CtTypeReference<? extends java.lang.Throwable>> {
		private CtExecutable_THROWN_RoleHandler() {
			super(spoon.reflect.path.CtRole.THROWN, spoon.reflect.declaration.CtExecutable.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getThrownTypes())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThrownTypes(castValue(value));
		}
	}

	static class CtIf_THEN_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtIf, spoon.reflect.code.CtStatement> {
		private CtIf_THEN_RoleHandler() {
			super(spoon.reflect.path.CtRole.THEN, spoon.reflect.code.CtIf.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getThenStatement())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThenStatement(castValue(value));
		}
	}

	static class CtConditional_THEN_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtConditional, spoon.reflect.code.CtExpression<?>> {
		private CtConditional_THEN_RoleHandler() {
			super(spoon.reflect.path.CtRole.THEN, spoon.reflect.code.CtConditional.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getThenExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThenExpression(castValue(value));
		}
	}

	static class CtLabelledFlowBreak_TARGET_LABEL_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtLabelledFlowBreak, java.lang.String> {
		private CtLabelledFlowBreak_TARGET_LABEL_RoleHandler() {
			super(spoon.reflect.path.CtRole.TARGET_LABEL, spoon.reflect.code.CtLabelledFlowBreak.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTargetLabel())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTargetLabel(castValue(value));
		}
	}

	static class CtTargetedExpression_TARGET_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtTargetedExpression, spoon.reflect.code.CtExpression<?>> {
		private CtTargetedExpression_TARGET_RoleHandler() {
			super(spoon.reflect.path.CtRole.TARGET, spoon.reflect.code.CtTargetedExpression.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTarget())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTarget(castValue(value));
		}
	}

	static class CtTypeInformation_SUPER_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtTypeInformation, spoon.reflect.reference.CtTypeReference<?>> {
		private CtTypeInformation_SUPER_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SUPER_TYPE, spoon.reflect.declaration.CtTypeInformation.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSuperclass())));
		}
	}

	static class CtType_SUPER_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtType, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtType_SUPER_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SUPER_TYPE, spoon.reflect.declaration.CtType.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSuperclass())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSuperclass(castValue(value));
		}
	}

	static class CtPackage_SUB_PACKAGE_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtPackage, spoon.reflect.declaration.CtPackage> {
		private CtPackage_SUB_PACKAGE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SUB_PACKAGE, spoon.reflect.declaration.CtPackage.class, spoon.reflect.declaration.CtPackage.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getPackages())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackages(castValue(value));
		}
	}

	static class CtModule_SUB_PACKAGE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtPackage> {
		private CtModule_SUB_PACKAGE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SUB_PACKAGE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtPackage.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRootPackage())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRootPackage(castValue(value));
		}
	}

	static class CtStatementList_STATEMENT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtStatementList, spoon.reflect.code.CtStatement> {
		private CtStatementList_STATEMENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.STATEMENT, spoon.reflect.code.CtStatementList.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getStatements())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatements(castValue(value));
		}
	}

	static class CtCodeSnippet_SNIPPET_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtCodeSnippet, java.lang.String> {
		private CtCodeSnippet_SNIPPET_RoleHandler() {
			super(spoon.reflect.path.CtRole.SNIPPET, spoon.reflect.declaration.CtCodeSnippet.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getValue())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setValue(castValue(value));
		}
	}

	static class CtUsedService_SERVICE_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtUsedService, spoon.reflect.reference.CtTypeReference> {
		private CtUsedService_SERVICE_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SERVICE_TYPE, spoon.reflect.declaration.CtUsedService.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getServiceType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setServiceType(castValue(value));
		}
	}

	static class CtProvidedService_SERVICE_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtProvidedService, spoon.reflect.reference.CtTypeReference> {
		private CtProvidedService_SERVICE_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SERVICE_TYPE, spoon.reflect.declaration.CtProvidedService.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getServiceType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setServiceType(castValue(value));
		}
	}

	static class CtModule_SERVICE_TYPE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtUsedService> {
		private CtModule_SERVICE_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.SERVICE_TYPE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtUsedService.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getUsedServices())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setUsedServices(castValue(value));
		}
	}

	static class CtBinaryOperator_RIGHT_OPERAND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtBinaryOperator, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtBinaryOperator_RIGHT_OPERAND_RoleHandler() {
			super(spoon.reflect.path.CtRole.RIGHT_OPERAND, spoon.reflect.code.CtBinaryOperator.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRightHandOperand())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRightHandOperand(castValue(value));
		}
	}

	static class CtModule_REQUIRED_MODULE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtModuleRequirement> {
		private CtModule_REQUIRED_MODULE_RoleHandler() {
			super(spoon.reflect.path.CtRole.REQUIRED_MODULE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtModuleRequirement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRequiredModules())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRequiredModules(castValue(value));
		}
	}

	static class CtRecord_RECORD_COMPONENT_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtRecord, spoon.reflect.declaration.CtRecordComponent> {
		private CtRecord_RECORD_COMPONENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.RECORD_COMPONENT, spoon.reflect.declaration.CtRecord.class, spoon.reflect.declaration.CtRecordComponent.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRecordComponents())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRecordComponents(castValue(value));
		}
	}

	static class CtModule_PROVIDED_SERVICE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtProvidedService> {
		private CtModule_PROVIDED_SERVICE_RoleHandler() {
			super(spoon.reflect.path.CtRole.PROVIDED_SERVICE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtProvidedService.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getProvidedServices())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setProvidedServices(castValue(value));
		}
	}

	static class CtElement_POSITION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtElement, spoon.reflect.cu.SourcePosition> {
		private CtElement_POSITION_RoleHandler() {
			super(spoon.reflect.path.CtRole.POSITION, spoon.reflect.declaration.CtElement.class, spoon.reflect.cu.SourcePosition.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getPosition())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPosition(castValue(value));
		}
	}

	static class CtExecutable_PARAMETER_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtExecutable, spoon.reflect.declaration.CtParameter<? extends java.lang.Object>> {
		private CtExecutable_PARAMETER_RoleHandler() {
			super(spoon.reflect.path.CtRole.PARAMETER, spoon.reflect.declaration.CtExecutable.class, spoon.reflect.declaration.CtParameter.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getParameters())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameters(castValue(value));
		}
	}

	static class CtCatch_PARAMETER_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtCatch, spoon.reflect.code.CtCatchVariable<? extends java.lang.Throwable>> {
		private CtCatch_PARAMETER_RoleHandler() {
			super(spoon.reflect.path.CtRole.PARAMETER, spoon.reflect.code.CtCatch.class, spoon.reflect.code.CtCatchVariable.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getParameter())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameter(castValue(value));
		}
	}

	static class CtTypeReference_PACKAGE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtTypeReference, spoon.reflect.reference.CtPackageReference> {
		private CtTypeReference_PACKAGE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.PACKAGE_REF, spoon.reflect.reference.CtTypeReference.class, spoon.reflect.reference.CtPackageReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getPackage())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackage(castValue(value));
		}
	}

	static class CtPackageExport_PACKAGE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtPackageExport, spoon.reflect.reference.CtPackageReference> {
		private CtPackageExport_PACKAGE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.PACKAGE_REF, spoon.reflect.declaration.CtPackageExport.class, spoon.reflect.reference.CtPackageReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getPackageReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackageReference(castValue(value));
		}
	}

	static class CtPackageDeclaration_PACKAGE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtPackageDeclaration, spoon.reflect.reference.CtPackageReference> {
		private CtPackageDeclaration_PACKAGE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.PACKAGE_REF, spoon.reflect.declaration.CtPackageDeclaration.class, spoon.reflect.reference.CtPackageReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReference(castValue(value));
		}
	}

	static class CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.declaration.CtPackageDeclaration> {
		private CtCompilationUnit_PACKAGE_DECLARATION_RoleHandler() {
			super(spoon.reflect.path.CtRole.PACKAGE_DECLARATION, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.declaration.CtPackageDeclaration.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getPackageDeclaration())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setPackageDeclaration(castValue(value));
		}
	}

	static class CtUnaryOperator_OPERATOR_KIND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtUnaryOperator, spoon.reflect.code.UnaryOperatorKind> {
		private CtUnaryOperator_OPERATOR_KIND_RoleHandler() {
			super(spoon.reflect.path.CtRole.OPERATOR_KIND, spoon.reflect.code.CtUnaryOperator.class, spoon.reflect.code.UnaryOperatorKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getKind())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtOperatorAssignment_OPERATOR_KIND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtOperatorAssignment, spoon.reflect.code.BinaryOperatorKind> {
		private CtOperatorAssignment_OPERATOR_KIND_RoleHandler() {
			super(spoon.reflect.path.CtRole.OPERATOR_KIND, spoon.reflect.code.CtOperatorAssignment.class, spoon.reflect.code.BinaryOperatorKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getKind())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtBinaryOperator_OPERATOR_KIND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtBinaryOperator, spoon.reflect.code.BinaryOperatorKind> {
		private CtBinaryOperator_OPERATOR_KIND_RoleHandler() {
			super(spoon.reflect.path.CtRole.OPERATOR_KIND, spoon.reflect.code.CtBinaryOperator.class, spoon.reflect.code.BinaryOperatorKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getKind())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setKind(castValue(value));
		}
	}

	static class CtPackageExport_OPENED_PACKAGE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtPackageExport, java.lang.Boolean> {
		private CtPackageExport_OPENED_PACKAGE_RoleHandler() {
			super(spoon.reflect.path.CtRole.OPENED_PACKAGE, spoon.reflect.declaration.CtPackageExport.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isOpenedPackage())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOpenedPackage(castValue(value));
		}
	}

	static class CtModule_OPENED_PACKAGE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtPackageExport> {
		private CtModule_OPENED_PACKAGE_RoleHandler() {
			super(spoon.reflect.path.CtRole.OPENED_PACKAGE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtPackageExport.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getOpenedPackages())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOpenedPackages(castValue(value));
		}
	}

	static class CtType_NESTED_TYPE_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtType, spoon.reflect.declaration.CtType<? extends java.lang.Object>> {
		private CtType_NESTED_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.NESTED_TYPE, spoon.reflect.declaration.CtType.class, spoon.reflect.declaration.CtType.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getNestedTypes())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setNestedTypes(castValue(value));
		}
	}

	static class CtNewClass_NESTED_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtNewClass, spoon.reflect.declaration.CtClass<? extends java.lang.Object>> {
		private CtNewClass_NESTED_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.NESTED_TYPE, spoon.reflect.code.CtNewClass.class, spoon.reflect.declaration.CtClass.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAnonymousClass())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnonymousClass(castValue(value));
		}
	}

	static class CtReference_NAME_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtReference, java.lang.String> {
		private CtReference_NAME_RoleHandler() {
			super(spoon.reflect.path.CtRole.NAME, spoon.reflect.reference.CtReference.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSimpleName())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSimpleName(castValue(value));
		}
	}

	static class CtNamedElement_NAME_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtNamedElement, java.lang.String> {
		private CtNamedElement_NAME_RoleHandler() {
			super(spoon.reflect.path.CtRole.NAME, spoon.reflect.declaration.CtNamedElement.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSimpleName())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSimpleName(castValue(value));
		}
	}

	static class CtMultiTypedElement_MULTI_TYPE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtMultiTypedElement, spoon.reflect.reference.CtTypeReference<?>> {
		private CtMultiTypedElement_MULTI_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.MULTI_TYPE, spoon.reflect.declaration.CtMultiTypedElement.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getMultiTypes())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setMultiTypes(castValue(value));
		}
	}

	static class CtPackageExport_MODULE_REF_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtPackageExport, spoon.reflect.reference.CtModuleReference> {
		private CtPackageExport_MODULE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODULE_REF, spoon.reflect.declaration.CtPackageExport.class, spoon.reflect.reference.CtModuleReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTargetExport())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTargetExport(castValue(value));
		}
	}

	static class CtModuleRequirement_MODULE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtModuleRequirement, spoon.reflect.reference.CtModuleReference> {
		private CtModuleRequirement_MODULE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODULE_REF, spoon.reflect.declaration.CtModuleRequirement.class, spoon.reflect.reference.CtModuleReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getModuleReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModuleReference(castValue(value));
		}
	}

	static class CtModule_MODULE_DIRECTIVE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtModuleDirective> {
		private CtModule_MODULE_DIRECTIVE_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODULE_DIRECTIVE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtModuleDirective.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getModuleDirectives())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModuleDirectives(castValue(value));
		}
	}

	static class CtTypeInformation_MODIFIER_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtTypeInformation, spoon.reflect.declaration.ModifierKind> {
		private CtTypeInformation_MODIFIER_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODIFIER, spoon.reflect.declaration.CtTypeInformation.class, spoon.reflect.declaration.ModifierKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getModifiers())));
		}
	}

	static class CtModuleRequirement_MODIFIER_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtModuleRequirement, spoon.reflect.declaration.CtModuleRequirement.RequiresModifier> {
		private CtModuleRequirement_MODIFIER_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODIFIER, spoon.reflect.declaration.CtModuleRequirement.class, spoon.reflect.declaration.CtModuleRequirement.RequiresModifier.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRequiresModifiers())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRequiresModifiers(castValue(value));
		}
	}

	static class CtModule_MODIFIER_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtModule, java.lang.Boolean> {
		private CtModule_MODIFIER_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODIFIER, spoon.reflect.declaration.CtModule.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isOpenModule())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setIsOpenModule(castValue(value));
		}
	}

	static class CtModifiable_MODIFIER_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtModifiable, spoon.reflect.declaration.ModifierKind> {
		private CtModifiable_MODIFIER_RoleHandler() {
			super(spoon.reflect.path.CtRole.MODIFIER, spoon.reflect.declaration.CtModifiable.class, spoon.reflect.declaration.ModifierKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getModifiers())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setModifiers(castValue(value));
		}
	}

	static class CtType_METHOD_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtType, spoon.reflect.declaration.CtMethod<? extends java.lang.Object>> {
		private CtType_METHOD_RoleHandler() {
			super(spoon.reflect.path.CtRole.METHOD, spoon.reflect.declaration.CtType.class, spoon.reflect.declaration.CtMethod.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getMethods())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setMethods(castValue(value));
		}
	}

	static class CtLiteral_LITERAL_BASE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtLiteral, spoon.reflect.code.LiteralBase> {
		private CtLiteral_LITERAL_BASE_RoleHandler() {
			super(spoon.reflect.path.CtRole.LITERAL_BASE, spoon.reflect.code.CtLiteral.class, spoon.reflect.code.LiteralBase.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getBase())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBase(castValue(value));
		}
	}

	static class CtBinaryOperator_LEFT_OPERAND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtBinaryOperator, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtBinaryOperator_LEFT_OPERAND_RoleHandler() {
			super(spoon.reflect.path.CtRole.LEFT_OPERAND, spoon.reflect.code.CtBinaryOperator.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getLeftHandOperand())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLeftHandOperand(castValue(value));
		}
	}

	static class CtStatement_LABEL_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtStatement, java.lang.String> {
		private CtStatement_LABEL_RoleHandler() {
			super(spoon.reflect.path.CtRole.LABEL, spoon.reflect.code.CtStatement.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getLabel())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLabel(castValue(value));
		}
	}

	static class CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtJavaDocTag, java.lang.String> {
		private CtJavaDocTag_JAVADOC_TAG_VALUE_RoleHandler() {
			super(spoon.reflect.path.CtRole.JAVADOC_TAG_VALUE, spoon.reflect.code.CtJavaDocTag.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getParam())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParam(castValue(value));
		}
	}

	static class CtParameter_IS_VARARGS_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtParameter, java.lang.Boolean> {
		private CtParameter_IS_VARARGS_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_VARARGS, spoon.reflect.declaration.CtParameter.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isVarArgs())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVarArgs(castValue(value));
		}
	}

	static class CtWildcardReference_IS_UPPER_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtWildcardReference, java.lang.Boolean> {
		private CtWildcardReference_IS_UPPER_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_UPPER, spoon.reflect.reference.CtWildcardReference.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isUpper())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setUpper(castValue(value));
		}
	}

	static class CtFieldReference_IS_STATIC_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtFieldReference, java.lang.Boolean> {
		private CtFieldReference_IS_STATIC_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_STATIC, spoon.reflect.reference.CtFieldReference.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isStatic())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatic(castValue(value));
		}
	}

	static class CtExecutableReference_IS_STATIC_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtExecutableReference, java.lang.Boolean> {
		private CtExecutableReference_IS_STATIC_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_STATIC, spoon.reflect.reference.CtExecutableReference.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isStatic())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setStatic(castValue(value));
		}
	}

	static class CtShadowable_IS_SHADOW_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtShadowable, java.lang.Boolean> {
		private CtShadowable_IS_SHADOW_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_SHADOW, spoon.reflect.declaration.CtShadowable.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isShadow())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setShadow(castValue(value));
		}
	}

	static class CtParameter_IS_INFERRED_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtParameter, java.lang.Boolean> {
		private CtParameter_IS_INFERRED_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_INFERRED, spoon.reflect.declaration.CtParameter.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isInferred())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setInferred(castValue(value));
		}
	}

	static class CtLocalVariable_IS_INFERRED_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtLocalVariable, java.lang.Boolean> {
		private CtLocalVariable_IS_INFERRED_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_INFERRED, spoon.reflect.code.CtLocalVariable.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isInferred())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setInferred(castValue(value));
		}
	}

	static class CtElement_IS_IMPLICIT_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtElement, java.lang.Boolean> {
		private CtElement_IS_IMPLICIT_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_IMPLICIT, spoon.reflect.declaration.CtElement.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isImplicit())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImplicit(castValue(value));
		}
	}

	static class CtFieldReference_IS_FINAL_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtFieldReference, java.lang.Boolean> {
		private CtFieldReference_IS_FINAL_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_FINAL, spoon.reflect.reference.CtFieldReference.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isFinal())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFinal(castValue(value));
		}
	}

	static class CtMethod_IS_DEFAULT_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtMethod, java.lang.Boolean> {
		private CtMethod_IS_DEFAULT_RoleHandler() {
			super(spoon.reflect.path.CtRole.IS_DEFAULT, spoon.reflect.declaration.CtMethod.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isDefaultMethod())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultMethod(castValue(value));
		}
	}

	static class CtTypeInformation_INTERFACE_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtTypeInformation, spoon.reflect.reference.CtTypeReference<?>> {
		private CtTypeInformation_INTERFACE_RoleHandler() {
			super(spoon.reflect.path.CtRole.INTERFACE, spoon.reflect.declaration.CtTypeInformation.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSuperInterfaces())));
		}
	}

	static class CtType_INTERFACE_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtType, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtType_INTERFACE_RoleHandler() {
			super(spoon.reflect.path.CtRole.INTERFACE, spoon.reflect.declaration.CtType.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSuperInterfaces())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSuperInterfaces(castValue(value));
		}
	}

	static class CtImport_IMPORT_REFERENCE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtImport, spoon.reflect.reference.CtReference> {
		private CtImport_IMPORT_REFERENCE_RoleHandler() {
			super(spoon.reflect.path.CtRole.IMPORT_REFERENCE, spoon.reflect.declaration.CtImport.class, spoon.reflect.reference.CtReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReference(castValue(value));
		}
	}

	static class CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtProvidedService, spoon.reflect.reference.CtTypeReference> {
		private CtProvidedService_IMPLEMENTATION_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.IMPLEMENTATION_TYPE, spoon.reflect.declaration.CtProvidedService.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getImplementationTypes())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImplementationTypes(castValue(value));
		}
	}

	static class CtFor_FOR_UPDATE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtFor, spoon.reflect.code.CtStatement> {
		private CtFor_FOR_UPDATE_RoleHandler() {
			super(spoon.reflect.path.CtRole.FOR_UPDATE, spoon.reflect.code.CtFor.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getForUpdate())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setForUpdate(castValue(value));
		}
	}

	static class CtFor_FOR_INIT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtFor, spoon.reflect.code.CtStatement> {
		private CtFor_FOR_INIT_RoleHandler() {
			super(spoon.reflect.path.CtRole.FOR_INIT, spoon.reflect.code.CtFor.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getForInit())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setForInit(castValue(value));
		}
	}

	static class CtForEach_FOREACH_VARIABLE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtForEach, spoon.reflect.code.CtLocalVariable<? extends java.lang.Object>> {
		private CtForEach_FOREACH_VARIABLE_RoleHandler() {
			super(spoon.reflect.path.CtRole.FOREACH_VARIABLE, spoon.reflect.code.CtForEach.class, spoon.reflect.code.CtLocalVariable.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getVariable())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setVariable(castValue(value));
		}
	}

	static class CtTry_FINALIZER_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtTry, spoon.reflect.code.CtBlock<? extends java.lang.Object>> {
		private CtTry_FINALIZER_RoleHandler() {
			super(spoon.reflect.path.CtRole.FINALIZER, spoon.reflect.code.CtTry.class, spoon.reflect.code.CtBlock.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getFinalizer())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFinalizer(castValue(value));
		}
	}

	static class CtType_FIELD_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtType, spoon.reflect.declaration.CtField<? extends java.lang.Object>> {
		private CtType_FIELD_RoleHandler() {
			super(spoon.reflect.path.CtRole.FIELD, spoon.reflect.declaration.CtType.class, spoon.reflect.declaration.CtField.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getFields())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setFields(castValue(value));
		}
	}

	static class CtYieldStatement_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtYieldStatement, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtYieldStatement_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtYieldStatement.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtWhile_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtWhile, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtWhile_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtWhile.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getLoopingExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLoopingExpression(castValue(value));
		}
	}

	static class CtUnaryOperator_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtUnaryOperator, spoon.reflect.code.CtExpression<?>> {
		private CtUnaryOperator_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtUnaryOperator.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getOperand())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setOperand(castValue(value));
		}
	}

	static class CtThrow_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtThrow, spoon.reflect.code.CtExpression<? extends java.lang.Throwable>> {
		private CtThrow_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtThrow.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getThrownExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setThrownExpression(castValue(value));
		}
	}

	static class CtSynchronized_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtSynchronized, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtSynchronized_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtSynchronized.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtReturn_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtReturn, spoon.reflect.code.CtExpression<?>> {
		private CtReturn_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtReturn.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getReturnedExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setReturnedExpression(castValue(value));
		}
	}

	static class CtNewArray_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtNewArray, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtNewArray_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtNewArray.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getElements())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElements(castValue(value));
		}
	}

	static class CtLambda_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtLambda, spoon.reflect.code.CtExpression<?>> {
		private CtLambda_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtLambda.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtForEach_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtForEach, spoon.reflect.code.CtExpression<? extends java.lang.Object>> {
		private CtForEach_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtForEach.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtFor_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtFor, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtFor_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtFor.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtDo_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtDo, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtDo_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtDo.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getLoopingExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setLoopingExpression(castValue(value));
		}
	}

	static class CtCase_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtCase, spoon.reflect.code.CtExpression<?>> {
		private CtCase_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtCase.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCaseExpressions())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCaseExpressions(castValue(value));
		}
	}

	static class CtAssert_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtAssert, spoon.reflect.code.CtExpression<?>> {
		private CtAssert_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtAssert.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExpression(castValue(value));
		}
	}

	static class CtArrayAccess_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtArrayAccess, spoon.reflect.code.CtExpression<java.lang.Integer>> {
		private CtArrayAccess_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtArrayAccess.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getIndexExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setIndexExpression(castValue(value));
		}
	}

	static class CtAbstractSwitch_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtAbstractSwitch, spoon.reflect.code.CtExpression<?>> {
		private CtAbstractSwitch_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPRESSION, spoon.reflect.code.CtAbstractSwitch.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getSelector())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setSelector(castValue(value));
		}
	}

	static class CtModule_EXPORTED_PACKAGE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtModule, spoon.reflect.declaration.CtPackageExport> {
		private CtModule_EXPORTED_PACKAGE_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXPORTED_PACKAGE, spoon.reflect.declaration.CtModule.class, spoon.reflect.declaration.CtPackageExport.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExportedPackages())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExportedPackages(castValue(value));
		}
	}

	static class CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtExecutableReferenceExpression, spoon.reflect.reference.CtExecutableReference<?>> {
		private CtExecutableReferenceExpression_EXECUTABLE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXECUTABLE_REF, spoon.reflect.code.CtExecutableReferenceExpression.class, spoon.reflect.reference.CtExecutableReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExecutable())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExecutable(castValue(value));
		}
	}

	static class CtAbstractInvocation_EXECUTABLE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtAbstractInvocation, spoon.reflect.reference.CtExecutableReference<?>> {
		private CtAbstractInvocation_EXECUTABLE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.EXECUTABLE_REF, spoon.reflect.code.CtAbstractInvocation.class, spoon.reflect.reference.CtExecutableReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExecutable())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setExecutable(castValue(value));
		}
	}

	static class CtModifiable_EMODIFIER_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtModifiable, spoon.support.reflect.CtExtendedModifier> {
		private CtModifiable_EMODIFIER_RoleHandler() {
			super(spoon.reflect.path.CtRole.EMODIFIER, spoon.reflect.declaration.CtModifiable.class, spoon.support.reflect.CtExtendedModifier.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getExtendedModifiers())));
		}
	}

	static class CtIf_ELSE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtIf, spoon.reflect.code.CtStatement> {
		private CtIf_ELSE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ELSE, spoon.reflect.code.CtIf.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getElseStatement())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElseStatement(castValue(value));
		}
	}

	static class CtConditional_ELSE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtConditional, spoon.reflect.code.CtExpression<?>> {
		private CtConditional_ELSE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ELSE, spoon.reflect.code.CtConditional.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getElseExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setElseExpression(castValue(value));
		}
	}

	static class CtJavaDocTag_DOCUMENTATION_TYPE_REALNAME_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtJavaDocTag, java.lang.String> {
		private CtJavaDocTag_DOCUMENTATION_TYPE_REALNAME_RoleHandler() {
			super(spoon.reflect.path.CtRole.DOCUMENTATION_TYPE_REALNAME, spoon.reflect.code.CtJavaDocTag.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getRealName())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setRealName(castValue(value));
		}
	}

	static class CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtJavaDocTag, spoon.reflect.code.CtJavaDocTag.TagType> {
		private CtJavaDocTag_DOCUMENTATION_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DOCUMENTATION_TYPE, spoon.reflect.code.CtJavaDocTag.class, spoon.reflect.code.CtJavaDocTag.TagType.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setType(castValue(value));
		}
	}

	static class CtNewArray_DIMENSION_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtNewArray, spoon.reflect.code.CtExpression<java.lang.Integer>> {
		private CtNewArray_DIMENSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.DIMENSION, spoon.reflect.code.CtNewArray.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDimensionExpressions())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDimensionExpressions(castValue(value));
		}
	}

	static class CtVariable_DEFAULT_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtVariable, spoon.reflect.code.CtExpression<?>> {
		private CtVariable_DEFAULT_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.DEFAULT_EXPRESSION, spoon.reflect.declaration.CtVariable.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDefaultExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultExpression(castValue(value));
		}
	}

	static class CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtAnnotationMethod, spoon.reflect.code.CtExpression<?>> {
		private CtAnnotationMethod_DEFAULT_EXPRESSION_RoleHandler() {
			super(spoon.reflect.path.CtRole.DEFAULT_EXPRESSION, spoon.reflect.declaration.CtAnnotationMethod.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDefaultExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDefaultExpression(castValue(value));
		}
	}

	static class CtTypeReference_DECLARING_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtTypeReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtTypeReference_DECLARING_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARING_TYPE, spoon.reflect.reference.CtTypeReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaringType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtFieldReference_DECLARING_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtFieldReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtFieldReference_DECLARING_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARING_TYPE, spoon.reflect.reference.CtFieldReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaringType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtExecutableReference_DECLARING_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtExecutableReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtExecutableReference_DECLARING_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARING_TYPE, spoon.reflect.reference.CtExecutableReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaringType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaringType(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtCompilationUnit_DECLARED_TYPE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARED_TYPE_REF, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaredTypeReferences())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaredTypeReferences(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_TYPE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.declaration.CtType<? extends java.lang.Object>> {
		private CtCompilationUnit_DECLARED_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARED_TYPE, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.declaration.CtType.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaredTypes())));
		}
	}

	static class CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.reference.CtModuleReference> {
		private CtCompilationUnit_DECLARED_MODULE_REF_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARED_MODULE_REF, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.reference.CtModuleReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaredModuleReference())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setDeclaredModuleReference(castValue(value));
		}
	}

	static class CtCompilationUnit_DECLARED_MODULE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.declaration.CtModule> {
		private CtCompilationUnit_DECLARED_MODULE_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARED_MODULE, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.declaration.CtModule.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getDeclaredModule())));
		}
	}

	static class CtCompilationUnit_DECLARED_IMPORT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtCompilationUnit, spoon.reflect.declaration.CtImport> {
		private CtCompilationUnit_DECLARED_IMPORT_RoleHandler() {
			super(spoon.reflect.path.CtRole.DECLARED_IMPORT, spoon.reflect.declaration.CtCompilationUnit.class, spoon.reflect.declaration.CtImport.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getImports())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setImports(castValue(value));
		}
	}

	static class CtPackage_CONTAINED_TYPE_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtPackage, spoon.reflect.declaration.CtType<? extends java.lang.Object>> {
		private CtPackage_CONTAINED_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.CONTAINED_TYPE, spoon.reflect.declaration.CtPackage.class, spoon.reflect.declaration.CtType.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTypes())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypes(castValue(value));
		}
	}

	static class CtClass_CONSTRUCTOR_RoleHandler extends spoon.reflect.meta.impl.SetHandler<spoon.reflect.declaration.CtClass, spoon.reflect.declaration.CtConstructor<?>> {
		private CtClass_CONSTRUCTOR_RoleHandler() {
			super(spoon.reflect.path.CtRole.CONSTRUCTOR, spoon.reflect.declaration.CtClass.class, spoon.reflect.declaration.CtConstructor.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getConstructors())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setConstructors(castValue(value));
		}
	}

	static class CtIf_CONDITION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtIf, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtIf_CONDITION_RoleHandler() {
			super(spoon.reflect.path.CtRole.CONDITION, spoon.reflect.code.CtIf.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCondition())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCondition(castValue(value));
		}
	}

	static class CtConditional_CONDITION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtConditional, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtConditional_CONDITION_RoleHandler() {
			super(spoon.reflect.path.CtRole.CONDITION, spoon.reflect.code.CtConditional.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCondition())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCondition(castValue(value));
		}
	}

	static class CtAssert_CONDITION_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtAssert, spoon.reflect.code.CtExpression<java.lang.Boolean>> {
		private CtAssert_CONDITION_RoleHandler() {
			super(spoon.reflect.path.CtRole.CONDITION, spoon.reflect.code.CtAssert.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAssertExpression())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssertExpression(castValue(value));
		}
	}

	static class CtConstructor_COMPACT_CONSTRUCTOR_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtConstructor, java.lang.Boolean> {
		private CtConstructor_COMPACT_CONSTRUCTOR_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMPACT_CONSTRUCTOR, spoon.reflect.declaration.CtConstructor.class, java.lang.Boolean.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).isCompactConstructor())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCompactConstructor(castValue(value));
		}
	}

	static class CtComment_COMMENT_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtComment, spoon.reflect.code.CtComment.CommentType> {
		private CtComment_COMMENT_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMMENT_TYPE, spoon.reflect.code.CtComment.class, spoon.reflect.code.CtComment.CommentType.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCommentType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCommentType(castValue(value));
		}
	}

	static class CtJavaDoc_COMMENT_TAG_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtJavaDoc, spoon.reflect.code.CtJavaDocTag> {
		private CtJavaDoc_COMMENT_TAG_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMMENT_TAG, spoon.reflect.code.CtJavaDoc.class, spoon.reflect.code.CtJavaDocTag.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTags())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTags(castValue(value));
		}
	}

	static class CtJavaDocTag_COMMENT_CONTENT_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtJavaDocTag, java.lang.String> {
		private CtJavaDocTag_COMMENT_CONTENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMMENT_CONTENT, spoon.reflect.code.CtJavaDocTag.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getContent())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setContent(castValue(value));
		}
	}

	static class CtComment_COMMENT_CONTENT_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtComment, java.lang.String> {
		private CtComment_COMMENT_CONTENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMMENT_CONTENT, spoon.reflect.code.CtComment.class, java.lang.String.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getContent())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setContent(castValue(value));
		}
	}

	static class CtElement_COMMENT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtElement, spoon.reflect.code.CtComment> {
		private CtElement_COMMENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.COMMENT, spoon.reflect.declaration.CtElement.class, spoon.reflect.code.CtComment.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getComments())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setComments(castValue(value));
		}
	}

	static class CtTry_CATCH_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtTry, spoon.reflect.code.CtCatch> {
		private CtTry_CATCH_RoleHandler() {
			super(spoon.reflect.path.CtRole.CATCH, spoon.reflect.code.CtTry.class, spoon.reflect.code.CtCatch.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCatchers())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCatchers(castValue(value));
		}
	}

	static class CtExpression_CAST_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtExpression, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtExpression_CAST_RoleHandler() {
			super(spoon.reflect.path.CtRole.CAST, spoon.reflect.code.CtExpression.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getTypeCasts())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setTypeCasts(castValue(value));
		}
	}

	static class CtCase_CASE_KIND_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtCase, spoon.reflect.code.CaseKind> {
		private CtCase_CASE_KIND_RoleHandler() {
			super(spoon.reflect.path.CtRole.CASE_KIND, spoon.reflect.code.CtCase.class, spoon.reflect.code.CaseKind.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCaseKind())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCaseKind(castValue(value));
		}
	}

	static class CtAbstractSwitch_CASE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtAbstractSwitch, spoon.reflect.code.CtCase<?>> {
		private CtAbstractSwitch_CASE_RoleHandler() {
			super(spoon.reflect.path.CtRole.CASE, spoon.reflect.code.CtAbstractSwitch.class, spoon.reflect.code.CtCase.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getCases())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setCases(castValue(value));
		}
	}

	static class CtWildcardReference_BOUNDING_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.reference.CtWildcardReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtWildcardReference_BOUNDING_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.BOUNDING_TYPE, spoon.reflect.reference.CtWildcardReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getBoundingType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBoundingType(castValue(value));
		}
	}

	static class CtIntersectionTypeReference_BOUND_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.reference.CtIntersectionTypeReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtIntersectionTypeReference_BOUND_RoleHandler() {
			super(spoon.reflect.path.CtRole.BOUND, spoon.reflect.reference.CtIntersectionTypeReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getBounds())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBounds(castValue(value));
		}
	}

	static class CtSynchronized_BODY_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtSynchronized, spoon.reflect.code.CtBlock<? extends java.lang.Object>> {
		private CtSynchronized_BODY_RoleHandler() {
			super(spoon.reflect.path.CtRole.BODY, spoon.reflect.code.CtSynchronized.class, spoon.reflect.code.CtBlock.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getBlock())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBlock(castValue(value));
		}
	}

	static class CtBodyHolder_BODY_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtBodyHolder, spoon.reflect.code.CtStatement> {
		private CtBodyHolder_BODY_RoleHandler() {
			super(spoon.reflect.path.CtRole.BODY, spoon.reflect.code.CtBodyHolder.class, spoon.reflect.code.CtStatement.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getBody())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setBody(castValue(value));
		}
	}

	static class CtRHSReceiver_ASSIGNMENT_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtRHSReceiver, spoon.reflect.code.CtExpression<?>> {
		private CtRHSReceiver_ASSIGNMENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.ASSIGNMENT, spoon.reflect.code.CtRHSReceiver.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAssignment())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssignment(castValue(value));
		}
	}

	static class CtAssignment_ASSIGNED_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtAssignment, spoon.reflect.code.CtExpression<?>> {
		private CtAssignment_ASSIGNED_RoleHandler() {
			super(spoon.reflect.path.CtRole.ASSIGNED, spoon.reflect.code.CtAssignment.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAssigned())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAssigned(castValue(value));
		}
	}

	static class CtExecutableReference_ARGUMENT_TYPE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.reference.CtExecutableReference, spoon.reflect.reference.CtTypeReference<? extends java.lang.Object>> {
		private CtExecutableReference_ARGUMENT_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ARGUMENT_TYPE, spoon.reflect.reference.CtExecutableReference.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getParameters())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setParameters(castValue(value));
		}
	}

	static class CtAbstractInvocation_ARGUMENT_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.code.CtAbstractInvocation, spoon.reflect.code.CtExpression<?>> {
		private CtAbstractInvocation_ARGUMENT_RoleHandler() {
			super(spoon.reflect.path.CtRole.ARGUMENT, spoon.reflect.code.CtAbstractInvocation.class, spoon.reflect.code.CtExpression.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getArguments())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setArguments(castValue(value));
		}
	}

	static class CtAnnotation_ANNOTATION_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.declaration.CtAnnotation, spoon.reflect.reference.CtTypeReference<? extends java.lang.annotation.Annotation>> {
		private CtAnnotation_ANNOTATION_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ANNOTATION_TYPE, spoon.reflect.declaration.CtAnnotation.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAnnotationType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnnotationType(castValue(value));
		}
	}

	static class CtElement_ANNOTATION_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtElement, spoon.reflect.declaration.CtAnnotation<? extends java.lang.annotation.Annotation>> {
		private CtElement_ANNOTATION_RoleHandler() {
			super(spoon.reflect.path.CtRole.ANNOTATION, spoon.reflect.declaration.CtElement.class, spoon.reflect.declaration.CtAnnotation.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAnnotations())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnnotations(castValue(value));
		}
	}

	static class CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler extends spoon.reflect.meta.impl.ListHandler<spoon.reflect.declaration.CtClass, spoon.reflect.declaration.CtAnonymousExecutable> {
		private CtClass_ANNONYMOUS_EXECUTABLE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ANNONYMOUS_EXECUTABLE, spoon.reflect.declaration.CtClass.class, spoon.reflect.declaration.CtAnonymousExecutable.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAnonymousExecutables())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAnonymousExecutables(castValue(value));
		}
	}

	static class CtTypeAccess_ACCESSED_TYPE_RoleHandler extends spoon.reflect.meta.impl.SingleHandler<spoon.reflect.code.CtTypeAccess, spoon.reflect.reference.CtTypeReference<?>> {
		private CtTypeAccess_ACCESSED_TYPE_RoleHandler() {
			super(spoon.reflect.path.CtRole.ACCESSED_TYPE, spoon.reflect.code.CtTypeAccess.class, spoon.reflect.reference.CtTypeReference.class);
		}

		@java.lang.SuppressWarnings("unchecked")
		@java.lang.Override
		public <T, U> U getValue(T element) {
			return ((U) ((java.lang.Object) (castTarget(element).getAccessedType())));
		}

		@java.lang.Override
		public <T, U> void setValue(T element, U value) {
			castTarget(element).setAccessedType(castValue(value));
		}
	}
}