package spoon.testing.assertions;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCasePattern;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtPattern;
import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.code.CtRecordPattern;
import spoon.reflect.code.CtResource;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTextBlock;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtTypePattern;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtUnnamedPattern;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
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
import spoon.reflect.declaration.CtInterface;
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
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.declaration.CtSealable;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
public final class SpoonAssertions {
	public static CtAbstractInvocationAssert assertThat(CtAbstractInvocation<?> ctAbstractInvocation) {
		return new CtAbstractInvocationAssert(ctAbstractInvocation);
	}

	public static CtResourceAssert assertThat(CtResource<?> ctResource) {
		return new CtResourceAssert(ctResource);
	}

	public static CtProvidedServiceAssert assertThat(CtProvidedService ctProvidedService) {
		return new CtProvidedServiceAssert(ctProvidedService);
	}

	public static CtAbstractSwitchAssert assertThat(CtAbstractSwitch<?> ctAbstractSwitch) {
		return new CtAbstractSwitchAssert(ctAbstractSwitch);
	}

	public static CtConditionalAssert assertThat(CtConditional<?> ctConditional) {
		return new CtConditionalAssert(ctConditional);
	}

	public static CtCFlowBreakAssert assertThat(CtCFlowBreak ctCFlowBreak) {
		return new CtCFlowBreakAssert(ctCFlowBreak);
	}

	public static CtParameterAssert assertThat(CtParameter<?> ctParameter) {
		return new CtParameterAssert(ctParameter);
	}

	public static CtLoopAssert assertThat(CtLoop ctLoop) {
		return new CtLoopAssert(ctLoop);
	}

	public static CtWhileAssert assertThat(CtWhile ctWhile) {
		return new CtWhileAssert(ctWhile);
	}

	public static CtTypeReferenceAssert assertThat(CtTypeReference<?> ctTypeReference) {
		return new CtTypeReferenceAssert(ctTypeReference);
	}

	public static CtEnumValueAssert assertThat(CtEnumValue<?> ctEnumValue) {
		return new CtEnumValueAssert(ctEnumValue);
	}

	public static CtCatchVariableReferenceAssert assertThat(CtCatchVariableReference<?> ctCatchVariableReference) {
		return new CtCatchVariableReferenceAssert(ctCatchVariableReference);
	}

	public static CtContinueAssert assertThat(CtContinue ctContinue) {
		return new CtContinueAssert(ctContinue);
	}

	public static CtInterfaceAssert assertThat(CtInterface<?> ctInterface) {
		return new CtInterfaceAssert(ctInterface);
	}

	public static CtElementAssert assertThat(CtElement ctElement) {
		return new CtElementAssert(ctElement);
	}

	public static CtAssignmentAssert assertThat(CtAssignment<?, ?> ctAssignment) {
		return new CtAssignmentAssert(ctAssignment);
	}

	public static CtBinaryOperatorAssert assertThat(CtBinaryOperator<?> ctBinaryOperator) {
		return new CtBinaryOperatorAssert(ctBinaryOperator);
	}

	public static CtRecordPatternAssert assertThat(CtRecordPattern ctRecordPattern) {
		return new CtRecordPatternAssert(ctRecordPattern);
	}

	public static CtForEachAssert assertThat(CtForEach ctForEach) {
		return new CtForEachAssert(ctForEach);
	}

	public static CtConstructorAssert assertThat(CtConstructor<?> ctConstructor) {
		return new CtConstructorAssert(ctConstructor);
	}

	public static CtModuleRequirementAssert assertThat(CtModuleRequirement ctModuleRequirement) {
		return new CtModuleRequirementAssert(ctModuleRequirement);
	}

	public static CtRHSReceiverAssert assertThat(CtRHSReceiver<?> ctRHSReceiver) {
		return new CtRHSReceiverAssert(ctRHSReceiver);
	}

	public static CtSuperAccessAssert assertThat(CtSuperAccess<?> ctSuperAccess) {
		return new CtSuperAccessAssert(ctSuperAccess);
	}

	public static CtTargetedExpressionAssert assertThat(CtTargetedExpression<?, ?> ctTargetedExpression) {
		return new CtTargetedExpressionAssert(ctTargetedExpression);
	}

