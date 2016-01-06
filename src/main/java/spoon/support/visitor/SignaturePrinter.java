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
package spoon.support.visitor;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayAccess;
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
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.visitor.CtVisitor;

import java.lang.annotation.Annotation;
import java.util.List;

public class SignaturePrinter implements CtVisitor {

	StringBuffer signature;

	public SignaturePrinter() {
		super();
		reset();
	}

	public String getSignature() {
		return signature.toString();
	}

	public void reset() {
		signature = new StringBuffer();
	}

	public void scan(CtElement e) {
		if (e != null) {
			e.accept(this);
		}
	}

	public void scan(CtReference e) {
		if (e != null) {
			e.accept(this);
		}
	}

	protected SignaturePrinter write(String value) {
		signature.append(value);
		return this;
	}

	private SignaturePrinter clearLast() {
		signature.deleteCharAt(signature.length() - 1);
		return this;
	}

	public <A extends Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {
		write("@");
		if (annotation.getAnnotationType() != null) {
			write(annotation.getAnnotationType().getQualifiedName());
		}
	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		write("@interface ");
		write(annotationType.getQualifiedName());
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		scan(e.getBody());
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		printCtArrayAccess(arrayAccess);
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		printCtArrayAccess(arrayRead);
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		printCtArrayAccess(arrayWrite);
	}

