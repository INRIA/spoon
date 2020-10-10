/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import static spoon.reflect.reference.CtExecutableReference.CONSTRUCTOR_NAME;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
	private static final String CLASS_LITERAL = "class";
	private static final String PACKAGE_SEPARATOR_REGEX = "\\.";
	private static final String ARRAY_SUFFIX_REGEX = "(\\[\\])+$";
	private static final String NUMERIC_PREFIX = "^\\d+";
	private static final String WILDCARD_STRING = "?";
	private static Set<String> keywords = fillWithKeywords();
	private static Set<String> typeKeywords = fillWithTypeKeywords();
	private boolean strictMode;
	private SpoonException exception;

	private static String identifierError =
			"The identifier %s for element %s violates contract defined in jls 3.8 for identifier, because it has illegal chars.";
	private static String keywordError =
			"The identifier %s for element %s violates contract defined in jls 3.8 for identifier, because it is a keyword.";

	public Optional<SpoonException> checkIdentifier(CtElement element) {
		try {
			element.accept(identifierVisitor);
		} catch (SpoonException e) {
				return Optional.of(e);
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

		@NoIdentifier
		@Override
		public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
			// no check needed, because not a single identifier but more combined code.
		}

		@NoIdentifier
		@Override
		public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
			// no check needed, because not a single identifier but more combined code.
		}
		@SupportedIdentifiers()
		@Override
		public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
			// A java annotation is never a localType => there is no prefix
			String identifier = annotationType.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, annotationType));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, annotationType));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, annotationType));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, annotationType));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, annotationType));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, annotationType));
		}

		@NoIdentifier
		@Override
		public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtAssert(CtAssert<T> asserted) {
		}

		@NoIdentifier
		@Override
		public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		}

		@NoIdentifier
		@Override
		public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		}

		@NoIdentifier
		@Override
		public <R> void visitCtBlock(CtBlock<R> block) {
		}

		@NoIdentifier
		@Override
		public void visitCtBreak(CtBreak breakStatement) {
		}

		@NoIdentifier
		@Override
		public <S> void visitCtCase(CtCase<S> caseStatement) {
		}

		@NoIdentifier
		@Override
		public void visitCtCatch(CtCatch catchBlock) {
		}

		@SupportedIdentifiers(localType = true)
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

			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, ctClass));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, ctClass));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, ctClass));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, ctClass));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, ctClass));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, ctClass));

		}
		@SupportedIdentifiers(localType = true)
		@Override
		public void visitCtTypeParameter(CtTypeParameter typeParameter) {
			// a type parameter is like 'class Foo<A>' "A"
			String identifier = typeParameter.getSimpleName();
			if (!strictMode || typeParameter.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}

			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, typeParameter));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, typeParameter));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, typeParameter));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, typeParameter));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, typeParameter));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, typeParameter));
		}

		@NoIdentifier
		@Override
		public <T> void visitCtConditional(CtConditional<T> conditional) {
			// CtConditional have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtConstructor(CtConstructor<T> c) {
			// maybe check here for consistency reasons, even if the case shouldn't exist.
			checkInvertedCondition((name) -> name.equals(CONSTRUCTOR_NAME), c.getSimpleName(), () -> createException(identifierError, c));
		}

		@NoIdentifier
		@Override
		public void visitCtContinue(CtContinue continueStatement) {
			// CtContinue have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtDo(CtDo doLoop) {
			// CtDo have no identifier => no check needed
		}

		@SupportedIdentifiers()
		@Override
		public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
			String identifier = ctEnum.getSimpleName();
			// enums are never a local type or anonymous
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, ctEnum));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, ctEnum));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, ctEnum));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, ctEnum));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, ctEnum));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, ctEnum));
		}
		@SupportedIdentifiers()
		@Override
		public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
			String identifier = reference.getSimpleName();
			if (identifier.equals(CONSTRUCTOR_NAME) && reference.isConstructor()) {
				// we allow <init> method references
				return;
			}
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));
		}
		@SupportedIdentifiers()
		@Override
		public <T> void visitCtField(CtField<T> f) {
			String identifier = f.getSimpleName();

			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, f));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, f));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, f));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, f));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, f));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, f));
		}

		@Override
		public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {
			String identifier = enumValue.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, enumValue));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, enumValue));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, enumValue));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, enumValue));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, enumValue));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, enumValue));
		}

		@NoIdentifier
		@Override
		public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
			// CtThisAccess have no identifier => no check needed
		}
		@SupportedIdentifiers(classKeyword = true, fqName = true)
		@Override
		public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
			String identifier = reference.getSimpleName();
			for (String identifierPart : identifier.split("\\.")) {
				checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

				checkCondition(this::isKeyword, identifierPart, () -> createException(keywordError, reference));
				checkCondition(this::isTypeKeyword, identifierPart, () -> createException(keywordError, reference));
				checkCondition(this::isNullLiteral, identifierPart, () -> createException(keywordError, reference));
				checkCondition(this::isBooleanLiteral, identifierPart, () -> createException(keywordError, reference));
			}
		}

		@Override
		public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));
		}
		@NoIdentifier
		@Override
		public void visitCtFor(CtFor forLoop) {
			// CtFor have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtForEach(CtForEach foreach) {
			// CtForEach have no identifier => no check needed
		}

		@NoIdentifier
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
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, intrface));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, intrface));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, intrface));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, intrface));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, intrface));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, intrface));
		}

		@NoIdentifier
		@Override
		public <T> void visitCtInvocation(CtInvocation<T> invocation) {
			// CtInvocation have no identifier => no check needed
		}
		@NoIdentifier

		@Override
		public <T> void visitCtLiteral(CtLiteral<T> literal) {
			// CtLiteral have no identifier => no check needed
		}

		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
			String identifier = localVariable.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, localVariable));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, localVariable));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, localVariable));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, localVariable));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, localVariable));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, localVariable));

		}

		@Override
		public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));
		}

		@Override
		public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
			String identifier = catchVariable.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, catchVariable));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, catchVariable));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, catchVariable));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, catchVariable));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, catchVariable));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, catchVariable));
		}

		@Override
		public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
			String identifier = reference.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			String identifier = m.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, m));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, m));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, m));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, m));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, m));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, m));

		}

		@Override
		public <T> void visitCtAnnotationMethod(CtAnnotationMethod<T> annotationMethod) {
			String identifier = annotationMethod.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, annotationMethod));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, annotationMethod));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, annotationMethod));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, annotationMethod));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, annotationMethod));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, annotationMethod));

		}
		@NoIdentifier
		@Override
		public <T> void visitCtNewArray(CtNewArray<T> newArray) {
			// CtNewArray have no identifier => no check needed
		}

		@NoIdentifier
		@Override
		public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
			// CtConstructorCall have no identifier => no check needed
		}

		@NoIdentifier
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

		@NoIdentifier
		@Override
		public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
				CtExecutableReferenceExpression<T, E> expression) {
			// CtExecutableReferenceExpression have no identifier => no check needed
		}

		@NoIdentifier
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
				checkInvertedCondition(this::isJavaIdentifier, part, () -> createException(identifierError, ctPackage));

				checkCondition(this::isKeyword, part, () -> createException(keywordError, ctPackage));
				checkCondition(this::isTypeKeyword, part, () -> createException(keywordError, ctPackage));
				checkCondition(this::isNullLiteral, part, () -> createException(keywordError, ctPackage));
				checkCondition(this::isBooleanLiteral, part, () -> createException(keywordError, ctPackage));
				checkCondition(this::isClassLiteral, part, () -> createException(keywordError, ctPackage));
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
				checkInvertedCondition(this::isJavaIdentifier, part, () -> createException(identifierError, reference));

				checkCondition(this::isKeyword, part, () -> createException(keywordError, reference));
				checkCondition(this::isTypeKeyword, part, () -> createException(keywordError, reference));
				checkCondition(this::isNullLiteral, part, () -> createException(keywordError, reference));
				checkCondition(this::isBooleanLiteral, part, () -> createException(keywordError, reference));
				checkCondition(this::isClassLiteral, part, () -> createException(keywordError, reference));
			}
		}

		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			String identifier = parameter.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, parameter));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, parameter));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, parameter));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, parameter));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, parameter));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, parameter));
		}

		@Override
		public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
			String identifier = reference.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));

		}
		@NoIdentifier
		@Override
		public <R> void visitCtReturn(CtReturn<R> returnStatement) {
			// CtReturn have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <R> void visitCtStatementList(CtStatementList statements) {
			// CtStatementList have no identifier => no check needed

		}
		@NoIdentifier
		@Override
		public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
			// CtSwitch have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> switchExpression) {
			// CtSwitchExpression have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtSynchronized(CtSynchronized synchro) {
			// CtSynchronized have no identifier => no check needed

		}
		@NoIdentifier
		@Override
		public void visitCtThrow(CtThrow throwStatement) {
			// CtThrow have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtTry(CtTry tryBlock) {
			// CtTry have no identifier => no check needed

		}
		@NoIdentifier
		@Override
		public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
			// CtTryWithResource have no identifier => no check needed
		}

		@Override
		public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
			//TODO: can generic refer to a local type???
			// when dont check for wildcard here, because a generic Type cant refere to wildcard. class Foo<?> is not legal java.
			String identifier = ref.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, ref));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, ref));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, ref));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, ref));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, ref));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, ref));
		}

		@Override
		public void visitCtWildcardReference(CtWildcardReference wildcardReference) {
			String identifier = wildcardReference.getSimpleName();
			if (!strictMode || wildcardReference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (isWildcard(identifier)) {
				return;
			}
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, wildcardReference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, wildcardReference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, wildcardReference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, wildcardReference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, wildcardReference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, wildcardReference));
		}

		@Override
		public <T> void visitCtIntersectionTypeReference(CtIntersectionTypeReference<T> reference) {
			String identifier = reference.getSimpleName();
			// arrayTypeReferences have one or multiple [] at the end
			identifier = identifier.replaceAll(ARRAY_SUFFIX_REGEX, "");
			if (!strictMode || reference.isLocalType()) {
				// local types have a numeric prefix, we need to remove.
				identifier = convertLocalTypeIdentifier(identifier);
			}
			if (isWildcard(identifier)) {
				return;
			}
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, reference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, reference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, reference));
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
		@NoIdentifier
		@Override
		public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
			// CtTypeAccess have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
			// CtUnaryOperator have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
			// CtVariableRead have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
			// CtVariableWrite have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtWhile(CtWhile whileLoop) {
			// CtWhile have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
			// CtAnnotationFieldAccess have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
			// CtSuperAccess have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
			// CtFieldWrite have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
			// CtSuperAccess have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtComment(CtComment comment) {
			// CtComment have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtJavaDoc(CtJavaDoc comment) {
			// CtJavaDoc have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtJavaDocTag(CtJavaDocTag docTag) {
			//TODO: do they have limitations?
		}
		@NoIdentifier
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
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, module));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, module));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, module));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, module));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, module));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, module));
		}

		@Override
		public void visitCtModuleReference(CtModuleReference moduleReference) {
			String identifier = moduleReference.getSimpleName();
			checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, moduleReference));

			checkCondition(this::isKeyword, identifier, () -> createException(keywordError, moduleReference));
			checkCondition(this::isTypeKeyword, identifier, () -> createException(keywordError, moduleReference));
			checkCondition(this::isNullLiteral, identifier, () -> createException(keywordError, moduleReference));
			checkCondition(this::isBooleanLiteral, identifier, () -> createException(keywordError, moduleReference));
			checkCondition(this::isClassLiteral, identifier, () -> createException(keywordError, moduleReference));

		}
		@NoIdentifier
		@Override
		public void visitCtPackageExport(CtPackageExport moduleExport) {
			// CtPackageExport have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtModuleRequirement(CtModuleRequirement moduleRequirement) {
			// CtModuleRequirement have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtProvidedService(CtProvidedService moduleProvidedService) {
			// CtProvidedService have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtUsedService(CtUsedService usedService) {
			// CtUsedService have no identifier => no check needed
		}
		@NoIdentifier
		@Override
		public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
			// CtCompilationUnit have no identifier => no check needed
		}
		@NoIdentifier
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
				checkInvertedCondition(this::isJavaIdentifier, identifier, () -> createException(identifierError, wildcardReference));

				checkCondition(this::isKeyword, part, () -> createException(keywordError, wildcardReference));
				checkCondition(this::isTypeKeyword, part, () -> createException(keywordError, wildcardReference));
				checkCondition(this::isNullLiteral, part, () -> createException(keywordError, wildcardReference));
				checkCondition(this::isBooleanLiteral, part, () -> createException(keywordError, wildcardReference));
				checkCondition(this::isClassLiteral, part, () -> createException(keywordError, wildcardReference));
			}
			if (!identifierParts[identifierParts.length - 1].equals(ASTERISK_LITERAL)) {
				exception = createException(identifierError, wildcardReference);
				return;
			}
		}
		@NoIdentifier
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
		 * Checks if an identifier is the class keyword. As defined in jls 3.8 an identifier is never the class keyword.
		 * @param identifier to check
		 * @return true if the identifier is the class literal, false otherwise.
		 */
		private boolean isClassLiteral(String identifier) {
			return identifier.equals(CLASS_LITERAL);
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
		//TODO: Doc
		private <T> boolean isFullQName(CtFieldReference<T> reference) {
			String fqName = reference.getQualifiedName();
			// the last part is the identifier
			return fqName.split("#").length != 1;
		}
		//TODO: Doc
		private <T>  String removeFQName(CtFieldReference<T> reference) {
			String name = reference.getSimpleName();
			int index = name.lastIndexOf(".");
			return index >= 0 ? name.substring(index + 1) : name;
		}
		//TODO: doc
		private void checkCondition(Predicate<String> check, String identifier, Supplier<SpoonException> error) {
			if (check.test(identifier)) {
				throw error.get();
			}
		}
		//TODO: doc
		private void checkInvertedCondition(Predicate<String> check, String identifier, Supplier<SpoonException> error) {
			checkCondition(check.negate(), identifier, error);
		}

		/**
		 * Creates a formatted exception from input.
		 * @param rawOutput to format.
		 * @param element with identifier problem.
		 * @return a @link{spoon.SpoonException.SpoonException(String)}, with the reason.
		 */
		private SpoonException createException(String rawOutput, CtNamedElement element) {
			return new SpoonException(String.format(identifierError, element.getSimpleName(), element.getPosition()));
		}

		/**
		 * Creates a formatted exception from input.
		 * @param rawOutput to format.
		 * @param element with identifier problem.
		 * @return a @link{spoon.SpoonException.SpoonException(String)}, with the reason.
		 */
		private SpoonException createException(String rawOutput, CtReference element) {
			return new SpoonException(String.format(identifierError, element.getSimpleName(), element.getPosition()));
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

	private @interface SupportedIdentifiers {
		/** Element can have generic brackets <>*/
		boolean generics() default false;
		/** Element can have array ending as suffix in identifier */
		boolean arrays() default false;;
		/** Element identifier can be a FQ Name */
		boolean fqName() default false;;
		/** Element can be a localType and have a numeric prefix in identifier */
		boolean localType() default false;;
		/** Element identifier can be a wildCard '?' */
		boolean wildCard() default false;;
		/** Element identifier can be a type keyword */
		boolean typeKeyword() default false;;
		/** Element identifier can be class keyword */
		boolean classKeyword() default false;;
		/** Element identifier can be "null" */
		boolean nullLiteral() default false;;
		/** Element identifier can be boolean literal */
		boolean booleanLiteral() default false;;

	}
	/** Element has no checkable identifier, no rules or a static identifier */
	private @interface NoIdentifier {
	};
}