	public static CtAnonymousExecutableAssert assertThat(CtAnonymousExecutable ctAnonymousExecutable) {
		return new CtAnonymousExecutableAssert(ctAnonymousExecutable);
	}

	public static CtNamedElementAssert assertThat(CtNamedElement ctNamedElement) {
		return new CtNamedElementAssert(ctNamedElement);
	}

	public static CtSealableAssert assertThat(CtSealable ctSealable) {
		return new CtSealableAssert(ctSealable);
	}

	public static CtModifiableAssert assertThat(CtModifiable ctModifiable) {
		return new CtModifiableAssert(ctModifiable);
	}

	public static CtPackageDeclarationAssert assertThat(CtPackageDeclaration ctPackageDeclaration) {
		return new CtPackageDeclarationAssert(ctPackageDeclaration);
	}

	public static CtCommentAssert assertThat(CtComment ctComment) {
		return new CtCommentAssert(ctComment);
	}

	public static CtArrayWriteAssert assertThat(CtArrayWrite<?> ctArrayWrite) {
		return new CtArrayWriteAssert(ctArrayWrite);
	}

	public static CtFieldAccessAssert assertThat(CtFieldAccess<?> ctFieldAccess) {
		return new CtFieldAccessAssert(ctFieldAccess);
	}

	public static CtCodeSnippetAssert assertThat(CtCodeSnippet ctCodeSnippet) {
		return new CtCodeSnippetAssert(ctCodeSnippet);
	}

	public static CtWildcardReferenceAssert assertThat(CtWildcardReference ctWildcardReference) {
		return new CtWildcardReferenceAssert(ctWildcardReference);
	}

	public static CtTypeInformationAssert assertThat(CtTypeInformation ctTypeInformation) {
		return new CtTypeInformationAssert(ctTypeInformation);
	}

	public static CtRecordComponentAssert assertThat(CtRecordComponent ctRecordComponent) {
		return new CtRecordComponentAssert(ctRecordComponent);
	}

	public static CtThisAccessAssert assertThat(CtThisAccess<?> ctThisAccess) {
		return new CtThisAccessAssert(ctThisAccess);
	}

	public static CtPackageReferenceAssert assertThat(CtPackageReference ctPackageReference) {
		return new CtPackageReferenceAssert(ctPackageReference);
	}

	public static CtJavaDocAssert assertThat(CtJavaDoc ctJavaDoc) {
		return new CtJavaDocAssert(ctJavaDoc);
	}

	public static CtArrayReadAssert assertThat(CtArrayRead<?> ctArrayRead) {
		return new CtArrayReadAssert(ctArrayRead);
	}

	public static CtStatementListAssert assertThat(CtStatementList ctStatementList) {
		return new CtStatementListAssert(ctStatementList);
	}

	public static CtVariableWriteAssert assertThat(CtVariableWrite<?> ctVariableWrite) {
		return new CtVariableWriteAssert(ctVariableWrite);
	}

	public static CtCompilationUnitAssert assertThat(CtCompilationUnit ctCompilationUnit) {
		return new CtCompilationUnitAssert(ctCompilationUnit);
	}

	public static CtParameterReferenceAssert assertThat(CtParameterReference<?> ctParameterReference) {
		return new CtParameterReferenceAssert(ctParameterReference);
	}

	public static CtOperatorAssignmentAssert assertThat(CtOperatorAssignment<?, ?> ctOperatorAssignment) {
		return new CtOperatorAssignmentAssert(ctOperatorAssignment);
	}

	public static CtAnnotationFieldAccessAssert assertThat(CtAnnotationFieldAccess<?> ctAnnotationFieldAccess) {
		return new CtAnnotationFieldAccessAssert(ctAnnotationFieldAccess);
	}

	public static CtReceiverParameterAssert assertThat(CtReceiverParameter ctReceiverParameter) {
		return new CtReceiverParameterAssert(ctReceiverParameter);
	}

	public static CtAnnotationMethodAssert assertThat(CtAnnotationMethod<?> ctAnnotationMethod) {
		return new CtAnnotationMethodAssert(ctAnnotationMethod);
	}

	public static CtUnboundVariableReferenceAssert assertThat(CtUnboundVariableReference<?> ctUnboundVariableReference) {
		return new CtUnboundVariableReferenceAssert(ctUnboundVariableReference);
	}

