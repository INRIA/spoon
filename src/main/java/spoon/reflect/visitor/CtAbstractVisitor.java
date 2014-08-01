package spoon.reflect.visitor;

import java.lang.annotation.Annotation;
import java.util.Collection;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

public abstract class CtAbstractVisitor implements CtVisitor {
	/**
	 * Generically scans a collection of meta-model elements.
	 */
	public void scan(Collection<? extends CtElement> elements) {
		if (elements != null) {
			for (CtElement e : elements) {
				scan(e);
			}
		}
	}

	/**
	 * Generically scans a meta-model element.
	 */
	public void scan(CtElement element) {
		if (element != null)
			element.accept(this);
	}

	/**
	 * Generically scans a meta-model element reference.
	 */
	public void scan(CtReference reference) {
		if (reference != null)
			reference.accept(this);
	}

	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {

	}

	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {

	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {

	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {

	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable anonymousExec) {

	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {

	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {

	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {

	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {

	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {

	}

	public <R> void visitCtBlock(CtBlock<R> block) {

	}

	public void visitCtBreak(CtBreak breakStatement) {

	}

	public <S> void visitCtCase(CtCase<S> caseStatement) {

	}

	public void visitCtCatch(CtCatch catchBlock) {

	}

	public <T> void visitCtClass(CtClass<T> ctClass) {

	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {

	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {

	}

	public void visitCtContinue(CtContinue continueStatement) {

	}

	public void visitCtDo(CtDo doLoop) {

	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {

	}

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {

	}

	public <T> void visitCtField(CtField<T> f) {

	}

	public <T> void visitCtTargetedAccess(CtTargetedAccess<T> targetedAccess) {

	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {

	}

	public void visitCtFor(CtFor forLoop) {

	}

	public void visitCtForEach(CtForEach foreach) {

	}

	public void visitCtIf(CtIf ifElement) {

	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {

	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {

	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {

	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {

	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {

	}

	public <T> void visitCtMethod(CtMethod<T> m) {

	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {

	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {

	}

	public <T, A extends T> void visitCtOperatorAssignement(
			CtOperatorAssignment<T, A> assignment) {

	}

	public void visitCtPackage(CtPackage ctPackage) {

	}

	public void visitCtPackageReference(CtPackageReference reference) {

	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {

	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {

	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {

	}

	public <R> void visitCtStatementList(CtStatementList statements) {

	}

	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {

	}

	public void visitCtSynchronized(CtSynchronized synchro) {

	}

	public void visitCtThrow(CtThrow throwStatement) {

	}

	public void visitCtTry(CtTry tryBlock) {

	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {

	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {

	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {

	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {

	}

	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {

	}

	public void visitCtWhile(CtWhile whileLoop) {

	}

	public <T> void visitCtAnnotationFieldAccess(
			CtAnnotationFieldAccess<T> annotationFieldAccess) {

	}

}
