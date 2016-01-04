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
package spoon.support.reflect.eval;

import spoon.reflect.code.BinaryOperatorKind;
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
import spoon.reflect.code.CtCodeElement;
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
import spoon.reflect.code.CtTargetedExpression;
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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.internal.CtImplicitArrayTypeReference;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtGenericElementReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtUnboundVariableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.RtHelper;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This visitor implements a simple partial evaluator for the program
 * compile-time metamodel.
 */
public class VisitorPartialEvaluator implements CtVisitor, PartialEvaluator {

	boolean flowEnded = false;

	CtCodeElement result;

	Number convert(CtTypeReference<?> type, Number n) {
		if ((type.getActualClass() == int.class) || (type.getActualClass() == Integer.class)) {
			return n.intValue();
		}
		if ((type.getActualClass() == byte.class) || (type.getActualClass() == Byte.class)) {
			return n.byteValue();
		}
		if ((type.getActualClass() == long.class) || (type.getActualClass() == Long.class)) {
			return n.longValue();
		}
		if ((type.getActualClass() == float.class) || (type.getActualClass() == Float.class)) {
			return n.floatValue();
		}
		if ((type.getActualClass() == short.class) || (type.getActualClass() == Short.class)) {
			return n.shortValue();
		}
		return n;
	}

	@SuppressWarnings("unchecked")
	public <R extends CtCodeElement> R evaluate(CtElement parent, R element) {
		if (element == null) {
			return null;
		}
		element.accept(this);
		if (result != null) {
			result.setParent(parent);
		}
		return (R) result;
	}

	void setResult(CtCodeElement element) {
		result = element;
	}