	public static CtLabelledFlowBreakAssert assertThat(CtLabelledFlowBreak ctLabelledFlowBreak) {
		return new CtLabelledFlowBreakAssert(ctLabelledFlowBreak);
	}

	public static CtArrayAccessAssert assertThat(CtArrayAccess<?, ?> ctArrayAccess) {
		return new CtArrayAccessAssert(ctArrayAccess);
	}

	public static CtBlockAssert assertThat(CtBlock<?> ctBlock) {
		return new CtBlockAssert(ctBlock);
	}

	public static CtPackageAssert assertThat(CtPackage ctPackage) {
		return new CtPackageAssert(ctPackage);
	}

	public static CtTryWithResourceAssert assertThat(CtTryWithResource ctTryWithResource) {
		return new CtTryWithResourceAssert(ctTryWithResource);
	}

	public static CtActualTypeContainerAssert assertThat(CtActualTypeContainer ctActualTypeContainer) {
		return new CtActualTypeContainerAssert(ctActualTypeContainer);
	}

	public static CtClassAssert assertThat(CtClass<?> ctClass) {
		return new CtClassAssert(ctClass);
	}

	public static CtVariableReferenceAssert assertThat(CtVariableReference<?> ctVariableReference) {
		return new CtVariableReferenceAssert(ctVariableReference);
	}

	public static CtSwitchAssert assertThat(CtSwitch<?> ctSwitch) {
		return new CtSwitchAssert(ctSwitch);
	}

	public static CtYieldStatementAssert assertThat(CtYieldStatement ctYieldStatement) {
		return new CtYieldStatementAssert(ctYieldStatement);
	}

	public static CtSynchronizedAssert assertThat(CtSynchronized ctSynchronized) {
		return new CtSynchronizedAssert(ctSynchronized);
	}

	public static CtTryAssert assertThat(CtTry ctTry) {
		return new CtTryAssert(ctTry);
	}

	public static CtAssertAssert assertThat(CtAssert<?> ctAssert) {
		return new CtAssertAssert(ctAssert);
	}

	public static CtImportAssert assertThat(CtImport ctImport) {
		return new CtImportAssert(ctImport);
	}

	public static CtInvocationAssert assertThat(CtInvocation<?> ctInvocation) {
		return new CtInvocationAssert(ctInvocation);
	}

	public static CtTypeParameterReferenceAssert assertThat(CtTypeParameterReference ctTypeParameterReference) {
		return new CtTypeParameterReferenceAssert(ctTypeParameterReference);
	}

	public static CtStatementAssert assertThat(CtStatement ctStatement) {
		return new CtStatementAssert(ctStatement);
	}

	public static CtFieldWriteAssert assertThat(CtFieldWrite<?> ctFieldWrite) {
		return new CtFieldWriteAssert(ctFieldWrite);
	}

	public static CtUnaryOperatorAssert assertThat(CtUnaryOperator<?> ctUnaryOperator) {
		return new CtUnaryOperatorAssert(ctUnaryOperator);
	}

	public static CtVariableReadAssert assertThat(CtVariableRead<?> ctVariableRead) {
		return new CtVariableReadAssert(ctVariableRead);
	}

	public static CtExecutableReferenceAssert assertThat(CtExecutableReference<?> ctExecutableReference) {
		return new CtExecutableReferenceAssert(ctExecutableReference);
	}

	public static CtCodeSnippetExpressionAssert assertThat(CtCodeSnippetExpression<?> ctCodeSnippetExpression) {
		return new CtCodeSnippetExpressionAssert(ctCodeSnippetExpression);
	}

	public static CtTypedElementAssert assertThat(CtTypedElement<?> ctTypedElement) {
		return new CtTypedElementAssert(ctTypedElement);
	}

	public static CtForAssert assertThat(CtFor ctFor) {
		return new CtForAssert(ctFor);
	}

	public static CtTypeParameterAssert assertThat(CtTypeParameter ctTypeParameter) {
		return new CtTypeParameterAssert(ctTypeParameter);
	}

	public static CtFormalTypeDeclarerAssert assertThat(CtFormalTypeDeclarer ctFormalTypeDeclarer) {
		return new CtFormalTypeDeclarerAssert(ctFormalTypeDeclarer);
	}

