/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
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
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtAbstractVisitor;
import spoon.reflect.visitor.CtVisitor;

public class IdentifierVerifier {

	private static final String ASTERISK_LITERAL = "*";
	private static final String FALSE_LITERAL = "false";
	private static final String TRUE_LITERAL = "true";
	private static final String NULL_LITERAL = "null";
	private static final String PACKAGE_SEPARATOR_REGEX = "\\.";
	private static final String ARRAY_SUFFIX_REGEX = "(\\[\\])+$";
	private static final String NUMERIC_PREFIX = "^\\d+";
	private static final String WILDCARD_STRING = "?";
	private static Set<String> keywords = fillWithKeywords();
	private static Set<String> typeKeywords = fillWithTypeKeywords();
	private boolean lenient;
	private boolean strictMode;
	private SpoonException exception;

	private static String identifierError =
			"The identifier %s in %s at %s violates contract defined in jls 3.8 for identifier, because it has illegal chars.";
	private static String keywordError =
			"The identifier %s in %s at %s violates contract defined in jls 3.8 for identifier, because it is a keyword.";

	public Optional<SpoonException> checkIdentifier(CtElement element) {
		element.accept(identifierVisitor);
		if (exception != null) {
			if (lenient) {
				throw exception;
			} else {
				Optional<SpoonException> error = Optional.of(exception);
				exception = null;
				return error;
			}
		}
		return Optional.empty();
	}

	/**
	 * Notes:
	 * - We still need to allow an empty identifier.
	 */

	public IdentifierVerifier(boolean strictMode) {
		this.strictMode = strictMode;
	}

	private CtVisitor identifierVisitor = new CtAbstractVisitor() {

		@Override
		public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
			// no check needed.
			// an annotation usage refers to a type. We check if the type has a correct name and not the usage.
		}

