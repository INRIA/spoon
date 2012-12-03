/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.lang.annotation.Annotation;
import java.util.List;

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
import spoon.reflect.code.CtFieldAccess;
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
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
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
import spoon.reflect.declaration.CtExecutable;
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
import spoon.reflect.visitor.CtVisitor;

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
		if (e != null)
			e.accept(this);
	}

	public void scan(CtReference e) {
		if (e != null)
			e.accept(this);
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
		write("@").write(annotation.getAnnotationType().getQualifiedName());
	}

	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		write("@interface ");
		write(annotationType.getQualifiedName());
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		scan(e.getBody());
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		scan(arrayAccess.getTarget());
		write("[");
		scan(arrayAccess.getIndexExpression());
		write("]");
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
		write("[]");
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
		if (breakStatement.getTargetLabel() != null)
			write(breakStatement.getTargetLabel());
	}

	public <E> void visitCtCase(CtCase<E> caseStatement) {
		write("case (");
		scan(caseStatement.getCaseExpression());
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
		if (!c.getParameters().isEmpty())
			clearLast();
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

	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		write(reference.getType().getQualifiedName());
		write(" ");
		write(reference.getDeclaringType().getQualifiedName());
		write(CtExecutable.EXECUTABLE_SEPARATOR);
		write(reference.getSimpleName());
		write("(");
		for (CtTypeReference<?> ref : reference.getParameterTypes()) {
			scan(ref);
			write(",");
		}
		if (!reference.getParameterTypes().isEmpty())
			clearLast();
		write(")");
	}

	public <T> void visitCtField(CtField<T> f) {
		scan(f.getType());
		write(" ").write(f.getSimpleName());
	}

	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		scan(fieldAccess.getTarget());
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		// TODO: Fix this null pointer catch
		try {
			write(reference.getType().getQualifiedName()).write(" ");
			write(reference.getDeclaringType().getQualifiedName());
			write(CtField.FIELD_SEPARATOR);
			write(reference.getSimpleName());
		} catch (NullPointerException npe) {
			System.err.println("Null Pointer Exception in SingnaturePrinter.visitCtFieldReference()");
		}
	}

	public void visitCtFor(CtFor forLoop) {
		write("for (");
		for (CtStatement s : forLoop.getForInit()) {
			scan(s);
			write(",");
		}
		if (!forLoop.getForInit().isEmpty())
			clearLast();
		write(";");
		scan(forLoop.getExpression());
		write(";");
		for (CtStatement s : forLoop.getForUpdate()) {
			scan(s);
			write(",");
		}
		if (!forLoop.getForUpdate().isEmpty())
			clearLast();
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
		scan(ifElement.getThenStatement());
		write(" else ");
		scan(ifElement.getElseStatement());
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		write("interface ");
		write(intrface.getQualifiedName());
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		scan(invocation.getTarget());
		write(".");
		scan(invocation.getExecutable());
		write("(");
		for (CtExpression<?> e : invocation.getArguments()) {
			scan(e);
			write(",");
		}
		if (!invocation.getArguments().isEmpty())
			clearLast();
		write(")");
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		if (literal.getValue() != null)
			write(literal.getValue().toString());
	}

	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		write(localVariable.getSimpleName());
	}

	public <T> void visitCtLocalVariableReference(
			CtLocalVariableReference<T> reference) {
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
		if (!m.getParameters().isEmpty())
			clearLast();
		write(")");
	}
	
	public void scan(List<CtTypeReference<?>> formalTypeParameters) {
		if(formalTypeParameters!=null && formalTypeParameters.size()>0){
			write("<");
			for (CtTypeReference<?> type : formalTypeParameters) {
				write(type.getQualifiedName());
				if(type instanceof CtTypeParameterReference){
					CtTypeParameterReference tmp = (CtTypeParameterReference)type;
					if(tmp.getBounds()!=null && tmp.getBounds().size()>0){
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
		if (!newArray.getElements().isEmpty())
			clearLast();
		write("}");
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		write("new ");
		scan(newClass.getExecutable());
		scan(newClass.getAnonymousClass());
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
		write(expression.getValue());
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		write(statement.getValue());
	}
	
	public <T, A extends T> void visitCtOperatorAssignement(
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
		write(reference.getSimpleName());
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		write("return ");
		scan(returnStatement.getReturnedExpression());
	}

	public <R> void visitCtStatementList(CtStatementList<R> statements) {
		for (CtStatement s : statements.getStatements()) {
			scan(s);
			write(";\n");
		}
	}

	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		write("switch(");
		scan(switchStatement.getSelector());
		write(")");
		for (CtCase<?> c : switchStatement.getCases())
			scan(c);
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

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		write("<");
		write(typeParameter.getName());
		write(">");
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		write(ref.getQualifiedName());
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		write(reference.getQualifiedName());
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		scan(operator.getOperand());
		write(operator.getKind().toString());
	}

	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		scan(variableAccess.getVariable());
	}

	public void visitCtWhile(CtWhile whileLoop) {
		write("while (");
		scan(whileLoop.getLoopingExpression());
		write(")");
		scan(whileLoop.getBody());
	}

}