	public static CtExecutableAssert assertThat(CtExecutable<?> ctExecutable) {
		return new CtExecutableAssert(ctExecutable);
	}

	public static CtLocalVariableAssert assertThat(CtLocalVariable<?> ctLocalVariable) {
		return new CtLocalVariableAssert(ctLocalVariable);
	}

	public static CtMultiTypedElementAssert assertThat(CtMultiTypedElement ctMultiTypedElement) {
		return new CtMultiTypedElementAssert(ctMultiTypedElement);
	}

	public static CtModuleAssert assertThat(CtModule ctModule) {
		return new CtModuleAssert(ctModule);
	}

	public static CtIfAssert assertThat(CtIf ctIf) {
		return new CtIfAssert(ctIf);
	}

	public static CtRecordAssert assertThat(CtRecord ctRecord) {
		return new CtRecordAssert(ctRecord);
	}

	public static CtSwitchExpressionAssert assertThat(CtSwitchExpression<?, ?> ctSwitchExpression) {
		return new CtSwitchExpressionAssert(ctSwitchExpression);
	}

	public static CtPackageExportAssert assertThat(CtPackageExport ctPackageExport) {
		return new CtPackageExportAssert(ctPackageExport);
	}

	public static CtTypeAssert assertThat(CtType<?> ctType) {
		return new CtTypeAssert(ctType);
	}

	public static CtVariableAssert assertThat(CtVariable<?> ctVariable) {
		return new CtVariableAssert(ctVariable);
	}

	public static CtCaseAssert assertThat(CtCase<?> ctCase) {
		return new CtCaseAssert(ctCase);
	}

	public static CtModuleReferenceAssert assertThat(CtModuleReference ctModuleReference) {
		return new CtModuleReferenceAssert(ctModuleReference);
	}

	public static CtCatchAssert assertThat(CtCatch ctCatch) {
		return new CtCatchAssert(ctCatch);
	}

	public static CtConstructorCallAssert assertThat(CtConstructorCall<?> ctConstructorCall) {
		return new CtConstructorCallAssert(ctConstructorCall);
	}

	public static CtMethodAssert assertThat(CtMethod<?> ctMethod) {
		return new CtMethodAssert(ctMethod);
	}

	public static CtArrayTypeReferenceAssert assertThat(CtArrayTypeReference<?> ctArrayTypeReference) {
		return new CtArrayTypeReferenceAssert(ctArrayTypeReference);
	}

	public static CtIntersectionTypeReferenceAssert assertThat(CtIntersectionTypeReference<?> ctIntersectionTypeReference) {
		return new CtIntersectionTypeReferenceAssert(ctIntersectionTypeReference);
	}

	public static CtUsedServiceAssert assertThat(CtUsedService ctUsedService) {
		return new CtUsedServiceAssert(ctUsedService);
	}

	public static CtLambdaAssert assertThat(CtLambda<?> ctLambda) {
		return new CtLambdaAssert(ctLambda);
	}

	public static CtPatternAssert assertThat(CtPattern ctPattern) {
		return new CtPatternAssert(ctPattern);
	}

	public static CtTypePatternAssert assertThat(CtTypePattern ctTypePattern) {
		return new CtTypePatternAssert(ctTypePattern);
	}

	public static CtTypeAccessAssert assertThat(CtTypeAccess<?> ctTypeAccess) {
		return new CtTypeAccessAssert(ctTypeAccess);
	}

	public static CtBodyHolderAssert assertThat(CtBodyHolder ctBodyHolder) {
		return new CtBodyHolderAssert(ctBodyHolder);
	}

	public static CtNewArrayAssert assertThat(CtNewArray<?> ctNewArray) {
		return new CtNewArrayAssert(ctNewArray);
	}

	public static CtShadowableAssert assertThat(CtShadowable ctShadowable) {
		return new CtShadowableAssert(ctShadowable);
	}

	public static CtTextBlockAssert assertThat(CtTextBlock ctTextBlock) {
		return new CtTextBlockAssert(ctTextBlock);
	}

	public static CtThrowAssert assertThat(CtThrow ctThrow) {
		return new CtThrowAssert(ctThrow);
	}