		@Override
		public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
			// no check needed, because not a single identifier but more combined code.
		}

		@Override
		public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
			// no check needed, because not a single identifier but more combined code.
		}

		@Override
		public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
			String identifier = annotationType.getSimpleName();
			if (!strictMode || annotationType.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, annotationType);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)) {
				exception = createException(keywordError, annotationType);
				return;
			}
		}

		@Override
		public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
			//TODO: ??? identifier makes no sense here
			// CtAnonymousExecutable have no identifier => no check needed
		}

		@Override
		public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
			// CtArrayRead have no identifier => no check needed
		}

		@Override
		public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
			// CtArrayWrite have no identifier => no check needed
		}

		@Override
		public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
			String identifier = reference.getSimpleName();
			// arrayTypeReferences have one or multiple [] at the end
			identifier = identifier.replaceAll(ARRAY_SUFFIX_REGEX, "");
			if (!strictMode || reference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier) && !isWildcard(identifier)) {
				//wildcard identifiers "?" happen for typeReferences.
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}

		}

		@Override
		public <T> void visitCtAssert(CtAssert<T> asserted) {
			// CtAssert have no identifier => no check needed
		}

		@Override
		public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
			// CtAssignment have no identifier => no check needed
		}

		@Override
		public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
			// CtBinaryOperator have no identifier => no check needed
		}

		@Override
		public <R> void visitCtBlock(CtBlock<R> block) {
			// CtBlock have no identifier => no check needed
		}

		@Override
		public void visitCtBreak(CtBreak breakStatement) {
			// CtBreak have no identifier => no check needed
		}

		@Override
		public <S> void visitCtCase(CtCase<S> caseStatement) {
			// CtCase have no identifier => no check needed
		}

		@Override
		public void visitCtCatch(CtCatch catchBlock) {
			// CtCatch have no identifier => no check needed
		}

		@Override
		public <T> void visitCtClass(CtClass<T> ctClass) {
			String identifier = ctClass.getSimpleName();
			if (ctClass.isAnonymous()) {
				// anonymous classes have no identifier but only numbers. No reason to check it.
				return;
			}
			if (!strictMode || ctClass.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, ctClass);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, ctClass);
				return;
			}
		}

		@Override
		public void visitCtTypeParameter(CtTypeParameter typeParameter) {
			String identifier = typeParameter.getSimpleName();
			if (!strictMode || typeParameter.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, typeParameter);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)) {
				exception = createException(keywordError, typeParameter);
				return;
			}
		}

		@Override
		public <T> void visitCtConditional(CtConditional<T> conditional) {
			// CtConditional have no identifier => no check needed
		}

		@Override
		public <T> void visitCtConstructor(CtConstructor<T> c) {
			// maybe check here for consistency reasons, even if the case shouldn't exist.
			if (!c.getSimpleName().equals(CtExecutableReference.CONSTRUCTOR_NAME)) {
				exception = createException(identifierError, c);
				return;
			}
		}

		@Override
		public void visitCtContinue(CtContinue continueStatement) {
			// CtContinue have no identifier => no check needed
		}

		@Override
		public void visitCtDo(CtDo doLoop) {
			// CtDo have no identifier => no check needed
		}

		@Override
		public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
			String identifier = ctEnum.getSimpleName();
			if (ctEnum.isAnonymous()) {
				// anonymous classes have no identifier but only numbers. No reason to check it.
				return;
			}
			if (!strictMode || ctEnum.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, ctEnum);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, ctEnum);
				return;
			}

		}

		@Override
		public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (identifier.equals(CtExecutableReference.CONSTRUCTOR_NAME)) {
				// we allow <init> method references
				return;
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public <T> void visitCtField(CtField<T> f) {
			String identifier = f.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, f);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, f);
				return;
			}
		}

		@Override
		public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
			String identifier = enumValue.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, enumValue);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, enumValue);
				return;
			}
		}

		@Override
		public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
			// CtThisAccess have no identifier => no check needed
		}

		@Override
		public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public void visitCtFor(CtFor forLoop) {
			// CtFor have no identifier => no check needed
		}

		@Override
		public void visitCtForEach(CtForEach foreach) {
			// CtForEach have no identifier => no check needed
		}

		@Override
		public void visitCtIf(CtIf ifElement) {
			// CtIf have no identifier => no check needed
		}

		@Override
		public <T> void visitCtInterface(CtInterface<T> intrface) {
			String identifier = intrface.getSimpleName();
			if (!strictMode || intrface.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, intrface);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, intrface);
				return;
			}
		}

		@Override
		public <T> void visitCtInvocation(CtInvocation<T> invocation) {
			// CtInvocation have no identifier => no check needed
		}

		@Override
		public <T> void visitCtLiteral(CtLiteral<T> literal) {
			// CtLiteral have no identifier => no check needed
		}

		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
			String identifier = localVariable.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, localVariable);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, localVariable);
				return;
			}

		}

		@Override
		public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
			String identifier = catchVariable.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, catchVariable);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, catchVariable);
				return;
			}
		}

		@Override
		public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			String identifier = m.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, m);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, m);
				return;
			}

		}

		@Override
		public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
			String identifier = annotationMethod.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, annotationMethod);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, annotationMethod);
				return;
			}

		}

		@Override
		public <T> void visitCtNewArray(CtNewArray<T> newArray) {
			// CtNewArray have no identifier => no check needed
		}

		@Override
		public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
			// CtConstructorCall have no identifier => no check needed
		}

		@Override
		public <T> void visitCtNewClass(CtNewClass<T> newClass) {
			// CtNewClass have no identifier => no check needed
		}

		@Override
		public <T> void visitCtLambda(CtLambda<T> lambda) {
			String identifier = lambda.getSimpleName();
			if (!identifier.startsWith(CtExecutableReference.LAMBDA_NAME_PREFIX)) {
				exception = createException(identifierError, lambda);
				return;
			}
		}

		@Override
		public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
				CtExecutableReferenceExpression<T, E> expression) {
			// CtExecutableReferenceExpression have no identifier => no check needed
		}

		@Override
		public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
			// CtOperatorAssignment have no identifier => no check needed
		}

		@Override
		public void visitCtPackage(CtPackage ctPackage) {
			String identifier = ctPackage.getSimpleName();
			if (identifier.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				// unnamed Package is legal
				return;
			}
			String[] identifierParts = identifier.split(PACKAGE_SEPARATOR_REGEX);
			for (String part : identifierParts) {
				if (!isJavaIdentifier(part)) {
					exception = createException(identifierError, ctPackage);
					return;
				}
				if (isKeyword(part) || isNullLiteral(part) || isBooleanLiteral(part)
						|| isTypeKeyword(part)) {
					exception = createException(keywordError, ctPackage);
					return;
				}
			}
		}

		@Override
		public void visitCtPackageReference(CtPackageReference reference) {
			String identifier = reference.getSimpleName();
			if (identifier.equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
				// unnamed Package is legal
				return;
			}
			String[] identifierParts = identifier.split(PACKAGE_SEPARATOR_REGEX);
			for (String part : identifierParts) {
				if (!isJavaIdentifier(part)) {
					exception = createException(identifierError, reference);
					return;
				}
				if (isKeyword(part) || isNullLiteral(part) || isBooleanLiteral(part)
						|| isTypeKeyword(part)) {
					exception = createException(keywordError, reference);
					return;
				}
			}
		}

		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			String identifier = parameter.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, parameter);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, parameter);
				return;
			}
		}

		@Override
		public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}

		}

		@Override
		public <R> void visitCtReturn(CtReturn<R> returnStatement) {
			// CtReturn have no identifier => no check needed
		}

		@Override
		public <R> void visitCtStatementList(CtStatementList statements) {
			// CtStatementList have no identifier => no check needed

		}

		@Override
		public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
			// CtSwitch have no identifier => no check needed
		}

		@Override
		public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {
			// CtSwitchExpression have no identifier => no check needed
		}

		@Override
		public void visitCtSynchronized(CtSynchronized synchro) {
			// CtSynchronized have no identifier => no check needed

		}

		@Override
		public void visitCtThrow(CtThrow throwStatement) {
			// CtThrow have no identifier => no check needed
		}

		@Override
		public void visitCtTry(CtTry tryBlock) {
			// CtTry have no identifier => no check needed

		}

		@Override
		public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
			// CtTryWithResource have no identifier => no check needed
		}

		@Override
		public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
			//TODO: can generic refer to a local type???
			// when dont check for wildcard here, because a generic Type cant refere to wildcard. class Foo<?> is not legal java.
			String identifier = ref.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, ref);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, ref);
				return;
			}
		}

		@Override
		public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
			String identifier = wildcardReference.getSimpleName();
			if (!strictMode || wildcardReference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier) && !isWildcard(identifier)) {
				//wildcard identifiers "?" happen for typeReferences.
				exception = createException(identifierError, wildcardReference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier)) {
				exception = createException(keywordError, wildcardReference);
				return;
			}
		}

		@Override
		public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
			String identifier = reference.getSimpleName();
			// arrayTypeReferences have one or multiple [] at the end
			identifier = identifier.replaceAll(ARRAY_SUFFIX_REGEX, "");
			if (strictMode || reference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier) && !isWildcard(identifier)) {
				//wildcard identifiers "?" happen for typeReferences.
				exception = createException(identifierError, reference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}
		}

		@Override
		public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (identifier.equals(CtTypeReference.NULL_TYPE_NAME)
					|| identifier.equals(CtTypeReference.OMITTED_TYPE_ARG_NAME)) {
				// we need to allow null and omitted type
				return;
			}
			if (strictMode || reference.isArray()) {
				// remove array []
				identifier = identifier.replaceAll(ARRAY_SUFFIX_REGEX, "");
			}
			if (!strictMode || reference.isGenerics() || reference.isParameterized()) {
				// remove generic argument. Generics are checked in {#visitCtTypeParameterReference(CtTypeParameterReference)}
				identifier = identifier.replaceAll("<.*>", "");
			}
			if (reference.isAnonymous()) {
				// anonymous types are numeric only.
				// TODO: is there a reason to check it?
				return;
			}
			if (!strictMode || reference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, reference);
				return;
			}
			// some type references have simpleName null. TODO: Why and not NULL_TYPE_NAME?
			if (isKeyword(identifier) || isBooleanLiteral(identifier)) {
				exception = createException(keywordError, reference);
				return;
			}

		}

		@Override
		public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
			// CtTypeAccess have no identifier => no check needed
		}

		@Override
		public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
			// CtUnaryOperator have no identifier => no check needed
		}

		@Override
		public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
			// CtVariableRead have no identifier => no check needed
		}

		@Override
		public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
			// CtVariableWrite have no identifier => no check needed
		}

		@Override
		public void visitCtWhile(CtWhile whileLoop) {
			// CtWhile have no identifier => no check needed
		}

		@Override
		public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
			// CtAnnotationFieldAccess have no identifier => no check needed
		}

		@Override
		public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
			// CtSuperAccess have no identifier => no check needed
		}

		@Override
		public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
			// CtFieldWrite have no identifier => no check needed
		}

		@Override
		public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
			// CtSuperAccess have no identifier => no check needed
		}

		@Override
		public void visitCtComment(CtComment comment) {
			// CtComment have no identifier => no check needed
		}

		@Override
		public void visitCtJavaDoc(CtJavaDoc comment) {
			// CtJavaDoc have no identifier => no check needed
		}

		@Override
		public void visitCtJavaDocTag(CtJavaDocTag docTag) {
			//TODO: do they have limitations?
		}

		@Override
		public void visitCtImport(CtImport ctImport) {
			// CtImport have no identifier => no check needed
		}

		@Override
		public void visitCtModule(CtModule module) {
			String identifier = module.getSimpleName();
			if (identifier.equals(CtModule.TOP_LEVEL_MODULE_NAME)) {
				// unnamend module is allowed
				return;
			}
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, module);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, module);
				return;
			}
		}

		@Override
		public void visitCtModuleReference(CtModuleReference moduleReference) {
			String identifier = moduleReference.getSimpleName();
			if (!isJavaIdentifier(identifier)) {
				exception = createException(identifierError, moduleReference);
				return;
			}
			if (isKeyword(identifier) || isNullLiteral(identifier) || isBooleanLiteral(identifier)
					|| isTypeKeyword(identifier)) {
				exception = createException(keywordError, moduleReference);
				return;
			}

		}

		@Override
		public void visitCtPackageExport(CtPackageExport moduleExport) {
			// CtPackageExport have no identifier => no check needed
		}

		@Override
		public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
			// CtModuleRequirement have no identifier => no check needed
		}

		@Override
		public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
			// CtProvidedService have no identifier => no check needed
		}

		@Override
		public void visitCtUsedService(CtUsedService usedService) {
			// CtUsedService have no identifier => no check needed
		}

		@Override
		public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
			// CtCompilationUnit have no identifier => no check needed
		}

		@Override
		public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
			// CtPackageDeclaration have no identifier => no check needed
		}

		@Override
		public void visitCtTypeMemberWildcardImportReference(
				CtTypeMemberWildcardImportReference wildcardReference) {
			String identifier = wildcardReference.getSimpleName();
			String[] identifierParts = identifier.split(PACKAGE_SEPARATOR_REGEX);
			for (String part : Arrays.copyOfRange(identifierParts, 0, identifierParts.length - 1)) {
				if (!isJavaIdentifier(part)) {
					exception = createException(identifierError, wildcardReference);
					return;
				}
				if (isKeyword(part) || isNullLiteral(part) || isBooleanLiteral(part)
						|| isTypeKeyword(part)) {
					exception = createException(keywordError, wildcardReference);
					return;
				}
			}
			if (!identifierParts[identifierParts.length - 1].equals(ASTERISK_LITERAL)) {
				exception = createException(identifierError, wildcardReference);
				return;
			}
		}

		@Override
		public void visitCtYieldStatement(CtYieldStatement statement) {
			// CtYieldStatement have no identifier => no check needed
		}


		/**
		 * Checks if a identifier is a keyword. See jls 3.9 for all keywords.
		 * @param identifier to check
		 * @return true if the identifier is a keyword. False otherwise.
		 */
		private boolean isKeyword(String identifier) {
			return keywords.contains(identifier);
		}

		/**
		 * Checks if a identifier is a type keyword. See jls 3.9 for all type keywords.
		 * @param identifier to check
		 * @return true if the identifier is a type keyword. False otherwise.
		 */
		private boolean isTypeKeyword(String identifier) {
			return typeKeywords.contains(identifier);
		}

		/**
		 * Checks if a identifier is a boolean literal. As defined in jls 3.8 an identifier is never a boolean literal.
		 * boolean literals are {true,false}
		 * @param identifier to check
		 * @return True if the identifier is a boolean literal, false otherwise.
		 */
		private boolean isBooleanLiteral(String identifier) {
			return identifier.equals(TRUE_LITERAL) || identifier.equals(FALSE_LITERAL);
		}

		/**
		 * Checks if an identifier is a null literal. As defined in jls 3.8 an identifier is never a null literal.
		 * null literals are {null}
		 * @param identifier to check
		 * @return true if the identifier is a null literal, false otherwise.
		 */
		private boolean isNullLiteral(String identifier) {
			return identifier.equals(NULL_LITERAL);
		}

		/**
		 * Checks if an identifier is a legal java identifier. See {@link #isJavaLetterStart(String)} and {@link #isJavaIdentifierPart(String)} for details.
		 * @param identifier to check
		 * @return true is the identifier is a correct javaIdentifier, false otherwise.
		 */
		private boolean isJavaIdentifier(String identifier) {
			return identifier.isEmpty()
					|| (isJavaLetterStart(identifier) && isJavaIdentifierPart(identifier));
		}

		/**
		 * Checks if an identifier has a legal identifierStart. A legal identifier starts with a java letter.
		 * JavaLetters are defined in jls 3.8.
		 * @param identifier to check
		 * @return true is the identifier starts with a java letter, false otherwise.
		 */
		private boolean isJavaLetterStart(String identifier) {
			return Character.isJavaIdentifierStart(identifier.charAt(0));
		}

		/**
		 * Checks if an identifier consists of legal javaIdentifierParts. All chars expect the first are checked
		 * @param identifier to check
		 * @return true if the identifier consists of only 1 letter or all chars expect first are legalIdentifierParts. False otherwise.
		 */
		private boolean isJavaIdentifierPart(String identifier) {
			return identifier.length() == 1 || identifier.subSequence(1, identifier.length()).chars()
					.allMatch(Character::isJavaIdentifierPart);
		}

		private String convertLocalTypeIdentifier(String identifier) {
			return identifier.replaceAll(NUMERIC_PREFIX, "");
		}

		/**
		 * checks if a  identifier is a wildcard.
		 * @param identifier to check.
		 * @return true if identifier is wildcard, false otherwise.
		 */
		private boolean isWildcard(String identifier) {
			return identifier.equals(WILDCARD_STRING);
		}

		/**
		 * Creates a formatted exception from input.
		 * @param rawOutput to format.
		 * @param element with identifier problem.
		 * @return a @link{spoon.SpoonException.SpoonException(String)}, with the reason.
		 */
		private SpoonException createException(String rawOutput, CtNamedElement element) {
			return new SpoonException(String.format(identifierError, element.getSimpleName(),
					element.getPath(), element.getPosition().toString()));
		}

		/**
		 * Creates a formatted exception from input.
		 * @param rawOutput to format.
		 * @param element with identifier problem.
		 * @return a @link{spoon.SpoonException.SpoonException(String)}, with the reason.
		 */
		private SpoonException createException(String rawOutput, CtReference element) {
			return element.isParentInitialized()
					? new SpoonException(String.format(identifierError, element.getSimpleName(),
							element.getPath(), element.getPosition().toString()))
					: new SpoonException(String.format(identifierError, element.getSimpleName(), "",
							element.getPosition().toString()));
		}
	};


	private static Set<String> fillWithKeywords() {
		// "class" still does not work
		return Stream
				.of("abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package",
						"synchronized", "do", "goto", "private", "this", "break", "implements", "protected",
						"throw", "else", "import", "public", "throws", "case", "enum", "instanceof", "return",
						"transient", "catch", "extends", "try", "final", "interface", "static", "finally",
						"strictfp", "volatile", "const", "native", "super", "while", "_")
				.collect(Collectors.toCollection(HashSet::new));
	}

	private static Set<String> fillWithTypeKeywords() {
		return Stream.of("int", "short", "char", "void", "byte", "float", TRUE_LITERAL, FALSE_LITERAL,
				"boolean", "double", "long", NULL_LITERAL).collect(Collectors.toCollection(HashSet::new));
	}
}
