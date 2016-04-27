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


public class ReplacementVisitor extends spoon.reflect.visitor.CtScanner {
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

	private <K, V extends spoon.reflect.declaration.CtElement> void replaceInMapIfExist(java.util.Map<K, V> map) {
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
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInSetIfExist(java.util.Set<T> set) {
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
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInListIfExist(java.util.List<T> list) {
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
		replaceInListIfExist(annotation.getAnnotations());
		replaceInMapIfExist(annotation.getValues());
	}

	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		replaceInListIfExist(annotationType.getAnnotations());
		replaceInSetIfExist(annotationType.getNestedTypes());
		replaceInListIfExist(annotationType.getFields());
		replaceInListIfExist(annotationType.getComments());
	}

	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		replaceInListIfExist(anonymousExec.getAnnotations());
		replaceElementIfExist(anonymousExec.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(anonymousExec));
		replaceInListIfExist(anonymousExec.getComments());
	}

	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtArrayAccess(final spoon.reflect.code.CtArrayAccess<T, E> arrayAccess) {
		replaceInListIfExist(arrayAccess.getAnnotations());
		replaceElementIfExist(arrayAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayAccess));
		replaceInListIfExist(arrayAccess.getTypeCasts());
		replaceElementIfExist(arrayAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayAccess));
		replaceElementIfExist(arrayAccess.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayAccess));
		replaceInListIfExist(arrayAccess.getComments());
	}

	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		replaceInListIfExist(arrayRead.getAnnotations());
		replaceElementIfExist(arrayRead.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getTypeCasts());
		replaceElementIfExist(arrayRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getComments());
	}

	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		replaceInListIfExist(arrayWrite.getAnnotations());
		replaceElementIfExist(arrayWrite.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getTypeCasts());
		replaceElementIfExist(arrayWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getComments());
	}

	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getComponentType(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayTypeReferenceComponentTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments());
		replaceInListIfExist(reference.getAnnotations());
	}

	@java.lang.Override
	public <T> void visitCtImplicitArrayTypeReference(final spoon.reflect.internal.CtImplicitArrayTypeReference<T> reference) {
	}

	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		replaceInListIfExist(asserted.getAnnotations());
		replaceElementIfExist(asserted.getAssertExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertAssertExpressionReplaceListener(asserted));
		replaceElementIfExist(asserted.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertExpressionReplaceListener(asserted));
		replaceInListIfExist(asserted.getComments());
	}

	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		replaceInListIfExist(assignement.getAnnotations());
		replaceElementIfExist(assignement.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignement));
		replaceInListIfExist(assignement.getTypeCasts());
		replaceElementIfExist(assignement.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignement));
		replaceInListIfExist(assignement.getComments());
	}

	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations());
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts());
		replaceElementIfExist(operator.getLeftHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorLeftHandOperandReplaceListener(operator));
		replaceElementIfExist(operator.getRightHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorRightHandOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments());
	}

	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		replaceInListIfExist(block.getAnnotations());
		replaceInListIfExist(block.getStatements());
		replaceInListIfExist(block.getComments());
	}

	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		replaceInListIfExist(breakStatement.getAnnotations());
		replaceInListIfExist(breakStatement.getComments());
	}

	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		replaceInListIfExist(caseStatement.getAnnotations());
		replaceElementIfExist(caseStatement.getCaseExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtCaseCaseExpressionReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getStatements());
		replaceInListIfExist(caseStatement.getComments());
	}

	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		replaceInListIfExist(catchBlock.getAnnotations());
		replaceElementIfExist(catchBlock.getParameter(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchParameterReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchBodyReplaceListener(catchBlock));
		replaceInListIfExist(catchBlock.getComments());
	}

	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		replaceInListIfExist(ctClass.getAnnotations());
		replaceElementIfExist(ctClass.getSuperclass(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getSuperInterfaces());
		replaceInListIfExist(ctClass.getFormalTypeParameters());
		replaceInListIfExist(ctClass.getAnonymousExecutables());
		replaceInSetIfExist(ctClass.getNestedTypes());
		replaceInListIfExist(ctClass.getFields());
		replaceInSetIfExist(ctClass.getConstructors());
		replaceInSetIfExist(ctClass.getMethods());
		replaceInListIfExist(ctClass.getComments());
	}

	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		replaceInListIfExist(conditional.getAnnotations());
		replaceElementIfExist(conditional.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalConditionReplaceListener(conditional));
		replaceElementIfExist(conditional.getThenExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalThenExpressionReplaceListener(conditional));
		replaceElementIfExist(conditional.getElseExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalElseExpressionReplaceListener(conditional));
		replaceInListIfExist(conditional.getComments());
	}

	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		replaceInListIfExist(c.getAnnotations());
		replaceInListIfExist(c.getParameters());
		replaceInSetIfExist(c.getThrownTypes());
		replaceInListIfExist(c.getFormalTypeParameters());
		replaceElementIfExist(c.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(c));
		replaceInListIfExist(c.getComments());
	}

	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		replaceInListIfExist(continueStatement.getAnnotations());
		replaceElementIfExist(continueStatement.getLabelledStatement(), new spoon.support.visitor.replace.ReplacementVisitor.CtContinueLabelledStatementReplaceListener(continueStatement));
		replaceInListIfExist(continueStatement.getComments());
	}

	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		replaceInListIfExist(doLoop.getAnnotations());
		replaceElementIfExist(doLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtDoLoopingExpressionReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(doLoop));
		replaceInListIfExist(doLoop.getComments());
	}

	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		replaceInListIfExist(ctEnum.getAnnotations());
		replaceInSetIfExist(ctEnum.getSuperInterfaces());
		replaceInListIfExist(ctEnum.getFields());
		replaceInSetIfExist(ctEnum.getConstructors());
		replaceInSetIfExist(ctEnum.getMethods());
		replaceInSetIfExist(ctEnum.getNestedTypes());
		replaceInListIfExist(ctEnum.getComments());
	}

	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getParameters());
		replaceInListIfExist(reference.getActualTypeArguments());
		replaceInListIfExist(reference.getAnnotations());
		replaceInListIfExist(reference.getComments());
	}

	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		replaceInListIfExist(f.getAnnotations());
		replaceElementIfExist(f.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceElementIfExist(f.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(f));
		replaceInListIfExist(f.getComments());
	}

	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		replaceInListIfExist(enumValue.getAnnotations());
		replaceElementIfExist(enumValue.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(enumValue));
		replaceInListIfExist(enumValue.getComments());
	}

	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		replaceElementIfExist(thisAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getTypeCasts());
		replaceElementIfExist(thisAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getComments());
	}

	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		replaceInListIfExist(annotationFieldAccess.getAnnotations());
		replaceElementIfExist(annotationFieldAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getTypeCasts());
		replaceElementIfExist(annotationFieldAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationFieldAccessVariableReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getComments());
	}

	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations());
	}

	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		replaceInListIfExist(forLoop.getAnnotations());
		replaceInListIfExist(forLoop.getForInit());
		replaceElementIfExist(forLoop.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForExpressionReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForUpdate());
		replaceElementIfExist(forLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getComments());
	}

	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		replaceInListIfExist(foreach.getAnnotations());
		replaceElementIfExist(foreach.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachVariableReplaceListener(foreach));
		replaceElementIfExist(foreach.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachExpressionReplaceListener(foreach));
		replaceElementIfExist(foreach.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(foreach));
		replaceInListIfExist(foreach.getComments());
	}

	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		replaceInListIfExist(ifElement.getAnnotations());
		replaceElementIfExist(ifElement.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtIfConditionReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfThenStatementReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfElseStatementReplaceListener(ifElement));
		replaceInListIfExist(ifElement.getComments());
	}

	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		replaceInListIfExist(intrface.getAnnotations());
		replaceInSetIfExist(intrface.getSuperInterfaces());
		replaceInListIfExist(intrface.getFormalTypeParameters());
		replaceInSetIfExist(intrface.getNestedTypes());
		replaceInListIfExist(intrface.getFields());
		replaceInSetIfExist(intrface.getMethods());
		replaceInListIfExist(intrface.getComments());
	}

	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		replaceInListIfExist(invocation.getAnnotations());
		replaceInListIfExist(invocation.getTypeCasts());
		replaceElementIfExist(invocation.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(invocation));
		replaceElementIfExist(invocation.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(invocation));
		replaceInListIfExist(invocation.getArguments());
		replaceInListIfExist(invocation.getComments());
	}

	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		replaceInListIfExist(literal.getAnnotations());
		replaceElementIfExist(literal.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(literal));
		replaceInListIfExist(literal.getTypeCasts());
		replaceInListIfExist(literal.getComments());
	}

	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		replaceInListIfExist(localVariable.getAnnotations());
		replaceElementIfExist(localVariable.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(localVariable));
		replaceInListIfExist(localVariable.getComments());
	}

	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations());
	}

	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		replaceInListIfExist(catchVariable.getAnnotations());
		replaceElementIfExist(catchVariable.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getComments());
	}

	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations());
	}

	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		replaceInListIfExist(m.getAnnotations());
		replaceInListIfExist(m.getFormalTypeParameters());
		replaceElementIfExist(m.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(m));
		replaceInListIfExist(m.getParameters());
		replaceInSetIfExist(m.getThrownTypes());
		replaceElementIfExist(m.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(m));
		replaceInListIfExist(m.getComments());
	}

	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		replaceInListIfExist(newArray.getAnnotations());
		replaceElementIfExist(newArray.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(newArray));
		replaceInListIfExist(newArray.getTypeCasts());
		replaceInListIfExist(newArray.getElements());
		replaceInListIfExist(newArray.getDimensionExpressions());
		replaceInListIfExist(newArray.getComments());
	}

	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		replaceInListIfExist(ctConstructorCall.getAnnotations());
		replaceInListIfExist(ctConstructorCall.getTypeCasts());
		replaceElementIfExist(ctConstructorCall.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getArguments());
		replaceInListIfExist(ctConstructorCall.getComments());
	}

	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		replaceInListIfExist(newClass.getAnnotations());
		replaceElementIfExist(newClass.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(newClass));
		replaceInListIfExist(newClass.getTypeCasts());
		replaceElementIfExist(newClass.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(newClass));
		replaceElementIfExist(newClass.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(newClass));
		replaceInListIfExist(newClass.getArguments());
		replaceElementIfExist(newClass.getAnonymousClass(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewClassAnonymousClassReplaceListener(newClass));
		replaceInListIfExist(newClass.getComments());
	}

	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		replaceInListIfExist(lambda.getAnnotations());
		replaceElementIfExist(lambda.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(lambda));
		replaceInListIfExist(lambda.getTypeCasts());
		replaceInListIfExist(lambda.getParameters());
		replaceElementIfExist(lambda.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(lambda));
		replaceElementIfExist(lambda.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtLambdaExpressionReplaceListener(lambda));
		replaceInListIfExist(lambda.getComments());
	}

	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		replaceElementIfExist(expression.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts());
		replaceElementIfExist(expression.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceExpressionExecutableReplaceListener(expression));
		replaceElementIfExist(expression.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(expression));
	}

	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		replaceInListIfExist(assignment.getAnnotations());
		replaceElementIfExist(assignment.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignment));
		replaceInListIfExist(assignment.getTypeCasts());
		replaceElementIfExist(assignment.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignment));
		replaceInListIfExist(assignment.getComments());
	}

	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		replaceInListIfExist(ctPackage.getAnnotations());
		replaceInSetIfExist(ctPackage.getPackages());
		replaceInSetIfExist(ctPackage.getTypes());
		replaceInListIfExist(ctPackage.getComments());
	}

	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
	}

	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		replaceInListIfExist(parameter.getAnnotations());
		replaceElementIfExist(parameter.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(parameter));
		replaceInListIfExist(parameter.getComments());
	}

	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations());
	}

	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		replaceInListIfExist(returnStatement.getAnnotations());
		replaceElementIfExist(returnStatement.getReturnedExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtReturnReturnedExpressionReplaceListener(returnStatement));
		replaceInListIfExist(returnStatement.getComments());
	}

	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		replaceInListIfExist(statements.getAnnotations());
		replaceInListIfExist(statements.getStatements());
		replaceInListIfExist(statements.getComments());
	}

	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		replaceInListIfExist(switchStatement.getAnnotations());
		replaceElementIfExist(switchStatement.getSelector(), new spoon.support.visitor.replace.ReplacementVisitor.CtSwitchSelectorReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getCases());
		replaceInListIfExist(switchStatement.getComments());
	}

	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		replaceInListIfExist(synchro.getAnnotations());
		replaceElementIfExist(synchro.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedExpressionReplaceListener(synchro));
		replaceElementIfExist(synchro.getBlock(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedBlockReplaceListener(synchro));
		replaceInListIfExist(synchro.getComments());
	}

	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		replaceInListIfExist(throwStatement.getAnnotations());
		replaceElementIfExist(throwStatement.getThrownExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtThrowThrownExpressionReplaceListener(throwStatement));
		replaceInListIfExist(throwStatement.getComments());
	}

	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		replaceInListIfExist(tryBlock.getAnnotations());
		replaceElementIfExist(tryBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getCatchers());
		replaceElementIfExist(tryBlock.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getComments());
	}

	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		replaceInListIfExist(tryWithResource.getAnnotations());
		replaceInListIfExist(tryWithResource.getResources());
		replaceElementIfExist(tryWithResource.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getCatchers());
		replaceElementIfExist(tryWithResource.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getComments());
	}

	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		replaceElementIfExist(ref.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(ref));
		replaceElementIfExist(ref.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(ref));
		replaceInListIfExist(ref.getActualTypeArguments());
		replaceInListIfExist(ref.getAnnotations());
		replaceElementIfExist(ref.getBoundingType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeParameterReferenceBoundingTypeReplaceListener(ref));
	}

	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		replaceInListIfExist(reference.getBounds());
	}

	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments());
		replaceInListIfExist(reference.getAnnotations());
		replaceInListIfExist(reference.getComments());
	}

	@java.lang.Override
	public void visitCtCircularTypeReference(final spoon.reflect.internal.CtCircularTypeReference reference) {
	}

	@java.lang.Override
	public <T> void visitCtImplicitTypeReference(final spoon.reflect.internal.CtImplicitTypeReference<T> reference) {
	}

	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		replaceInListIfExist(typeAccess.getAnnotations());
		replaceElementIfExist(typeAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeAccessTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getTypeCasts());
		replaceElementIfExist(typeAccess.getAccessedType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeAccessAccessedTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getComments());
	}

	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations());
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts());
		replaceElementIfExist(operator.getOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtUnaryOperatorOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments());
	}

	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		replaceInListIfExist(variableRead.getAnnotations());
		replaceElementIfExist(variableRead.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getTypeCasts());
		replaceElementIfExist(variableRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getComments());
	}

	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		replaceInListIfExist(variableWrite.getAnnotations());
		replaceElementIfExist(variableWrite.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getTypeCasts());
		replaceElementIfExist(variableWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getComments());
	}

	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		replaceInListIfExist(whileLoop.getAnnotations());
		replaceElementIfExist(whileLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtWhileLoopingExpressionReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(whileLoop));
		replaceInListIfExist(whileLoop.getComments());
	}

	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
	}

	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
	}

	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
	}

	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		replaceInListIfExist(fieldRead.getAnnotations());
		replaceInListIfExist(fieldRead.getTypeCasts());
		replaceElementIfExist(fieldRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getComments());
	}

	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		replaceInListIfExist(fieldWrite.getAnnotations());
		replaceInListIfExist(fieldWrite.getTypeCasts());
		replaceElementIfExist(fieldWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getComments());
	}

	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		replaceInListIfExist(f.getAnnotations());
		replaceElementIfExist(f.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceInListIfExist(f.getTypeCasts());
		replaceElementIfExist(f.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(f));
		replaceInListIfExist(f.getComments());
	}

	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
	}
}
