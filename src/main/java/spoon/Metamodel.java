/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon;

import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * This class enables to reason on the Spoon metamodel directly
 */
public class Metamodel {
	private Metamodel() { }

	/**
	 * Returns all interfaces of the Spoon metamodel.
	 * This method is stateless for sake of maintenance.
	 * If you need to call it several times, you should store the result.
	 */
	public static Set<CtType<?>> getAllMetamodelInterfaces() {
		Set<CtType<?>> result = new HashSet<>();
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		result.add(factory.Type().get(spoon.reflect.code.BinaryOperatorKind.class));
		result.add(factory.Type().get(spoon.reflect.code.CtAbstractInvocation.class));
		result.add(factory.Type().get(spoon.reflect.code.CtAnnotationFieldAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtArrayAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtArrayRead.class));
		result.add(factory.Type().get(spoon.reflect.code.CtArrayWrite.class));
		result.add(factory.Type().get(spoon.reflect.code.CtAssert.class));
		result.add(factory.Type().get(spoon.reflect.code.CtAssignment.class));
		result.add(factory.Type().get(spoon.reflect.code.CtBinaryOperator.class));
		result.add(factory.Type().get(spoon.reflect.code.CtBlock.class));
		result.add(factory.Type().get(spoon.reflect.code.CtBodyHolder.class));
		result.add(factory.Type().get(spoon.reflect.code.CtBreak.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCFlowBreak.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCase.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCatch.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCatchVariable.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCodeElement.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCodeSnippetExpression.class));
		result.add(factory.Type().get(spoon.reflect.code.CtCodeSnippetStatement.class));
		result.add(factory.Type().get(spoon.reflect.code.CtComment.class));
		result.add(factory.Type().get(spoon.reflect.code.CtConditional.class));
		result.add(factory.Type().get(spoon.reflect.code.CtConstructorCall.class));
		result.add(factory.Type().get(spoon.reflect.code.CtContinue.class));
		result.add(factory.Type().get(spoon.reflect.code.CtDo.class));
		result.add(factory.Type().get(spoon.reflect.code.CtExecutableReferenceExpression.class));
		result.add(factory.Type().get(spoon.reflect.code.CtExpression.class));
		result.add(factory.Type().get(spoon.reflect.code.CtFieldAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtFieldRead.class));
		result.add(factory.Type().get(spoon.reflect.code.CtFieldWrite.class));
		result.add(factory.Type().get(spoon.reflect.code.CtFor.class));
		result.add(factory.Type().get(spoon.reflect.code.CtForEach.class));
		result.add(factory.Type().get(spoon.reflect.code.CtIf.class));
		result.add(factory.Type().get(spoon.reflect.code.CtInvocation.class));
		result.add(factory.Type().get(spoon.reflect.code.CtJavaDoc.class));
		result.add(factory.Type().get(spoon.reflect.code.CtJavaDocTag.class));
		result.add(factory.Type().get(spoon.reflect.code.CtLabelledFlowBreak.class));
		result.add(factory.Type().get(spoon.reflect.code.CtLambda.class));
		result.add(factory.Type().get(spoon.reflect.code.CtLiteral.class));
		result.add(factory.Type().get(spoon.reflect.code.CtLocalVariable.class));
		result.add(factory.Type().get(spoon.reflect.code.CtLoop.class));
		result.add(factory.Type().get(spoon.reflect.code.CtNewArray.class));
		result.add(factory.Type().get(spoon.reflect.code.CtNewClass.class));
		result.add(factory.Type().get(spoon.reflect.code.CtOperatorAssignment.class));
		result.add(factory.Type().get(spoon.reflect.code.CtRHSReceiver.class));
		result.add(factory.Type().get(spoon.reflect.code.CtReturn.class));
		result.add(factory.Type().get(spoon.reflect.code.CtStatement.class));
		result.add(factory.Type().get(spoon.reflect.code.CtStatementList.class));
		result.add(factory.Type().get(spoon.reflect.code.CtSuperAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtSwitch.class));
		result.add(factory.Type().get(spoon.reflect.code.CtSynchronized.class));
		result.add(factory.Type().get(spoon.reflect.code.CtTargetedExpression.class));
		result.add(factory.Type().get(spoon.reflect.code.CtThisAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtThrow.class));
		result.add(factory.Type().get(spoon.reflect.code.CtTry.class));
		result.add(factory.Type().get(spoon.reflect.code.CtTryWithResource.class));
		result.add(factory.Type().get(spoon.reflect.code.CtTypeAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtUnaryOperator.class));
		result.add(factory.Type().get(spoon.reflect.code.CtVariableAccess.class));
		result.add(factory.Type().get(spoon.reflect.code.CtVariableRead.class));
		result.add(factory.Type().get(spoon.reflect.code.CtVariableWrite.class));
		result.add(factory.Type().get(spoon.reflect.code.CtWhile.class));
		result.add(factory.Type().get(spoon.reflect.code.UnaryOperatorKind.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtAnnotatedElementType.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtAnnotation.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtAnnotationMethod.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtAnnotationType.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtAnonymousExecutable.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtClass.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtCodeSnippet.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtConstructor.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtElement.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtEnum.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtEnumValue.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtExecutable.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtField.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtFormalTypeDeclarer.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtInterface.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtMethod.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtModifiable.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtMultiTypedElement.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtNamedElement.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtPackage.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtParameter.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtShadowable.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtType.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtTypeInformation.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtTypeMember.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtTypeParameter.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtTypedElement.class));
		result.add(factory.Type().get(spoon.reflect.declaration.CtVariable.class));
		result.add(factory.Type().get(spoon.reflect.declaration.ModifierKind.class));
		result.add(factory.Type().get(spoon.reflect.declaration.ParentNotInitializedException.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtActualTypeContainer.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtArrayTypeReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtCatchVariableReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtExecutableReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtFieldReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtIntersectionTypeReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtLocalVariableReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtPackageReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtParameterReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtTypeParameterReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtTypeReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtUnboundVariableReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtVariableReference.class));
		result.add(factory.Type().get(spoon.reflect.reference.CtWildcardReference.class));
		return result;
	}

}