	public <T> void visitCtCodeSnippetExpression(CtCodeSnippetExpression<T> expression) {
	}

	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
	}

	public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> annotation) {
		throw new RuntimeException("Unknow Element");
	}

	public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> annotationType) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtAnonymousExecutable(CtAnonymousExecutable impl) {
		throw new RuntimeException("Unknow Element");
	}

	public <T, E extends CtExpression<?>> void visitCtArrayAccess(CtArrayAccess<T, E> arrayAccess) {
		setResult(arrayAccess.getFactory().Core().clone(arrayAccess));
	}

	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		setResult(arrayRead.getFactory().Core().clone(arrayRead));
	}

	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		setResult(arrayWrite.getFactory().Core().clone(arrayWrite));
	}

	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtImplicitArrayTypeReference(CtImplicitArrayTypeReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtAssert(CtAssert<T> asserted) {
		throw new RuntimeException("Unknow Element");
	}

	@SuppressWarnings("unchecked")
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		CtExpression<?> left = evaluate(operator, operator.getLeftHandOperand());
		CtExpression<?> right = evaluate(operator, operator.getRightHandOperand());
		if ((left instanceof CtLiteral) && (right instanceof CtLiteral)) {
			Object leftObject = ((CtLiteral<?>) left).getValue();
			Object rightObject = ((CtLiteral<?>) right).getValue();
			CtLiteral<Object> res = operator.getFactory().Core().createLiteral();
			switch (operator.getKind()) {
			case AND:
				res.setValue((Boolean) leftObject && (Boolean) rightObject);
				break;
			case OR:
				res.setValue((Boolean) leftObject || (Boolean) rightObject);
				break;
			case EQ:
				if (leftObject == null) {
					res.setValue(leftObject == rightObject);
				} else {
					res.setValue(leftObject.equals(rightObject));
				}
				break;
			case NE:
				if (leftObject == null) {
					res.setValue(leftObject != rightObject);
				} else {
					res.setValue(!leftObject.equals(rightObject));
				}
				break;
			case GE:
				res.setValue(((Number) leftObject).doubleValue() >= ((Number) rightObject).doubleValue());
				break;
			case LE:
				res.setValue(((Number) leftObject).doubleValue() <= ((Number) rightObject).doubleValue());
				break;
			case GT:
				res.setValue(((Number) leftObject).doubleValue() > ((Number) rightObject).doubleValue());
				break;
			case LT:
				res.setValue(((Number) leftObject).doubleValue() < ((Number) rightObject).doubleValue());
				break;
			case MINUS:
				res.setValue(convert(operator.getType(),
								((Number) leftObject).doubleValue() - ((Number) rightObject).doubleValue()));
				break;
			case MUL:
				res.setValue(convert(operator.getType(),
								((Number) leftObject).doubleValue() * ((Number) rightObject).doubleValue()));
				break;
			case DIV:
				res.setValue(convert(operator.getType(),
								((Number) leftObject).doubleValue() / ((Number) rightObject).doubleValue()));
				break;
			case PLUS:
				if ((leftObject instanceof String) || (rightObject instanceof String)) {
					res.setValue("" + leftObject + rightObject);
				} else {
					res.setValue(convert(operator.getType(),
									((Number) leftObject).doubleValue() + ((Number) rightObject).doubleValue()));
				}
				break;
			case BITAND:
				if (leftObject instanceof Boolean) {
					res.setValue(((Boolean) leftObject).booleanValue() & ((Boolean) rightObject).booleanValue());
				} else {
					res.setValue(((Number) leftObject).intValue() & ((Number) rightObject).intValue());
				}
				break;
			case BITOR:
				if (leftObject instanceof Boolean) {
					res.setValue(((Boolean) leftObject).booleanValue() | ((Boolean) rightObject).booleanValue());
				} else {
					res.setValue(((Number) leftObject).intValue() | ((Number) rightObject).intValue());
				}
				break;
			case BITXOR:
				if (leftObject instanceof Boolean) {
					res.setValue(((Boolean) leftObject).booleanValue() ^ ((Boolean) rightObject).booleanValue());
				} else {
					res.setValue(((Number) leftObject).intValue() ^ ((Number) rightObject).intValue());
				}
				break;
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}
			setResult(res);
			return;
		} else if (operator.getKind() == BinaryOperatorKind.INSTANCEOF) {
			CtLiteral<Boolean> res = operator.getFactory().Core().createLiteral();
			CtTypeReference<?> leftType = ((CtTypedElement<?>) left).getType().box();
			CtTypeReference<?> rightType = ((CtLiteral<CtTypeReference<?>>) right).getValue();
			if (leftType.isSubtypeOf(rightType)) {
				res.setValue(true);
				setResult(res);
			}
			return;
		} else if ((left instanceof CtLiteral) || (right instanceof CtLiteral)) {
			CtLiteral<?> literal;
			CtExpression<?> expr;
			if (left instanceof CtLiteral) {
				literal = (CtLiteral<?>) left;
				expr = right;
			} else {
				literal = (CtLiteral<?>) right;
				expr = left;
			}
			Object o = literal.getValue();
			CtLiteral<Object> res = operator.getFactory().Core().createLiteral();
			switch (operator.getKind()) {
			case AND:
				if ((Boolean) o) {
					setResult(expr);
				} else {
					res.setValue(false);
					setResult(res);
				}
				return;
			case OR:
				if ((Boolean) o) {
					res.setValue(true);
					setResult(res);
				} else {
					setResult(expr);
				}
				return;
			case BITOR:
				if ((o instanceof Boolean) && ((Boolean) o).booleanValue()) {
					res.setValue(true);
					setResult(res);
				}
				return;
			default:
				// TODO: other cases?
			}
		}
		CtBinaryOperator<T> op = operator.getFactory().Core().createBinaryOperator();
		op.setKind(operator.getKind());
		op.setLeftHandOperand(left);
		op.setRightHandOperand(right);
		op.setType(operator.getType());
		setResult(op);
	}

	public <R> void visitCtBlock(CtBlock<R> block) {
		CtBlock<?> b = block.getFactory().Core().createBlock();
		for (CtStatement s : block.getStatements()) {
			CtStatement res = evaluate(b, s);
			if (res != null) {
				b.addStatement(res);
			}
			// do not copy unreachable statements
			if (flowEnded) {
				break;
			}
		}
		if ((b.getStatements().size() == 1) && (b.getStatements().get(0) instanceof CtBlock)) {
			setResult(b.getStatements().get(0));
		} else {
			setResult(b);
		}
	}

	public void visitCtBreak(CtBreak breakStatement) {
		setResult(breakStatement.getFactory().Core().clone(breakStatement));
	}

	public <E> void visitCtCase(CtCase<E> caseStatement) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtCatch(CtCatch catchBlock) {
		setResult(catchBlock.getFactory().Core().clone(catchBlock));
	}

	public <T> void visitCtClass(CtClass<T> ctClass) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtConstructor(CtConstructor<T> c) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtContinue(CtContinue continueStatement) {
		setResult(continueStatement.getFactory().Core().clone(continueStatement));
	}

	public void visitCtDo(CtDo doLoop) {
		CtDo w = doLoop.getFactory().Core().clone(doLoop);
		w.setLoopingExpression(evaluate(w, doLoop.getLoopingExpression()));
		w.setBody(evaluate(w, doLoop.getBody()));
		setResult(w);
	}

	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtExpression(CtExpression<?> expression) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtField(CtField<T> f) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtFieldAccess(CtFieldAccess<T> fieldAccess) {
		visitFieldAccess(fieldAccess);
	}

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		visitFieldAccess(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		visitFieldAccess(fieldWrite);
	}

	private <T> void visitFieldAccess(CtFieldAccess<T> fieldAccess) {
		if ("class".equals(fieldAccess.getVariable().getSimpleName())) {
			Class<?> actualClass = fieldAccess.getVariable().getDeclaringType().getActualClass();
			if (actualClass != null) {
				CtLiteral<Class<?>> literal = fieldAccess.getFactory().Core().createLiteral();
				literal.setValue(actualClass);
				setResult(literal);
				return;
			}
		}
		if (fieldAccess.getFactory().Type().createReference(Enum.class)
				.isAssignableFrom(fieldAccess.getVariable().getDeclaringType())) {
			CtLiteral<CtFieldReference<?>> l = fieldAccess.getFactory().Core().createLiteral();
			l.setValue(fieldAccess.getVariable());
			setResult(l);
			return;
		}
		CtField<?> f = fieldAccess.getVariable().getDeclaration();
		if ((f != null) && f.getModifiers().contains(ModifierKind.FINAL)) {
			setResult(evaluate(f, f.getDefaultExpression()));
			return;
		}
		setResult(fieldAccess.getFactory().Core().clone(fieldAccess));
	}

	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		setResult(thisAccess.getFactory().Core().clone(thisAccess));
	}

	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		CtField<?> f = annotationFieldAccess.getVariable().getDeclaration();
		setResult(evaluate(f, f.getDefaultExpression()));
	}

	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtFor(CtFor forLoop) {

		// Evaluate forInit
		List<CtStatement> lst = forLoop.getForInit();
		for (CtStatement s : lst) {
			CtStatement evaluateStatement = evaluate(forLoop, s);
			if (evaluateStatement != null) {
				forLoop.addForInit(evaluateStatement);
			}
		}

		// Evaluate Expression
		forLoop.setExpression(evaluate(forLoop, forLoop.getExpression()));

		// Evaluate forUpdate
		lst = forLoop.getForUpdate();
		for (CtStatement s : lst) {
			CtStatement evaluateStatement = evaluate(forLoop, s);
			if (evaluateStatement != null) {
				forLoop.addForUpdate(evaluateStatement);
			}
		}

		setResult(forLoop.getFactory().Core().clone(forLoop));
	}

	public void visitCtForEach(CtForEach foreach) {
		setResult(foreach.getFactory().Core().clone(foreach));
	}

	public void visitCtGenericElementReference(CtGenericElementReference reference) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtIf(CtIf ifElement) {
		CtExpression<Boolean> r = evaluate(ifElement, ifElement.getCondition());
		if (r instanceof CtLiteral) {
			CtLiteral<Boolean> l = (CtLiteral<Boolean>) r;
			if (l.getValue()) {
				setResult(evaluate(null, ifElement.getThenStatement()));
			} else {
				setResult(evaluate(null, ifElement.getElseStatement()));
			}
		} else {
			CtIf ifRes = ifElement.getFactory().Core().createIf();
			ifRes.setCondition(r);
			boolean thenEnded = false, elseEnded = false;
			ifRes.setThenStatement((CtStatement) evaluate(ifRes, ifElement.getThenStatement()));
			if (flowEnded) {
				thenEnded = true;
				flowEnded = false;
			}
			if (ifElement.getElseStatement() != null) {
				ifRes.setElseStatement((CtStatement) evaluate(ifRes, ifElement.getElseStatement()));
			}
			if (flowEnded) {
				elseEnded = true;
				flowEnded = false;
			}
			setResult(ifRes);
			if (thenEnded && elseEnded) {
				flowEnded = true;
			}
		}
	}

	public <T> void visitCtInterface(CtInterface<T> intrface) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		CtInvocation<T> i = invocation.getFactory().Core().createInvocation();
		i.setExecutable(invocation.getExecutable());
		boolean constant = true;
		i.setTarget(evaluate(i, invocation.getTarget()));
		if ((i.getTarget() != null) && !(i.getTarget() instanceof CtLiteral)) {
			constant = false;
		}
		for (CtExpression<?> e : invocation.getArguments()) {
			CtExpression<?> re = evaluate(i, e);
			if (!(re instanceof CtLiteral)) {
				constant = false;
			}
			i.addArgument(re);
		}
		// do not partially evaluate super(...)
		if (i.getExecutable().getSimpleName().equals("<init>")) {
			setResult(i);
			return;
		}
		if (constant) {
			CtExecutable<?> executable = invocation.getExecutable().getDeclaration();
			// try to inline partial evaluation results for local calls
			// (including to superclasses)
			if ((executable != null) && (invocation.getType() != null) && invocation.getExecutable().getDeclaringType()
					.isAssignableFrom(((CtType<?>) invocation.getParent(CtType.class)).getReference())) {
				CtBlock<?> b = evaluate(invocation.getParent(), executable.getBody());
				flowEnded = false;
				CtStatement last = b.getStatements().get(b.getStatements().size() - 1);
				if ((last != null) && (last instanceof CtReturn)) {
					if (((CtReturn<?>) last).getReturnedExpression() instanceof CtLiteral) {
						setResult(((CtReturn<?>) last).getReturnedExpression());
						return;
					}
				}
			} else {
				// try to completely evaluate
				T r = null;
				try {
					// System.err.println("invocking "+i);
					r = RtHelper.invoke(i);
					CtLiteral<T> l = invocation.getFactory().Core().createLiteral();
					l.setValue(r);
					setResult(l);
					return;
				} catch (Exception e) {
				}
			}
		}
		setResult(i);
	}

	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		setResult(literal.getFactory().Core().clone(literal));
	}

	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
		// if (localVariable.getParent(CtBlock.class)!=null) {
		//
		// List<CtVariableAccess> res = Query.getElements(localVariable
		// .getParent(CtBlock.class), new TypeFilter<CtVariableAccess>(
		// CtVariableAccess.class) {
		// @Override
		// public boolean matches(CtVariableAccess element) {
		// boolean ret = super.matches(element)
		// && element.getVariable().getSimpleName().equals(
		// localVariable.getSimpleName());
		// return ret;
		// }
		// });
		// if (res.size() != 0)
		CtLocalVariable<T> r = localVariable.getFactory().Core().clone(localVariable);
		r.setDefaultExpression(evaluate(r, localVariable.getDefaultExpression()));
		setResult(r);
	}

	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		CtCatchVariable<T> r = catchVariable.getFactory().Core().clone(catchVariable);
		r.setDefaultExpression(evaluate(r, catchVariable.getDefaultExpression()));
		setResult(r);
	}

	@Override
	public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtMethod(CtMethod<T> m) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		setResult(newArray.getFactory().Core().clone(newArray));
	}

	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		setResult(ctConstructorCall.getFactory().Core().clone(ctConstructorCall));
	}

	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		setResult(newClass.getFactory().Core().clone(newClass));
	}

	@Override
	public <T> void visitCtLambda(CtLambda<T> lambda) {
		setResult(lambda.getFactory().Core().clone(lambda));
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtExecutableReferenceExpression(
			CtExecutableReferenceExpression<T, E> expression) {
		setResult(expression.getFactory().Core().clone(expression));
	}

	public <T, A extends T> void visitCtOperatorAssignment(CtOperatorAssignment<T, A> assignment) {
		setResult(assignment.getFactory().Core().clone(assignment));
	}

	public void visitCtPackage(CtPackage ctPackage) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtPackageReference(CtPackageReference reference) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtParameter(CtParameter<T> parameter) {
		throw new RuntimeException("Unknow Element");
	}

	public <R> void visitCtStatementList(CtStatementList statements) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtReference(CtReference reference) {
		throw new RuntimeException("Unknow Element");
	}

	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		CtReturn<R> r = returnStatement.getFactory().Core().createReturn();
		r.setReturnedExpression(evaluate(r, returnStatement.getReturnedExpression()));
		setResult(r);
		flowEnded = true;
	}

	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		setResult(switchStatement.getFactory().Core().clone(switchStatement));
	}

	public void visitCtSynchronized(CtSynchronized synchro) {
		CtSynchronized s = synchro.getFactory().Core().clone(synchro);
		s.setBlock(evaluate(s, synchro.getBlock()));
		setResult(s);
	}

	public <T, E extends CtExpression<?>> void visitCtTargetedExpression(
			CtTargetedExpression<T, E> targetedExpression) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtThrow(CtThrow throwStatement) {
		CtThrow r = throwStatement.getFactory().Core().createThrow();
		r.setThrownExpression(evaluate(r, throwStatement.getThrownExpression()));
		setResult(r);
		flowEnded = true;
	}

	public void visitCtTry(CtTry tryBlock) {
		setResult(tryBlock.getFactory().Core().clone(tryBlock));
	}

	@Override
	public void visitCtTryWithResource(CtTryWithResource tryWithResource) {
		visitCtTry(tryWithResource);
	}

	public void visitCtTypeParameter(CtTypeParameter typeParameter) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		throw new RuntimeException("Unknow Element");
	}

	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public void visitCtCircularTypeReference(CtCircularTypeReference reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtImplicitTypeReference(CtImplicitTypeReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
		throw new RuntimeException("Unknown Element");
	}

	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		CtExpression<?> operand = evaluate(operator, operator.getOperand());
		if (operand instanceof CtLiteral) {
			Object object = ((CtLiteral<?>) operand).getValue();
			CtLiteral<Object> res = operator.getFactory().Core().createLiteral();
			switch (operator.getKind()) {
			case NOT:
				res.setValue(!(Boolean) object);
				break;
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}
			setResult(res);
			return;
		}
		setResult(operator.getFactory().Core().clone(operator));
	}

	@Override
	public <T> void visitCtVariableAccess(CtVariableAccess<T> variableAccess) {
		visitVariableAccess(variableAccess);
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		visitVariableAccess(variableRead);
	}

	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		visitVariableAccess(variableWrite);
	}

	private <T> void visitVariableAccess(CtVariableAccess<T> variableAccess) {
		CtVariable<?> v = variableAccess.getVariable().getDeclaration();
		if (v != null && v.hasModifier(ModifierKind.FINAL) && v.getDefaultExpression() != null) {
			setResult(evaluate(v, v.getDefaultExpression()));
		} else {
			setResult(variableAccess.getFactory().Core().clone(variableAccess));
		}
	}

	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> variableAssignment) {
		CtAssignment<T, A> a = variableAssignment.getFactory().Core().clone(variableAssignment);
		a.setAssignment(evaluate(a, a.getAssignment()));
		setResult(a);
	}

	public <T> void visitCtVariableReference(CtVariableReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	public void visitCtWhile(CtWhile whileLoop) {
		CtWhile w = whileLoop.getFactory().Core().clone(whileLoop);
		w.setLoopingExpression(evaluate(w, whileLoop.getLoopingExpression()));
		// If lopping Expression always false
		if ((whileLoop.getLoopingExpression() instanceof CtLiteral) && !((CtLiteral<Boolean>) whileLoop
				.getLoopingExpression()).getValue()) {
			setResult(null);
			return;
		}
		w.setBody(evaluate(w, whileLoop.getBody()));
		setResult(w);
	}

	public <T> void visitCtConditional(CtConditional<T> conditional) {
		CtExpression<Boolean> r = evaluate(conditional, conditional.getCondition());
		if (r instanceof CtLiteral) {
			CtLiteral<Boolean> l = (CtLiteral<Boolean>) r;
			if (l.getValue()) {
				setResult(evaluate(null, conditional.getThenExpression()));
			} else {
				setResult(evaluate(null, conditional.getElseExpression()));
			}
		} else {
			CtConditional<T> ifRes = conditional.getFactory().Core().createConditional();
			ifRes.setCondition(r);
			ifRes.setThenExpression(evaluate(ifRes, conditional.getThenExpression()));
			ifRes.setElseExpression(evaluate(ifRes, conditional.getElseExpression()));
			setResult(ifRes);
		}
	}

	public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
		throw new RuntimeException("Unknow Element");
	}

	@Override
	public <T> void visitCtSuperAccess(CtSuperAccess<T> f) {
		setResult(f.getFactory().Core().clone(f));
	}
}
