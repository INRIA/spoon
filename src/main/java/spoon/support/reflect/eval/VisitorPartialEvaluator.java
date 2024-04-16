/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.eval;

import spoon.SpoonException;
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
import spoon.reflect.visitor.OperatorHelper;
import spoon.support.util.RtHelper;

import java.util.ArrayList;
import java.util.Collections;
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

	static Number convert(CtTypeReference<?> type, Number n) {
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
		if ((type.getActualClass() == double.class) || (type.getActualClass() == Double.class)) {
			return n.doubleValue();
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T, R> CtLiteral<R> promoteLiteral(CtTypeReference<R> type, CtLiteral<T> literal) {
		CtLiteral result = literal.clone();
		result.setType(type.clone());

		// check if there is no need to cast
		if (literal.getType().unbox().equals(type.unbox())) {
			result.setValue(literal.getValue());
			return result;
		}

		// casting a primitive to a string:
		if (type.equals(type.getFactory().Type().createReference(String.class)) && literal.getType().isPrimitive()) {
			result.setValue(literal.getValue().toString());
			return result;
		}

		// It is not possible to cast an Integer to a Double directly, which is a problem.
		if (type.unbox().isPrimitive()) {
			// for instances of Number, one can use the convert method:
			if (literal.getValue() instanceof Number) {
				result.setValue(convert(type, (Number) literal.getValue()));
			} else {
				// primitive types that do not implement Number are:
				// boolean, char

				// NOTE: it does not make sense to cast a boolean to any other primitive type
				if (literal.getValue() instanceof Character) {
					result.setValue(convert(type, (int) ((char) literal.getValue())));
				}
			}
		} else {
			result.setValue(type.getActualClass().cast(literal.getValue()));
		}

		return result;
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		CtExpression<?> left = evaluate(operator.getLeftHandOperand());
		CtExpression<?> right = evaluate(operator.getRightHandOperand());

		if ((left instanceof CtLiteral) && (right instanceof CtLiteral)) {
			CtLiteral<?> leftLiteral = (CtLiteral<?>) left;
			CtLiteral<?> rightLiteral = (CtLiteral<?>) right;

			CtTypeReference<?> promotedType = OperatorHelper.getPromotedType(
				operator.getKind(),
				leftLiteral,
				rightLiteral
			).orElse(null);

			if (promotedType == null) {
				return;
			}

			leftLiteral = promoteLiteral(promotedType, leftLiteral);
			rightLiteral = promoteLiteral(promotedType, rightLiteral);
			Object leftObject = leftLiteral.getValue();
			Object rightObject = rightLiteral.getValue();

			Object value;
			switch (operator.getKind()) {
			case AND:
				value = (Boolean) leftObject && (Boolean) rightObject;
				break;
			case OR:
				value = (Boolean) leftObject || (Boolean) rightObject;
				break;
			case EQ:
				if (leftObject == null) {
					value = leftObject == rightObject;
				} else {
					value = leftObject.equals(rightObject);
				}
				break;
			case NE:
				if (leftObject == null) {
					value = leftObject != rightObject;
				} else {
					value = !leftObject.equals(rightObject);
				}
				break;
			case GE:
				value = ((Number) leftObject).doubleValue() >= ((Number) rightObject).doubleValue();
				break;
			case LE:
				value = ((Number) leftObject).doubleValue() <= ((Number) rightObject).doubleValue();
				break;
			case GT:
				value = ((Number) leftObject).doubleValue() > ((Number) rightObject).doubleValue();
				break;
			case LT:
				value = ((Number) leftObject).doubleValue() < ((Number) rightObject).doubleValue();
				break;
			case MINUS:
				value = convert(operator.getType(),
					((Number) leftObject).doubleValue() - ((Number) rightObject).doubleValue());
				break;
			case MUL:
				value = convert(operator.getType(),
					((Number) leftObject).doubleValue() * ((Number) rightObject).doubleValue());
				break;
			case DIV:
				try {
					// handle floating point division differently than integer division, because
					// dividing by 0 is not an error for floating point numbers.
					if (isFloatingType(operator.getType())) {
						value = convert(operator.getType(),
							((Number) leftObject).doubleValue() / ((Number) rightObject).doubleValue());
					} else {
						value = convert(operator.getType(),
							((Number) leftObject).longValue() / ((Number) rightObject).longValue());
					}
				} catch (ArithmeticException exception) {
					// division by 0
					throw new SpoonException(
						String.format(
							"Expression '%s' evaluates to '%s %s %s' which can not be evaluated",
							operator,
							leftObject,
							OperatorHelper.getOperatorText(operator.getKind()),
							rightObject
						),
						exception
					);
				}
				break;
			case PLUS:
				if ((leftObject instanceof String) || (rightObject instanceof String)) {
					value = "" + leftObject + rightObject;
				} else {
					value = convert(operator.getType(),
						((Number) leftObject).doubleValue() + ((Number) rightObject).doubleValue());
				}
				break;
			case MOD:
				value = convert(operator.getType(),
					((Number) leftObject).doubleValue() % ((Number) rightObject).doubleValue());
				break;
			case BITAND:
				if (leftObject instanceof Boolean) {
					value = (Boolean) leftObject && (Boolean) rightObject;
				} else {
					value = convert(operator.getType(),
						((Number) leftObject).longValue() & ((Number) rightObject).longValue());
				}
				break;
			case BITOR:
				if (leftObject instanceof Boolean) {
					value = (Boolean) leftObject || (Boolean) rightObject;
				} else {
					value = convert(operator.getType(),
						((Number) leftObject).longValue() | ((Number) rightObject).longValue());
				}
				break;
			case BITXOR:
				if (leftObject instanceof Boolean) {
					value = (Boolean) leftObject ^ (Boolean) rightObject;
				} else {
					value = convert(operator.getType(),
						((Number) leftObject).longValue() ^ ((Number) rightObject).longValue());
				}
				break;
			case SL:
				if (isIntegralType(leftObject) && isIntegralType(rightObject)) {
					long rightObjectValue = ((Number) rightObject).longValue();
					if (leftObject instanceof Long) {
						value = (long) leftObject << rightObjectValue;
					} else {
						value = ((Number) leftObject).intValue() << rightObjectValue;
					}
					break;
				}
				throw new RuntimeException(operator.getKind() + " is only supported for integral types on both sides");
			case SR:
				if (isIntegralType(leftObject) && isIntegralType(rightObject)) {
					long rightObjectValue = ((Number) rightObject).longValue();
					if (leftObject instanceof Long) {
						value = (long) leftObject >> rightObjectValue;
					} else {
						value = ((Number) leftObject).intValue() >> rightObjectValue;
					}
					break;
				}
				throw new RuntimeException(operator.getKind() + " is only supported for integral types on both sides");
			case USR:
				if (isIntegralType(leftObject) && isIntegralType(rightObject)) {
					long rightObjectValue = ((Number) rightObject).longValue();
					if (leftObject instanceof Long) {
						value = (long) leftObject >>> rightObjectValue;
					} else {
						value = ((Number) leftObject).intValue() >>> rightObjectValue;
					}
					break;
				}
				throw new RuntimeException(operator.getKind() + " is only supported for integral types on both sides");
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}

			CtLiteral<Object> res = operator.getFactory().createLiteral(value);
			// the type of the result should not change
			res.setType(operator.getType().clone());

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

			switch (operator.getKind()) {
			case AND:
				if ((Boolean) o) {
					setResult(expr);
				} else {
					setResult(operator.getFactory().createLiteral(false));
				}
				return;
			case OR:
				if ((Boolean) o) {
					setResult(operator.getFactory().createLiteral(true));
				} else {
					setResult(expr);
				}
				return;
			case BITOR:
				if ((o instanceof Boolean) && (Boolean) o) {
					setResult(operator.getFactory().createLiteral(true));
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
				CtLiteral<Class<?>> literal = fieldAccess.getFactory().createLiteral(actualClass);
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
				&& !fieldAccess.getVariable().getDeclaringType().isSubtypeOf(fieldAccess.getFactory().Type().createReference(Enum.class))
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
		for (CtStatement s : new ArrayList<>(lst)) { // copying the list to avoid ConcurrentModificationException
			CtStatement evaluateStatement = evaluate(s);
			if (evaluateStatement != null) {
				forLoop.addForInit(evaluateStatement);
			}
		}

		// Evaluate Expression
		forLoop.setExpression(evaluate(forLoop.getExpression()));

		// Evaluate forUpdate
		lst = forLoop.getForUpdate();
		for (CtStatement s : new ArrayList<>(lst)) { // copying the list to avoid ConcurrentModificationException
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
						CtLiteral<T> l = invocation.getFactory().createLiteral(r);
						setResult(l);
						return;
					}
				} catch (Exception e) {
				}
			}
		}
		setResult(i);
	}

	private boolean isIntegralType(Object object) {
		// see https://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.2.1
		return object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Character;
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
		if (object instanceof Boolean) {
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
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> void visitCtLiteral(CtLiteral<T> ctLiteral) {
		CtLiteral result = ctLiteral.clone();

		List<CtTypeReference<?>> casts = new ArrayList<>(ctLiteral.getTypeCasts());
		Collections.reverse(casts);
		result.setTypeCasts(new ArrayList<>());

		for (CtTypeReference<?> cast : casts) {
			result = promoteLiteral(cast, result);
		}

		setResult(result);
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
			CtLiteral<?> literal = (CtLiteral<?>) operand;
			CtTypeReference<?> promotedType = OperatorHelper.getPromotedType(operator.getKind(), literal)
				.orElse(null);

			if (promotedType == null) {
				return;
			}

			literal = promoteLiteral(promotedType, literal);
			Object object = literal.getValue();
			Object value;
			switch (operator.getKind()) {
			case NOT:
				value = !(Boolean) object;
				break;
			case NEG:
				if (isFloatingType(operator.getType())) {
					value = convert(operator.getType(), -1 * ((Number) object).doubleValue());
				} else {
					value = convert(operator.getType(), -1 * ((Number) object).longValue());
				}
				break;
			case POS:
				if (isFloatingType(literal.getType())) {
					value = convert(operator.getType(), +((Number) object).doubleValue());
				} else {
					value = convert(operator.getType(), +((Number) object).longValue());
				}
				break;
			case COMPL:
				if (!isIntegralType(object)) {
					return;
				}

				value = convert(operator.getType(), ~((Number) object).longValue());
				break;
			default:
				throw new RuntimeException("unsupported operator " + operator.getKind());
			}
			CtLiteral<Object> res = operator.getFactory().createLiteral(value);
			setResult(res);
			return;
		}
		setResult(operator.clone());
	}

	/**
	 * Checks if the given type reference is a floating type. This includes primitive and their wrapper types.
	 * A type is considered floating if it is either a double or a float.
	 * @param type the type reference to check
	 * @return true if the type is a floating type, false otherwise. If the type is null, false is returned.
	 */
	private boolean isFloatingType(CtTypeReference<?> type) {
		if (type == null) {
			return false;
		}
		return type.equals(type.getFactory().Type().doublePrimitiveType())
				|| type.equals(type.getFactory().Type().floatPrimitiveType())
				|| type.equals(type.getFactory().Type().doubleType())
				|| type.equals(type.getFactory().Type().floatType());
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