	public <T, E extends CtExpression<?>> void printCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		scan(arrayAccess.getTarget());
		write("[");
		scan(arrayAccess.getIndexExpression());
		write("]");
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
		write("[]");
	}

	@Override
	public <T> void visitCtImplicitArrayTypeReference(CtImplicitArrayTypeReference<T> reference) {
		visitCtArrayTypeReference(reference);
	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		signature.append("assert ");
		scan(asserted.getAssertExpression());
		signature.append(":");
		scan(asserted.getExpression());
	}

	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		for (CtTypeReference<?> ref : assignement.getTypeCasts()) {
			write("(");
			scan(ref);
			write(")");
		}
		write("(");
		scan(assignement.getAssigned());
		write(" = ");
		scan(assignement.getAssignment());
		write(")");
	}

	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		scan(operator.getLeftHandOperand());
		write(operator.getKind().toString());
		scan(operator.getRightHandOperand());
	}

	public <R> void visitCtBlock(CtBlock<R> block) {
		signature.append("{\n");
		for (CtStatement s : block.getStatements()) {
			scan(s);
			signature.append(";\n");
		}
		signature.append("}");
	}

	public void visitCtBreak(CtBreak breakStatement) {
		write("break ");
		if (breakStatement.getTargetLabel() != null) {
			write(breakStatement.getTargetLabel());
		}
	}

	public <E> void visitCtCase(CtCase<E> caseStatement) {
		write("case (");
		scan(caseStatement.getCaseExpression());
		for (CtStatement statement : caseStatement.getStatements()) {
			scan(statement);
		}
		write(")");
	}

	public void visitCtCatch(CtCatch catchBlock) {
		write("catch (");
		scan(catchBlock.getParameter().getType());
		write(")");
	}

	public <T> void visitCtClass(CtClass<T> ctClass) {
		write("class ").write(ctClass.getQualifiedName());
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		scan(conditional.getCondition());
		write("?");
		scan(conditional.getThenExpression());
		write(":");
		scan(conditional.getElseExpression());
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		write(c.getDeclaringType().getQualifiedName());
		write("(");
		for (CtParameter<?> p : c.getParameters()) {
			scan(p.getType());
			write(",");
		}
		if (!c.getParameters().isEmpty()) {
			clearLast();
		}
		write(")");
	}

	public void visitCtContinue(CtContinue continueStatement) {
		signature.append("continue ");
		scan(continueStatement.getLabelledStatement());
	}

	public void visitCtDo(CtDo doLoop) {
		write("do ");
		scan(doLoop.getBody());
		write(" while (");
		scan(doLoop.getLoopingExpression());
		write(")");
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		write("enum ").write(ctEnum.getQualifiedName());
	}

	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		scan(reference.getDeclaringType());

		write(CtExecutable.EXECUTABLE_SEPARATOR);
		if (reference.isConstructor()) {
			write(reference.getDeclaringType().getSimpleName());
		} else {
			write(reference.getSimpleName());
		}
		write("(");
		if (reference.getParameters().size() > 0) {
			for (CtTypeReference<?> param : reference.getParameters()) {
				if (param != null && !"null".equals(param.getSimpleName())) {
					scan(param);
				} else {
					write(CtExecutableReference.UNKNOWN_TYPE);
				}
				write(", ");
			}
			clearLast();
			clearLast();
		}
		write(")");
	}

	public <T> void visitCtField(CtField<T> f) {
		scan(f.getType());
		write(" ").write(f.getSimpleName());
	}

	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		write(thisAccess.getType().getQualifiedName() + ".this");
	}

	public <T> void visitCtAnnotationFieldAccess(
			CtAnnotationFieldAccess<T> annotationFieldAccess) {
		scan(annotationFieldAccess.getTarget());
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		if (reference.getType() != null) {
			write(reference.getType().getQualifiedName());
		} else {
			write("<no type>");
		}
		write(" ");
		if (reference.getDeclaringType() != null) {
			write(reference.getDeclaringType().getQualifiedName());
			write(CtField.FIELD_SEPARATOR);
		}
		write(reference.getSimpleName());
	}

	public void visitCtFor(CtFor forLoop) {
		write("for (");
		for (CtStatement s : forLoop.getForInit()) {
			scan(s);
			write(",");
		}
		if (!forLoop.getForInit().isEmpty()) {
			clearLast();
		}
		write(";");
		scan(forLoop.getExpression());
		write(";");
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
			write(",");
		}
		if (!forLoop.getForUpdate().isEmpty()) {
			clearLast();
		}
		write(")");
		scan(forLoop.getBody());
	}

	public void visitCtForEach(CtForEach foreach) {
		write("for (");
		scan(foreach.getVariable());
		write(":");
		scan(foreach.getExpression());
		write(")");
		scan(foreach.getBody());
	}

	public void visitCtIf(CtIf ifElement) {
		write("if (");
		scan(ifElement.getCondition());
		write(") then ");
		scan((CtStatement) ifElement.getThenStatement());
		write(" else ");
		scan((CtStatement) ifElement.getElseStatement());
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		write("interface ");
		write(intrface.getQualifiedName());
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		write("(");
		scan(invocation.getExecutable());
		write("(");
		for (int i = 0; i < invocation.getArguments().size(); i++) {
			CtExpression<?> arg_i = invocation.getArguments().get(i);
			scan(arg_i);
			if (i != (invocation.getArguments().size() - 1)) {
				write(",");
			}
		}
		write(")");
		write(")");
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		if (literal.getValue() != null) {
			write(literal.toString());
		} else {
			write("null");
		}
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		write(localVariable.getSimpleName());
	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
		write(reference.getType().getQualifiedName()).write(" ");
		write(reference.getSimpleName());

	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		write(catchVariable.getSimpleName());
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		scan(reference.getDeclaration());
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		if (!m.getFormalTypeParameters().isEmpty()) {
			scan(m.getFormalTypeParameters());
			write(" ");
		}
		scan(m.getType());
		write(" ");
		write(m.getSimpleName());
		write("(");
		for (CtParameter<?> p : m.getParameters()) {
			scan(p.getType());
			write(",");
		}
		if (!m.getParameters().isEmpty()) {
			clearLast();
		}
		write(")");
	}

	public void scan(List<CtTypeReference<?>> formalTypeParameters) {
		if (formalTypeParameters != null && formalTypeParameters.size() > 0) {
			write("<");
			for (CtTypeReference<?> type : formalTypeParameters) {
				write(type.getQualifiedName());
				if (type instanceof CtTypeParameterReference) {
					CtTypeParameterReference tmp = (CtTypeParameterReference) type;
					if (tmp.getBounds() != null && tmp.getBounds().size() > 0) {
						write(" extends ");
						for (CtTypeReference<?> tmp2 : tmp.getBounds()) {
							write(tmp2.getQualifiedName());
						}
						clearLast();
					}
				}
				write(",");
			}
			clearLast();
			write(">");
		}
	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		write("new ");
		scan(newArray.getType());
		for (CtExpression<?> c : newArray.getDimensionExpressions()) {
			write("[");
			scan(c);
			write("]");
		}
		write("{");
		for (CtExpression<?> e : newArray.getElements()) {
			scan(e);
			write(",");
		}
		if (!newArray.getElements().isEmpty()) {
			clearLast();
		}
		write("}");
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		write("new ");
		scan(ctConstructorCall.getExecutable());
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		write("new ");
		scan(newClass.getExecutable());
		scan(newClass.getAnonymousClass());
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		write("(");
		scan(lambda.getType());
		write(") (");
		if (!lambda.getParameters().isEmpty()) {
			for (CtParameter<?> parameter : lambda.getParameters()) {
				scan(parameter);
				write(",");
			}
			clearLast();
		}
		write(")");
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression<T, E> expression) {
		write(expression.toString());
	}

	public <T> void visitCtCodeSnippetExpression(
			CtCodeSnippetExpression<T> expression) {
		write(expression.getValue());
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		write(statement.getValue());
	}

	public <T, A extends T> void visitCtOperatorAssignment(
			CtOperatorAssignment<T, A> assignment) {
		scan(assignment.getAssigned());
		write(assignment.getKind().toString());
		scan(assignment.getAssignment());
	}

	public void visitCtPackage(CtPackage ctPackage) {
		write(ctPackage.getQualifiedName());
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		write(reference.getSimpleName());
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		write(parameter.getSimpleName());
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		write(reference.getType().getQualifiedName()).write(" ");
		write(reference.getSimpleName());
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		write("return ");
		scan(returnStatement.getReturnedExpression());
	}

	public <R> void visitCtStatementList(CtStatementList statements) {
		for (CtStatement s : statements.getStatements()) {
			scan(s);
			write(";\n");
		}
	}

	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		write("switch(");
		scan(switchStatement.getSelector());
		write(")");
		for (CtCase<?> c : switchStatement.getCases()) {
			scan(c);
		}
	}

	public void visitCtSynchronized(CtSynchronized synchro) {
		write("synchronized (");
		scan(synchro.getExpression());
		write(") ");
		scan(synchro.getBlock());
	}

	public void visitCtThrow(CtThrow throwStatement) {
		write("throw ");
		scan(throwStatement.getThrownExpression());
	}

	public void visitCtTry(CtTry tryBlock) {
		write("try {\n");
		scan(tryBlock.getBody());
		for (CtCatch c : tryBlock.getCatchers()) {
			scan(c);
		}
		scan(tryBlock.getFinalizer());
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		write("try (");
		for (CtLocalVariable<?> resource : tryWithResource.getResources()) {
			scan(resource);
		}
		write(") {\n");
		scan(tryWithResource.getBody());
		for (CtCatch c : tryWithResource.getCatchers()) {
			scan(c);
		}
		scan(tryWithResource.getFinalizer());
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		write("<");
		write(typeParameter.getSimpleName());
		write(">");
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		write(ref.getQualifiedName());
		if (ref.getBounds() != null && !ref.getBounds().isEmpty()) {
			if (ref.isUpper()) {
				write(" extends ");
			} else {
				write(" super ");
			}
			for (CtTypeReference<?> b : ref.getBounds()) {
				scan(b);
				write(", ");
			}
			clearLast();
			clearLast();
		}
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		write(reference.getQualifiedName());
	}

	@Override
	public void visitCtCircularTypeReference(CtCircularTypeReference reference) {
		visitCtTypeReference(reference);
	}

	@Override
	public <T> void visitCtImplicitTypeReference(CtImplicitTypeReference<T> reference) {
		visitCtTypeReference(reference);
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		scan(typeAccess.getAccessedType());
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		scan(operator.getOperand());
		write(operator.getKind().toString());
	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		scan(variableAccess.getVariable());
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		scan(variableRead.getVariable());
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		scan(variableWrite.getVariable());
	}

	public void visitCtWhile(CtWhile whileLoop) {
		write("while (");
		scan(whileLoop.getLoopingExpression());
		write(")");
		scan(whileLoop.getBody());
	}

	public <T> void visitCtUnboundVariableReference(
			CtUnboundVariableReference<T> reference) {
		write(reference.getSimpleName());
	}

	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> f) {
		scan(f.getVariable());
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		scan(fieldRead.getVariable());
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		scan(fieldWrite.getVariable());
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		write(f.getType().getQualifiedName() + ".super");
	}
}
