/**
 * Copyright (C) 2006-2015 INRIA and contributors
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


package spoon.support.visitor.replace;


/**
 * Used to replace an element by another one.
 *
 * This class is generated automatically by the processor {@link spoon.generating.ReplacementVisitorGenerator}.
 */
public class ReplacementVisitor extends spoon.reflect.visitor.CtScanner {
	class CtAbstractInvocationArgumentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationArgumentsReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setArguments(replace);
		}
	}

	class CtAbstractInvocationExecutableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationExecutableReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	class CtAnnotationAnnotationTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationAnnotationTypeReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAnnotationType(replace);
		}
	}

	class CtAnnotationFieldAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private spoon.reflect.code.CtVariableAccess element;

		CtAnnotationFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	class CtAnnotationValuesReplaceListener implements spoon.generating.replace.ReplaceMapListener<java.util.Map> {
		private spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationValuesReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Map replace) {
			this.element.setValues(replace);
		}
	}

	class CtArrayAccessIndexExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtArrayAccess element;

		CtArrayAccessIndexExpressionReplaceListener(spoon.reflect.code.CtArrayAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setIndexExpression(replace);
		}
	}

	class CtArrayTypeReferenceComponentTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtArrayTypeReference element;

		CtArrayTypeReferenceComponentTypeReplaceListener(spoon.reflect.reference.CtArrayTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setComponentType(replace);
		}
	}

	class CtAssertAssertExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtAssert element;

		CtAssertAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssertExpression(replace);
		}
	}

	class CtAssertExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtAssert element;

		CtAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	class CtAssignmentAssignedReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtAssignment element;

		CtAssignmentAssignedReplaceListener(spoon.reflect.code.CtAssignment element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssigned(replace);
		}
	}

	class CtBinaryOperatorLeftHandOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorLeftHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLeftHandOperand(replace);
		}
	}

	class CtBinaryOperatorRightHandOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorRightHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setRightHandOperand(replace);
		}
	}

	class CtCaseCaseExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtCase element;

		CtCaseCaseExpressionReplaceListener(spoon.reflect.code.CtCase element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCaseExpression(replace);
		}
	}

	class CtCatchBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private spoon.reflect.code.CtCatch element;

		CtCatchBodyReplaceListener(spoon.reflect.code.CtCatch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	class CtCatchParameterReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtCatchVariable> {
		private spoon.reflect.code.CtCatch element;

		CtCatchParameterReplaceListener(spoon.reflect.code.CtCatch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtCatchVariable replace) {
			this.element.setParameter(replace);
		}
	}

	class CtClassAnonymousExecutablesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtClass element;

		CtClassAnonymousExecutablesReplaceListener(spoon.reflect.declaration.CtClass element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setAnonymousExecutables(replace);
		}
	}

	class CtClassConstructorsReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtClass element;

		CtClassConstructorsReplaceListener(spoon.reflect.declaration.CtClass element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setConstructors(replace);
		}
	}

	class CtClassFieldsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtType element;

		CtClassFieldsReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setFields(replace);
		}
	}

	class CtConditionalConditionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtConditional element;

		CtConditionalConditionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	class CtConditionalElseExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtConditional element;

		CtConditionalElseExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setElseExpression(replace);
		}
	}

	class CtConditionalThenExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtConditional element;

		CtConditionalThenExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThenExpression(replace);
		}
	}

	class CtContinueLabelledStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private spoon.reflect.code.CtContinue element;

		CtContinueLabelledStatementReplaceListener(spoon.reflect.code.CtContinue element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setLabelledStatement(replace);
		}
	}

	class CtDoLoopingExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtDo element;

		CtDoLoopingExpressionReplaceListener(spoon.reflect.code.CtDo element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	class CtElementAnnotationsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtElement element;

		CtElementAnnotationsReplaceListener(spoon.reflect.declaration.CtElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setAnnotations(replace);
		}
	}

	class CtElementCommentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtElement element;

		CtElementCommentsReplaceListener(spoon.reflect.declaration.CtElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setComments(replace);
		}
	}

	class CtExecutableBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private spoon.reflect.declaration.CtExecutable element;

		CtExecutableBodyReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	class CtExecutableParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtExecutable element;

		CtExecutableParametersReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	class CtExecutableReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	class CtExecutableReferenceExpressionExecutableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private spoon.reflect.code.CtExecutableReferenceExpression element;

		CtExecutableReferenceExpressionExecutableReplaceListener(spoon.reflect.code.CtExecutableReferenceExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	class CtExecutableReferenceParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceParametersReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	class CtExecutableReferenceTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	class CtExecutableThrownTypesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtExecutable element;

		CtExecutableThrownTypesReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setThrownTypes(replace);
		}
	}

	class CtExpressionTypeCastsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtExpression element;

		CtExpressionTypeCastsReplaceListener(spoon.reflect.code.CtExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTypeCasts(replace);
		}
	}

	class CtFieldAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private spoon.reflect.code.CtVariableAccess element;

		CtFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	class CtFieldReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtFieldReference element;

		CtFieldReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtFieldReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	class CtForEachExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtForEach element;

		CtForEachExpressionReplaceListener(spoon.reflect.code.CtForEach element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	class CtForEachVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtLocalVariable> {
		private spoon.reflect.code.CtForEach element;

		CtForEachVariableReplaceListener(spoon.reflect.code.CtForEach element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtLocalVariable replace) {
			this.element.setVariable(replace);
		}
	}

	class CtForExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtFor element;

		CtForExpressionReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	class CtForForInitReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtFor element;

		CtForForInitReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForInit(replace);
		}
	}

	class CtForForUpdateReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtFor element;

		CtForForUpdateReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForUpdate(replace);
		}
	}

	class CtGenericElementFormalTypeParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtGenericElement element;

		CtGenericElementFormalTypeParametersReplaceListener(spoon.reflect.declaration.CtGenericElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setFormalTypeParameters(replace);
		}
	}

	class CtGenericElementReferenceActualTypeArgumentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.reference.CtGenericElementReference element;

		CtGenericElementReferenceActualTypeArgumentsReplaceListener(spoon.reflect.reference.CtGenericElementReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setActualTypeArguments(replace);
		}
	}

	class CtIfConditionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtIf element;

		CtIfConditionReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	class CtIfElseStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private spoon.reflect.code.CtIf element;

		CtIfElseStatementReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setElseStatement(replace);
		}
	}

	class CtIfThenStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private spoon.reflect.code.CtIf element;

		CtIfThenStatementReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setThenStatement(replace);
		}
	}

	class CtIntersectionTypeReferenceBoundsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.reference.CtIntersectionTypeReference element;

		CtIntersectionTypeReferenceBoundsReplaceListener(spoon.reflect.reference.CtIntersectionTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setBounds(replace);
		}
	}

	class CtLambdaExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtLambda element;

		CtLambdaExpressionReplaceListener(spoon.reflect.code.CtLambda element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	class CtLoopBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private spoon.reflect.code.CtLoop element;

		CtLoopBodyReplaceListener(spoon.reflect.code.CtLoop element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setBody(replace);
		}
	}

	class CtNewArrayDimensionExpressionsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtNewArray element;

		CtNewArrayDimensionExpressionsReplaceListener(spoon.reflect.code.CtNewArray element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setDimensionExpressions(replace);
		}
	}

	class CtNewArrayElementsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtNewArray element;

		CtNewArrayElementsReplaceListener(spoon.reflect.code.CtNewArray element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setElements(replace);
		}
	}

	class CtNewClassAnonymousClassReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.declaration.CtClass> {
		private spoon.reflect.code.CtNewClass element;

		CtNewClassAnonymousClassReplaceListener(spoon.reflect.code.CtNewClass element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtClass replace) {
			this.element.setAnonymousClass(replace);
		}
	}

	class CtPackagePackagesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtPackage element;

		CtPackagePackagesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setPackages(replace);
		}
	}

	class CtPackageTypesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtPackage element;

		CtPackageTypesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setTypes(replace);
		}
	}

	class CtRHSReceiverAssignmentReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtRHSReceiver element;

		CtRHSReceiverAssignmentReplaceListener(spoon.reflect.code.CtRHSReceiver element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssignment(replace);
		}
	}

	class CtReturnReturnedExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtReturn element;

		CtReturnReturnedExpressionReplaceListener(spoon.reflect.code.CtReturn element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setReturnedExpression(replace);
		}
	}

	class CtStatementListStatementsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtStatementList element;

		CtStatementListStatementsReplaceListener(spoon.reflect.code.CtStatementList element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setStatements(replace);
		}
	}

	class CtSwitchCasesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtSwitch element;

		CtSwitchCasesReplaceListener(spoon.reflect.code.CtSwitch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCases(replace);
		}
	}

	class CtSwitchSelectorReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtSwitch element;

		CtSwitchSelectorReplaceListener(spoon.reflect.code.CtSwitch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setSelector(replace);
		}
	}

	class CtSynchronizedBlockReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private spoon.reflect.code.CtSynchronized element;

		CtSynchronizedBlockReplaceListener(spoon.reflect.code.CtSynchronized element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBlock(replace);
		}
	}

	class CtSynchronizedExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtSynchronized element;

		CtSynchronizedExpressionReplaceListener(spoon.reflect.code.CtSynchronized element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	class CtTargetedExpressionTargetReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtTargetedExpression element;

		CtTargetedExpressionTargetReplaceListener(spoon.reflect.code.CtTargetedExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setTarget(replace);
		}
	}

	class CtThrowThrownExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtThrow element;

		CtThrowThrownExpressionReplaceListener(spoon.reflect.code.CtThrow element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThrownExpression(replace);
		}
	}

	class CtTryBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private spoon.reflect.code.CtTry element;

		CtTryBodyReplaceListener(spoon.reflect.code.CtTry element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	class CtTryCatchersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtTry element;

		CtTryCatchersReplaceListener(spoon.reflect.code.CtTry element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCatchers(replace);
		}
	}

	class CtTryFinalizerReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private spoon.reflect.code.CtTry element;

		CtTryFinalizerReplaceListener(spoon.reflect.code.CtTry element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setFinalizer(replace);
		}
	}

	class CtTryWithResourceResourcesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.code.CtTryWithResource element;

		CtTryWithResourceResourcesReplaceListener(spoon.reflect.code.CtTryWithResource element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setResources(replace);
		}
	}

	class CtTypeAccessAccessedTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.code.CtTypeAccess element;

		CtTypeAccessAccessedTypeReplaceListener(spoon.reflect.code.CtTypeAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAccessedType(replace);
		}
	}

	class CtTypeAccessTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.declaration.CtTypedElement element;

		CtTypeAccessTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	class CtTypeFieldsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private spoon.reflect.declaration.CtType element;

		CtTypeFieldsReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setFields(replace);
		}
	}

	class CtTypeInformationSuperInterfacesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtType element;

		CtTypeInformationSuperInterfacesReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setSuperInterfaces(replace);
		}
	}

	class CtTypeInformationSuperclassReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.declaration.CtClass element;

		CtTypeInformationSuperclassReplaceListener(spoon.reflect.declaration.CtClass element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setSuperclass(replace);
		}
	}

	class CtTypeMethodsReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtType element;

		CtTypeMethodsReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setMethods(replace);
		}
	}

	class CtTypeNestedTypesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private spoon.reflect.declaration.CtType element;

		CtTypeNestedTypesReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setNestedTypes(replace);
		}
	}

	class CtTypeParameterReferenceBoundingTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtTypeParameterReference element;

		CtTypeParameterReferenceBoundingTypeReplaceListener(spoon.reflect.reference.CtTypeParameterReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setBoundingType(replace);
		}
	}

	class CtTypeReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtTypeReference element;

		CtTypeReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	class CtTypeReferencePackageReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtPackageReference> {
		private spoon.reflect.reference.CtTypeReference element;

		CtTypeReferencePackageReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtPackageReference replace) {
			this.element.setPackage(replace);
		}
	}

	class CtTypedElementTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.declaration.CtTypedElement element;

		CtTypedElementTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	class CtUnaryOperatorOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtUnaryOperator element;

		CtUnaryOperatorOperandReplaceListener(spoon.reflect.code.CtUnaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setOperand(replace);
		}
	}

	class CtVariableAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtVariableReference> {
		private spoon.reflect.code.CtVariableAccess element;

		CtVariableAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtVariableReference replace) {
			this.element.setVariable(replace);
		}
	}

	class CtVariableDefaultExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.declaration.CtVariable element;

		CtVariableDefaultExpressionReplaceListener(spoon.reflect.declaration.CtVariable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setDefaultExpression(replace);
		}
	}

	class CtVariableReferenceTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private spoon.reflect.reference.CtVariableReference element;

		CtVariableReferenceTypeReplaceListener(spoon.reflect.reference.CtVariableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	class CtWhileLoopingExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private spoon.reflect.code.CtWhile element;

		CtWhileLoopingExpressionReplaceListener(spoon.reflect.code.CtWhile element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	public static void replace(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement replace) {
		try {
			new spoon.support.visitor.replace.ReplacementVisitor(original, replace).scan(original.getParent());
		} catch (spoon.SpoonException ignore) {
		}
	}

	private spoon.reflect.declaration.CtElement original;

	private spoon.reflect.declaration.CtElement replace;

	private ReplacementVisitor(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement replace) {
		spoon.support.visitor.replace.ReplacementVisitor.this.original = original;
		spoon.support.visitor.replace.ReplacementVisitor.this.replace = replace;
	}

	private <K, V extends spoon.reflect.declaration.CtElement> void replaceInMapIfExist(java.util.Map<K, V> mapProtected, spoon.generating.replace.ReplaceMapListener listener) {
		java.util.Map<K, V> map = new java.util.HashMap<K, V>(mapProtected);
		V shouldBeDeleted = null;
		K key = null;
		for (java.util.Map.Entry<K, V> entry : map.entrySet()) {
			if ((entry.getValue()) == (original)) {
				shouldBeDeleted = entry.getValue();
				key = entry.getKey();
				break;
			}
		}
		if (shouldBeDeleted != null) {
			map.remove(key);
			if ((replace) != null) {
				map.put(key, ((V) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}
			listener.set(map);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInSetIfExist(java.util.Set<T> setProtected, spoon.generating.replace.ReplaceSetListener listener) {
		java.util.Set<T> set = new java.util.HashSet<T>(setProtected);
		T shouldBeDeleted = null;
		for (T element : set) {
			if (element == (original)) {
				shouldBeDeleted = element;
				break;
			}
		}
		if (shouldBeDeleted != null) {
			set.remove(shouldBeDeleted);
			if ((replace) != null) {
				set.add(((T) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}
			listener.set(set);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInListIfExist(java.util.List<T> listProtected, spoon.generating.replace.ReplaceListListener listener) {
		java.util.List<T> list = new java.util.ArrayList<T>(listProtected);
		T shouldBeDeleted = null;
		int index = 0;
		for (int i = 0; i < (list.size()); i++) {
			if ((list.get(i)) == (original)) {
				index = i;
				shouldBeDeleted = list.get(i);
				break;
			}
		}
		if (shouldBeDeleted != null) {
			list.remove(index);
			if ((replace) != null) {
				list.add(index, ((T) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}
			listener.set(list);
		}
	}

	private void replaceElementIfExist(spoon.reflect.declaration.CtElement candidate, spoon.generating.replace.ReplaceListener listener) {
		if (candidate == (original)) {
			listener.set(replace);
			if ((replace) != null) {
				replace.setParent(candidate.getParent());
			}
		}
	}

	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		replaceElementIfExist(annotation.getAnnotationType(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationAnnotationTypeReplaceListener(annotation));
		replaceInListIfExist(annotation.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotation));
		replaceInMapIfExist(annotation.getValues(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationValuesReplaceListener(annotation));
	}

	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		replaceInListIfExist(annotationType.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationType));
		replaceInSetIfExist(annotationType.getNestedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeNestedTypesReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getFields(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeFieldsReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotationType));
	}

	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		replaceInListIfExist(anonymousExec.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(anonymousExec));
		replaceElementIfExist(anonymousExec.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(anonymousExec));
		replaceInListIfExist(anonymousExec.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(anonymousExec));
	}

	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtArrayAccess(final spoon.reflect.code.CtArrayAccess<T, E> arrayAccess) {
		replaceInListIfExist(arrayAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayAccess));
		replaceElementIfExist(arrayAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayAccess));
		replaceInListIfExist(arrayAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayAccess));
		replaceElementIfExist(arrayAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayAccess));
		replaceElementIfExist(arrayAccess.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayAccess));
		replaceInListIfExist(arrayAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(arrayAccess));
	}

	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		replaceInListIfExist(arrayRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(arrayRead));
	}

	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		replaceInListIfExist(arrayWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(arrayWrite));
	}

	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getComponentType(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayTypeReferenceComponentTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementReferenceActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	@java.lang.Override
	public <T> void visitCtImplicitArrayTypeReference(final spoon.reflect.internal.CtImplicitArrayTypeReference<T> reference) {
	}

	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		replaceInListIfExist(asserted.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(asserted));
		replaceElementIfExist(asserted.getAssertExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertAssertExpressionReplaceListener(asserted));
		replaceElementIfExist(asserted.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertExpressionReplaceListener(asserted));
		replaceInListIfExist(asserted.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(asserted));
	}

	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		replaceInListIfExist(assignement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(assignement));
		replaceElementIfExist(assignement.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignement));
		replaceInListIfExist(assignement.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignement));
		replaceInListIfExist(assignement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(assignement));
	}

	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getLeftHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorLeftHandOperandReplaceListener(operator));
		replaceElementIfExist(operator.getRightHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorRightHandOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		replaceInListIfExist(block.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(block));
		replaceInListIfExist(block.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(block));
		replaceInListIfExist(block.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(block));
	}

	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		replaceInListIfExist(breakStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(breakStatement));
		replaceInListIfExist(breakStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(breakStatement));
	}

	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		replaceInListIfExist(caseStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(caseStatement));
		replaceElementIfExist(caseStatement.getCaseExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtCaseCaseExpressionReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(caseStatement));
	}

	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		replaceInListIfExist(catchBlock.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getParameter(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchParameterReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchBodyReplaceListener(catchBlock));
		replaceInListIfExist(catchBlock.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(catchBlock));
	}

	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		replaceInListIfExist(ctClass.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctClass));
		replaceElementIfExist(ctClass.getSuperclass(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getFormalTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementFormalTypeParametersReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getAnonymousExecutables(), new spoon.support.visitor.replace.ReplacementVisitor.CtClassAnonymousExecutablesReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getNestedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeNestedTypesReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getFields(), new spoon.support.visitor.replace.ReplacementVisitor.CtClassFieldsReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getConstructors(), new spoon.support.visitor.replace.ReplacementVisitor.CtClassConstructorsReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getMethods(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeMethodsReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctClass));
	}

	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		replaceInListIfExist(conditional.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(conditional));
		replaceElementIfExist(conditional.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalConditionReplaceListener(conditional));
		replaceElementIfExist(conditional.getThenExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalThenExpressionReplaceListener(conditional));
		replaceElementIfExist(conditional.getElseExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalElseExpressionReplaceListener(conditional));
		replaceInListIfExist(conditional.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(conditional));
	}

	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		replaceInListIfExist(c.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(c));
		replaceInListIfExist(c.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(c));
		replaceInSetIfExist(c.getThrownTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableThrownTypesReplaceListener(c));
		replaceInListIfExist(c.getFormalTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementFormalTypeParametersReplaceListener(c));
		replaceElementIfExist(c.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(c));
		replaceInListIfExist(c.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(c));
	}

	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		replaceInListIfExist(continueStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(continueStatement));
		replaceElementIfExist(continueStatement.getLabelledStatement(), new spoon.support.visitor.replace.ReplacementVisitor.CtContinueLabelledStatementReplaceListener(continueStatement));
		replaceInListIfExist(continueStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(continueStatement));
	}

	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		replaceInListIfExist(doLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtDoLoopingExpressionReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(doLoop));
		replaceInListIfExist(doLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(doLoop));
	}

	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		replaceInListIfExist(ctEnum.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getFields(), new spoon.support.visitor.replace.ReplacementVisitor.CtClassFieldsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getConstructors(), new spoon.support.visitor.replace.ReplacementVisitor.CtClassConstructorsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getMethods(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeMethodsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getNestedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeNestedTypesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctEnum));
	}

	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceParametersReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementReferenceActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		replaceInListIfExist(f.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceElementIfExist(f.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceElementIfExist(f.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(f));
		replaceInListIfExist(f.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(f));
	}

	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		replaceInListIfExist(enumValue.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(enumValue));
		replaceInListIfExist(enumValue.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(enumValue));
	}

	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		replaceElementIfExist(thisAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(thisAccess));
		replaceElementIfExist(thisAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(thisAccess));
	}

	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		replaceInListIfExist(annotationFieldAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationFieldAccessVariableReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotationFieldAccess));
	}

	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		replaceInListIfExist(forLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForInit(), new spoon.support.visitor.replace.ReplacementVisitor.CtForForInitReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForExpressionReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForUpdate(), new spoon.support.visitor.replace.ReplacementVisitor.CtForForUpdateReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(forLoop));
	}

	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		replaceInListIfExist(foreach.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(foreach));
		replaceElementIfExist(foreach.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachVariableReplaceListener(foreach));
		replaceElementIfExist(foreach.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachExpressionReplaceListener(foreach));
		replaceElementIfExist(foreach.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(foreach));
		replaceInListIfExist(foreach.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(foreach));
	}

	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		replaceInListIfExist(ifElement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ifElement));
		replaceElementIfExist(ifElement.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtIfConditionReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfThenStatementReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfElseStatementReplaceListener(ifElement));
		replaceInListIfExist(ifElement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ifElement));
	}

	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		replaceInListIfExist(intrface.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(intrface));
		replaceInSetIfExist(intrface.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(intrface));
		replaceInListIfExist(intrface.getFormalTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementFormalTypeParametersReplaceListener(intrface));
		replaceInSetIfExist(intrface.getNestedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeNestedTypesReplaceListener(intrface));
		replaceInListIfExist(intrface.getFields(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeFieldsReplaceListener(intrface));
		replaceInSetIfExist(intrface.getMethods(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeMethodsReplaceListener(intrface));
		replaceInListIfExist(intrface.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(intrface));
	}

	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		replaceInListIfExist(invocation.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(invocation));
		replaceInListIfExist(invocation.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(invocation));
		replaceElementIfExist(invocation.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(invocation));
		replaceElementIfExist(invocation.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(invocation));
		replaceInListIfExist(invocation.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(invocation));
		replaceInListIfExist(invocation.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(invocation));
	}

	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		replaceInListIfExist(literal.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(literal));
		replaceElementIfExist(literal.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(literal));
		replaceInListIfExist(literal.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(literal));
		replaceInListIfExist(literal.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(literal));
	}

	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		replaceInListIfExist(localVariable.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(localVariable));
		replaceInListIfExist(localVariable.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(localVariable));
	}

	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		replaceInListIfExist(catchVariable.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(catchVariable));
		replaceElementIfExist(catchVariable.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(catchVariable));
	}

	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		replaceInListIfExist(m.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(m));
		replaceInListIfExist(m.getFormalTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementFormalTypeParametersReplaceListener(m));
		replaceElementIfExist(m.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(m));
		replaceInListIfExist(m.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(m));
		replaceInSetIfExist(m.getThrownTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableThrownTypesReplaceListener(m));
		replaceElementIfExist(m.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(m));
		replaceInListIfExist(m.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(m));
	}

	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		replaceInListIfExist(newArray.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(newArray));
		replaceElementIfExist(newArray.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(newArray));
		replaceInListIfExist(newArray.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newArray));
		replaceInListIfExist(newArray.getElements(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewArrayElementsReplaceListener(newArray));
		replaceInListIfExist(newArray.getDimensionExpressions(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewArrayDimensionExpressionsReplaceListener(newArray));
		replaceInListIfExist(newArray.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(newArray));
	}

	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		replaceInListIfExist(ctConstructorCall.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctConstructorCall));
	}

	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		replaceInListIfExist(newClass.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(newClass));
		replaceElementIfExist(newClass.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(newClass));
		replaceInListIfExist(newClass.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newClass));
		replaceElementIfExist(newClass.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(newClass));
		replaceElementIfExist(newClass.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(newClass));
		replaceInListIfExist(newClass.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(newClass));
		replaceElementIfExist(newClass.getAnonymousClass(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewClassAnonymousClassReplaceListener(newClass));
		replaceInListIfExist(newClass.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(newClass));
	}

	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		replaceInListIfExist(lambda.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(lambda));
		replaceElementIfExist(lambda.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(lambda));
		replaceInListIfExist(lambda.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(lambda));
		replaceInListIfExist(lambda.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(lambda));
		replaceElementIfExist(lambda.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(lambda));
		replaceElementIfExist(lambda.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtLambdaExpressionReplaceListener(lambda));
		replaceInListIfExist(lambda.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(lambda));
	}

	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		replaceElementIfExist(expression.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(expression));
		replaceElementIfExist(expression.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceExpressionExecutableReplaceListener(expression));
		replaceElementIfExist(expression.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(expression));
	}

	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		replaceInListIfExist(assignment.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(assignment));
		replaceElementIfExist(assignment.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignment));
		replaceInListIfExist(assignment.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignment));
		replaceInListIfExist(assignment.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(assignment));
	}

	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		replaceInListIfExist(ctPackage.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getPackages(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackagePackagesReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackageTypesReplaceListener(ctPackage));
		replaceInListIfExist(ctPackage.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctPackage));
	}

	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
	}

	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		replaceInListIfExist(parameter.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(parameter));
		replaceElementIfExist(parameter.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(parameter));
		replaceInListIfExist(parameter.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(parameter));
	}

	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		replaceInListIfExist(returnStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(returnStatement));
		replaceElementIfExist(returnStatement.getReturnedExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtReturnReturnedExpressionReplaceListener(returnStatement));
		replaceInListIfExist(returnStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(returnStatement));
	}

	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		replaceInListIfExist(statements.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(statements));
		replaceInListIfExist(statements.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(statements));
		replaceInListIfExist(statements.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(statements));
	}

	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		replaceInListIfExist(switchStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(switchStatement));
		replaceElementIfExist(switchStatement.getSelector(), new spoon.support.visitor.replace.ReplacementVisitor.CtSwitchSelectorReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getCases(), new spoon.support.visitor.replace.ReplacementVisitor.CtSwitchCasesReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(switchStatement));
	}

	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		replaceInListIfExist(synchro.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(synchro));
		replaceElementIfExist(synchro.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedExpressionReplaceListener(synchro));
		replaceElementIfExist(synchro.getBlock(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedBlockReplaceListener(synchro));
		replaceInListIfExist(synchro.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(synchro));
	}

	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		replaceInListIfExist(throwStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(throwStatement));
		replaceElementIfExist(throwStatement.getThrownExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtThrowThrownExpressionReplaceListener(throwStatement));
		replaceInListIfExist(throwStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(throwStatement));
	}

	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		replaceInListIfExist(tryBlock.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getCatchers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryCatchersReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(tryBlock));
	}

	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		replaceInListIfExist(tryWithResource.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getResources(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryWithResourceResourcesReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getCatchers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryCatchersReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(tryWithResource));
	}

	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		replaceElementIfExist(ref.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(ref));
		replaceElementIfExist(ref.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(ref));
		replaceInListIfExist(ref.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementReferenceActualTypeArgumentsReplaceListener(ref));
		replaceInListIfExist(ref.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ref));
		replaceElementIfExist(ref.getBoundingType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeParameterReferenceBoundingTypeReplaceListener(ref));
	}

	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		replaceInListIfExist(reference.getBounds(), new spoon.support.visitor.replace.ReplacementVisitor.CtIntersectionTypeReferenceBoundsReplaceListener(reference));
	}

	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtGenericElementReferenceActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	@java.lang.Override
	public void visitCtCircularTypeReference(final spoon.reflect.internal.CtCircularTypeReference reference) {
	}

	@java.lang.Override
	public <T> void visitCtImplicitTypeReference(final spoon.reflect.internal.CtImplicitTypeReference<T> reference) {
	}

	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		replaceInListIfExist(typeAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(typeAccess));
		replaceElementIfExist(typeAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeAccessTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(typeAccess));
		replaceElementIfExist(typeAccess.getAccessedType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeAccessAccessedTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(typeAccess));
	}

	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtUnaryOperatorOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		replaceInListIfExist(variableRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(variableRead));
		replaceElementIfExist(variableRead.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableRead));
		replaceElementIfExist(variableRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(variableRead));
	}

	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		replaceInListIfExist(variableWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(variableWrite));
		replaceElementIfExist(variableWrite.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableWrite));
		replaceElementIfExist(variableWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(variableWrite));
	}

	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		replaceInListIfExist(whileLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtWhileLoopingExpressionReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(whileLoop));
		replaceInListIfExist(whileLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(whileLoop));
	}

	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
	}

	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
	}

	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
	}

	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		replaceInListIfExist(fieldRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(fieldRead));
	}

	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		replaceInListIfExist(fieldWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(fieldWrite));
	}

	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		replaceInListIfExist(f.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceElementIfExist(f.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceInListIfExist(f.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(f));
		replaceElementIfExist(f.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(f));
		replaceInListIfExist(f.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(f));
	}

	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
	}
}