	public static CtReferenceAssert assertThat(CtReference ctReference) {
		return new CtReferenceAssert(ctReference);
	}

	public static CtCodeElementAssert assertThat(CtCodeElement ctCodeElement) {
		return new CtCodeElementAssert(ctCodeElement);
	}

	public static CtJavaDocTagAssert assertThat(CtJavaDocTag ctJavaDocTag) {
		return new CtJavaDocTagAssert(ctJavaDocTag);
	}

	public static CtLiteralAssert assertThat(CtLiteral<?> ctLiteral) {
		return new CtLiteralAssert(ctLiteral);
	}

	public static CtFieldAssert assertThat(CtField<?> ctField) {
		return new CtFieldAssert(ctField);
	}

	public static CtReturnAssert assertThat(CtReturn<?> ctReturn) {
		return new CtReturnAssert(ctReturn);
	}

	public static CtFieldReadAssert assertThat(CtFieldRead<?> ctFieldRead) {
		return new CtFieldReadAssert(ctFieldRead);
	}

	public static CtCodeSnippetStatementAssert assertThat(CtCodeSnippetStatement ctCodeSnippetStatement) {
		return new CtCodeSnippetStatementAssert(ctCodeSnippetStatement);
	}

	public static CtDoAssert assertThat(CtDo ctDo) {
		return new CtDoAssert(ctDo);
	}

	public static CtAnnotationAssert assertThat(CtAnnotation<?> ctAnnotation) {
		return new CtAnnotationAssert(ctAnnotation);
	}

	public static CtVariableAccessAssert assertThat(CtVariableAccess<?> ctVariableAccess) {
		return new CtVariableAccessAssert(ctVariableAccess);
	}

	public static CtBreakAssert assertThat(CtBreak ctBreak) {
		return new CtBreakAssert(ctBreak);
	}

	public static CtExpressionAssert assertThat(CtExpression<?> ctExpression) {
		return new CtExpressionAssert(ctExpression);
	}

	public static CtFieldReferenceAssert assertThat(CtFieldReference<?> ctFieldReference) {
		return new CtFieldReferenceAssert(ctFieldReference);
	}

	public static CtEnumAssert assertThat(CtEnum<?> ctEnum) {
		return new CtEnumAssert(ctEnum);
	}

	public static CtCasePatternAssert assertThat(CtCasePattern ctCasePattern) {
		return new CtCasePatternAssert(ctCasePattern);
	}

	public static CtModuleDirectiveAssert assertThat(CtModuleDirective ctModuleDirective) {
		return new CtModuleDirectiveAssert(ctModuleDirective);
	}

	public static CtTypeMemberWildcardImportReferenceAssert assertThat(CtTypeMemberWildcardImportReference ctTypeMemberWildcardImportReference) {
		return new CtTypeMemberWildcardImportReferenceAssert(ctTypeMemberWildcardImportReference);
	}

	public static CtUnnamedPatternAssert assertThat(CtUnnamedPattern ctUnnamedPattern) {
		return new CtUnnamedPatternAssert(ctUnnamedPattern);
	}

	public static CtNewClassAssert assertThat(CtNewClass<?> ctNewClass) {
		return new CtNewClassAssert(ctNewClass);
	}

	public static CtLocalVariableReferenceAssert assertThat(CtLocalVariableReference<?> ctLocalVariableReference) {
		return new CtLocalVariableReferenceAssert(ctLocalVariableReference);
	}

	public static CtTypeMemberAssert assertThat(CtTypeMember ctTypeMember) {
		return new CtTypeMemberAssert(ctTypeMember);
	}

	public static CtAnnotationTypeAssert assertThat(CtAnnotationType<?> ctAnnotationType) {
		return new CtAnnotationTypeAssert(ctAnnotationType);
	}

	public static CtCatchVariableAssert assertThat(CtCatchVariable<?> ctCatchVariable) {
		return new CtCatchVariableAssert(ctCatchVariable);
	}

	public static CtExecutableReferenceExpressionAssert assertThat(CtExecutableReferenceExpression<?, ?> ctExecutableReferenceExpression) {
		return new CtExecutableReferenceExpressionAssert(ctExecutableReferenceExpression);
	}
}
