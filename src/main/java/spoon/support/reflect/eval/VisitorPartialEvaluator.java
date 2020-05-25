/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.eval;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.util.RtHelper;

import java.util.List;

/**
 *
 * Simplifies an AST by performing all operations that are statically known and changes the AST accordingly (eg "0+1" -&gt; "1")
 * This visitor implements a simple partial evaluator for the program
 * compile-time metamodel.
 */
public class VisitorPartialEvaluator extends CtScanner implements PartialEvaluator {

	boolean flowEnded = false;

	CtElement result;

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

	@Override
	protected void exit(CtElement e) {
		// if we go back to CtScanner, we discard the temp result
		result = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends CtElement> R evaluate(R element) {
		if (element == null) {
			return null;
		}
		element.accept(this);
		if (result != null) {
			CtElement r = result;
			//reset result, to not influence another evaluation.
			result = null;
			if (element.isParentInitialized()) {
				r.setParent(element.getParent());
			}
			return (R) r;
		}

		// otherwise nothing has been changed
		return (R) element.clone();
	}

	void setResult(CtElement element) {
		result = element;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		CtExpression<?> left = evaluate(operator.getLeftHandOperand());
		CtExpression<?> right = evaluate(operator.getRightHandOperand());
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
					res.setValue((Boolean) leftObject & (Boolean) rightObject);
				} else {
					res.setValue(((Number) leftObject).intValue() & ((Number) rightObject).intValue());
				}
				break;
			case BITOR:
				if (leftObject instanceof Boolean) {
					res.setValue((Boolean) leftObject | (Boolean) rightObject);
				} else {
					res.setValue(((Number) leftObject).intValue() | ((Number) rightObject).intValue());
				}
				break;
			case BITXOR:
				if (leftObject instanceof Boolean) {
					res.setValue((Boolean) leftObject ^ (Boolean) rightObject);
				} else {
					res.setValue(((Number) leftObject).intValue() ^ ((Number) rightObject).intValue());
				}
				break;
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}
			setResult(res);
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
				if ((o instanceof Boolean) && (Boolean) o) {
					res.setValue(true);
					setResult(res);
				}
				return;
			default:
				// TODO: other cases?
			}
		}
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		CtBlock<?> b = block.getFactory().Core().createBlock();
		for (CtStatement s : block.getStatements()) {
			CtElement res = evaluate(s);
			if (res != null) {
				if (res instanceof CtStatement) {
					b.addStatement((CtStatement) res);
				} else {
					//the context expects statement. We cannot simplify in this case
					b.addStatement(s.clone());
				}
			}
			// do not copy unreachable statements
			if (flowEnded) {
				break;
			}
		}
		setResult(b);
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		CtDo w = doLoop.clone();
		w.setLoopingExpression(evaluate(doLoop.getLoopingExpression()));
		w.setBody(evaluate(doLoop.getBody()));
		setResult(w);
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
		if ("length".equals(fieldAccess.getVariable().getSimpleName())) {
			CtExpression<?> target = fieldAccess.getTarget();
			if (target instanceof CtNewArray<?>) {
				CtNewArray<?> newArr = (CtNewArray<?>) target;
				CtLiteral<Number> literal = fieldAccess.getFactory().createLiteral(newArr.getElements().size());
				setResult(literal);
				return;
			}
		}
		String fieldName = fieldAccess.getVariable().getSimpleName();

		// accessing the field, even for shadow classes
		CtType<?> typeDeclaration = fieldAccess.getVariable()
				.getDeclaringType()
				.getTypeDeclaration();

		CtField<?> f;
		if (typeDeclaration != null) {
			f = typeDeclaration.getField(fieldName); // works for shadow fields also
		} else {
			f = fieldAccess.getVariable().getFieldDeclaration();
		}

		if ((f != null) && f.getModifiers().contains(ModifierKind.FINAL)
				// enum values have no meaningful default expression to be evaluated
				&& !fieldAccess.getVariable().getDeclaringType().isSubtypeOf(fieldAccess.getFactory().Type().ENUM)
				) {
			setResult(evaluate(f.getDefaultExpression()));
			return;
		}
		setResult(fieldAccess.clone());
	}

	@Override
	public <T> void visitCtAnnotationFieldAccess(CtAnnotationFieldAccess<T> annotationFieldAccess) {
		CtField<?> f = annotationFieldAccess.getVariable().getDeclaration();
		setResult(evaluate(f.getDefaultExpression()));
	}

	@Override
	public void visitCtFor(CtFor forLoop) {

		// Evaluate forInit
		List<CtStatement> lst = forLoop.getForInit();
		for (CtStatement s : lst) {
			CtStatement evaluateStatement = evaluate(s);
			if (evaluateStatement != null) {
				forLoop.addForInit(evaluateStatement);
			}
		}

		// Evaluate Expression
		forLoop.setExpression(evaluate(forLoop.getExpression()));

		// Evaluate forUpdate
		lst = forLoop.getForUpdate();
		for (CtStatement s : lst) {
			CtStatement evaluateStatement = evaluate(s);
			if (evaluateStatement != null) {
				forLoop.addForUpdate(evaluateStatement);
			}
		}

		setResult(forLoop.clone());
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		CtExpression<Boolean> r = evaluate(ifElement.getCondition());
		if (r instanceof CtLiteral) {
			CtLiteral<Boolean> l = (CtLiteral<Boolean>) r;
			if (l.getValue()) {
				setResult(evaluate(ifElement.getThenStatement()));
			} else {
				if (ifElement.getElseStatement() != null) {
					setResult(evaluate(ifElement.getElseStatement()));
				} else {
					setResult(ifElement.getFactory().Code().createComment("if removed", CtComment.CommentType.INLINE));
				}
			}
		} else {
			CtIf ifRes = ifElement.getFactory().Core().createIf();
			ifRes.setCondition(r);
			boolean thenEnded = false;
			boolean elseEnded = false;
			ifRes.setThenStatement((CtStatement) evaluate(ifElement.getThenStatement()));
			if (flowEnded) {
				thenEnded = true;
				flowEnded = false;
			}
			if (ifElement.getElseStatement() != null) {
				ifRes.setElseStatement((CtStatement) evaluate(ifElement.getElseStatement()));
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

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		CtInvocation<T> i = invocation.getFactory().Core().createInvocation();
		i.setExecutable(invocation.getExecutable());
		i.setTypeCasts(invocation.getTypeCasts());
		boolean constant = true;
		i.setTarget(evaluate(invocation.getTarget()));
		if ((i.getTarget() != null) && !(i.getTarget() instanceof CtLiteral)) {
			constant = false;
		}
		for (CtExpression<?> e : invocation.getArguments()) {
			CtExpression<?> re = evaluate(e);
			if (!(re instanceof CtLiteral)) {
				constant = false;
			}
			i.addArgument(re);
		}
		// do not partially evaluate super(...)
		if (i.getExecutable().getSimpleName().equals(CtExecutableReference.CONSTRUCTOR_NAME)) {
			setResult(i);
			return;
		}
		if (constant) {
			CtExecutable<?> executable = invocation.getExecutable().getDeclaration();
			CtType<?> aType = invocation.getParent(CtType.class);
			CtTypeReference<?> execDeclaringType = invocation.getExecutable().getDeclaringType();
			// try to inline partial evaluation results for local calls
			// (including superclasses)
			if (executable != null && aType != null && invocation.getType() != null && execDeclaringType != null
					&& execDeclaringType.isSubtypeOf(aType.getReference())) {
				CtBlock<?> b = evaluate(executable.getBody());
				flowEnded = false;
				CtStatement last = b.getStatements().get(b.getStatements().size() - 1);
				if ((last instanceof CtReturn)) {
					if (((CtReturn<?>) last).getReturnedExpression() instanceof CtLiteral) {
						setResult(((CtReturn<?>) last).getReturnedExpression());
						return;
					}
				}
			} else {
				// try to completely evaluate
				T r;
				try {
					r = RtHelper.invoke(i);
					if (isLiteralType(r)) {
						CtLiteral<T> l = invocation.getFactory().Core().createLiteral();
						l.setValue(r);
						setResult(l);
						return;
					}
				} catch (Exception e) {
				}
			}
		}
		setResult(i);
	}

	private boolean isLiteralType(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof String) {
			return true;
		}
		if (object instanceof Number) {
			return true;
		}
		if (object instanceof Character) {
			return true;
		}
		return object instanceof Class;
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		CtField<T> r = f.clone();
		r.setDefaultExpression(evaluate(f.getDefaultExpression()));
		setResult(r);
	}


	@Override
	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
		CtLocalVariable<T> r = localVariable.clone();
		r.setDefaultExpression(evaluate(localVariable.getDefaultExpression()));
		setResult(r);
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		CtCatchVariable<T> r = catchVariable.clone();
		r.setDefaultExpression(evaluate(catchVariable.getDefaultExpression()));
		setResult(r);
	}
	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		CtReturn<R> r = returnStatement.getFactory().Core().createReturn();
		r.setReturnedExpression(evaluate(returnStatement.getReturnedExpression()));
		setResult(r);
		flowEnded = true;
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		CtSynchronized s = synchro.clone();
		s.setBlock(evaluate(synchro.getBlock()));
		setResult(s);
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		CtThrow r = throwStatement.getFactory().Core().createThrow();
		r.setThrownExpression(evaluate(throwStatement.getThrownExpression()));
		setResult(r);
		flowEnded = true;
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		super.visitCtCatch(catchBlock);
		//the flowEnded = true set by throw in catch block does not stops flow of parent
		flowEnded = false;
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		CtExpression<?> operand = evaluate(operator.getOperand());
		if (operand instanceof CtLiteral) {
			Object object = ((CtLiteral<?>) operand).getValue();
			CtLiteral<Object> res = operator.getFactory().Core().createLiteral();
			switch (operator.getKind()) {
			case NOT:
				res.setValue(!(Boolean) object);
				break;
			case NEG:
				res.setValue(convert(operator.getType(),
					-1 * ((Number) object).longValue()));
				break;
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}
			setResult(res);
			return;
		}
		setResult(operator.clone());
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
			setResult(evaluate(v.getDefaultExpression()));
		} else {
			setResult(variableAccess.clone());
		}
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> variableAssignment) {
		CtAssignment<T, A> a = variableAssignment.clone();
		a.setAssignment(evaluate(a.getAssignment()));
		setResult(a);
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		CtWhile w = whileLoop.clone();
		w.setLoopingExpression(evaluate(whileLoop.getLoopingExpression()));
		// If lopping Expression always false
		if ((whileLoop.getLoopingExpression() instanceof CtLiteral) && !((CtLiteral<Boolean>) whileLoop
				.getLoopingExpression()).getValue()) {
			setResult(null);
			return;
		}
		w.setBody(evaluate(whileLoop.getBody()));
		setResult(w);
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		CtExpression<Boolean> r = evaluate(conditional.getCondition());
		if (r instanceof CtLiteral) {
			CtLiteral<Boolean> l = (CtLiteral<Boolean>) r;
			if (l.getValue()) {
				setResult(evaluate(conditional.getThenExpression()));
			} else {
				setResult(evaluate(conditional.getElseExpression()));
			}
		} else {
			CtConditional<T> ifRes = conditional.getFactory().Core().createConditional();
			ifRes.setCondition(r);
			ifRes.setThenExpression(evaluate(conditional.getThenExpression()));
			ifRes.setElseExpression(evaluate(conditional.getElseExpression()));
			setResult(ifRes);
		}
	}
}
