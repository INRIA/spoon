package spoon.support.compiler.jdt;

import java.util.ArrayList;

import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.CtInheritanceScanner;

@SuppressWarnings("unchecked")
public class ParentExiter extends CtInheritanceScanner {

	private final JDTTreeBuilder jdtTreeBuilder;

	/**
	 * @param jdtTreeBuilder
	 */
	ParentExiter(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	CtElement child;

	@Override
	public void scanCtElement(CtElement e) {
		if (child instanceof CtAnnotation
				&& this.jdtTreeBuilder.context.annotationValueName.isEmpty()) {
			e.addAnnotation((CtAnnotation<?>) child);
			return;
		}
	}

	@Override
	public <R> void scanCtExecutable(CtExecutable<R> e) {
		if (child instanceof CtParameter) {
			e.addParameter((CtParameter<?>) child);
			return;
		} else if (child instanceof CtBlock) {
			e.setBody((CtBlock<R>) child);
			return;
		}
		super.scanCtExecutable(e);
	}

	@Override
	public void scanCtGenericElement(CtGenericElement e) {
		return;
	}

	@Override
	public void scanCtLoop(CtLoop loop) {
		if (loop.getBody() == null && child instanceof CtStatement) {
			loop.setBody((CtStatement) child);
		}
		super.scanCtLoop(loop);
	}

	@Override
	public <T> void scanCtSimpleType(CtSimpleType<T> t) {
		if (child instanceof CtSimpleType) {
			if (t.getNestedTypes().contains(child)) {
				t.getNestedTypes().remove(child);
			}
			t.addNestedType((CtSimpleType<?>) child);
			return;
		} else if (child instanceof CtField) {
			t.addField((CtField<?>) child);
			return;
		} else if (child instanceof CtConstructor) {
			return;
		}
		super.scanCtSimpleType(t);
	}

	@Override
	public <T, E extends CtExpression<?>> void scanCtTargetedExpression(
			CtTargetedExpression<T, E> targetedExpression) {
		if (!this.jdtTreeBuilder.context.target.isEmpty()
				&& this.jdtTreeBuilder.context.target.peek() == targetedExpression) {
			targetedExpression.setTarget((E) child);
			return;
		}
		super.scanCtTargetedExpression(targetedExpression);
	}

	@Override
	public <T> void scanCtType(CtType<T> type) {
		if (child instanceof CtMethod) {
			type.addMethod((CtMethod<?>) child);
			return;
		}
		super.scanCtType(type);
	}

	@Override
	public <T> void scanCtVariable(CtVariable<T> v) {
		if (child instanceof CtExpression && !this.jdtTreeBuilder.context.arguments.isEmpty()
				&& this.jdtTreeBuilder.context.arguments.peek() == v) {
			v.setDefaultExpression((CtExpression<T>) child);
			return;
		}
		super.scanCtVariable(v);
	}

	@Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(
			CtAnnotation<A> annotation) {

		// //GANYMEDE FIX: JDT now inserts a simpletyperef below annotations
		// that points
		// //to the type of the annotation. The JDTTreeBuilder supposes that
		// this simpletyperef is
		// //in fact part of a member/value pair, and will try to construct
		// the pair. I think it is safe to
		// // ignore this, but we should really migrate to the JDT Binding
		// API since this will only get worse
		// // Just to be safe I upcall the visitCtAnnotation in the
		// inheritance scanner.
		// if(context.annotationValueName.isEmpty()){
		// super.visitCtAnnotation(annotation);
		// return;
		// }

		String name = this.jdtTreeBuilder.context.annotationValueName.peek();
		Object value = child;

		if (value instanceof CtVariableAccess)
			value = ((CtVariableAccess<?>) value).getVariable();
		if (value instanceof CtFieldReference
				&& ((CtFieldReference<?>) value).getSimpleName().equals(
						"class")) {
			value = ((CtFieldReference<?>) value).getType();
		}
		annotation.getElementValues().put(name, value);
		super.visitCtAnnotation(annotation);
	}

	@Override
	public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
		if (child instanceof CtBlock) {
			e.setBody((CtBlock<?>) child);
			return;
		}
		super.visitCtAnonymousExecutable(e);
	}

	@Override
	public <T, E extends CtExpression<?>> void visitCtArrayAccess(
			CtArrayAccess<T, E> arrayAccess) {
		if (this.jdtTreeBuilder.context.arguments.size() > 0
				&& this.jdtTreeBuilder.context.arguments.peek() == arrayAccess) {
			arrayAccess.setIndexExpression((CtExpression<Integer>) child);
			return;
		} else if (arrayAccess.getTarget() == null) {
			arrayAccess.setTarget((E) child);
			return;
		}
		super.visitCtArrayAccess(arrayAccess);
	}

	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		if (child instanceof CtExpression)
			if (!this.jdtTreeBuilder.context.arguments.isEmpty()
					&& this.jdtTreeBuilder.context.arguments.peek() == asserted) {
				asserted.setExpression((CtExpression<T>) child);
				return;
			} else {
				asserted.setAssertExpression((CtExpression<Boolean>) child);
				return;
			}
		super.visitCtAssert(asserted);
	}

	@Override
	public <T, A extends T> void visitCtAssignment(
			CtAssignment<T, A> assignement) {
		if (assignement.getAssigned() == null) {
			assignement.setAssigned((CtExpression<T>) child);
			return;
		} else if (assignement.getAssignment() == null) {
			assignement.setAssignment((CtExpression<A>) child);
			return;
		}
		super.visitCtAssignment(assignement);
	}

	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		if (child instanceof CtExpression)
			if (operator.getLeftHandOperand() == null) {
				operator.setLeftHandOperand((CtExpression<?>) child);
				return;
			} else if (operator.getRightHandOperand() == null) {
				operator.setRightHandOperand((CtExpression<?>) child);
				return;
			}
		super.visitCtBinaryOperator(operator);
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		if (child instanceof CtStatement) {
			block.addStatement((CtStatement) child);
			return;
		}
		super.visitCtBlock(block);
	}

	@Override
	public <E> void visitCtCase(CtCase<E> caseStatement) {
		if (this.jdtTreeBuilder.context.selector && caseStatement.getCaseExpression() == null
				&& child instanceof CtExpression) {
			caseStatement.setCaseExpression((CtExpression<E>) child);
			return;
		} else if (child instanceof CtStatement) {
			caseStatement.addStatement((CtStatement) child);
			return;
		}
		super.visitCtCase(caseStatement);
	}

	@Override
	public void visitCtCatch(CtCatch catchBlock) {
		if (child instanceof CtBlock) {
			catchBlock.setBody((CtBlock<?>) child);
			return;
		} else if (child instanceof CtLocalVariable) {
			catchBlock
					.setParameter((CtLocalVariable<? extends Throwable>) child);
			return;
		}
		super.visitCtCatch(catchBlock);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		if (child instanceof CtConstructor) {
			CtConstructor<T> c = (CtConstructor<T>) child;
			ctClass.addConstructor(c);
			if (c.getPosition() != null
					&& c.getPosition().equals(ctClass.getPosition())) {
				c.setImplicit(true);
			}
		}
		if (child instanceof CtAnonymousExecutable) {
			ctClass.addAnonymousExecutable((CtAnonymousExecutable) child);
		}
		super.visitCtClass(ctClass);
	}

	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		if (child instanceof CtExpression) {
			if (conditional.getCondition() == null) {
				conditional.setCondition((CtExpression<Boolean>) child);
			} else if (conditional.getThenExpression() == null) {
				conditional.setThenExpression((CtExpression<T>) child);
			} else if (conditional.getElseExpression() == null) {
				conditional.setElseExpression((CtExpression<T>) child);
			}
		}
		super.visitCtConditional(conditional);
	}

	@Override
	public void visitCtDo(CtDo doLoop) {
		if (doLoop.getBody() != null && child instanceof CtExpression
				&& doLoop.getLoopingExpression() == null) {
			doLoop.setLoopingExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtDo(doLoop);
	}

	@Override
	public <T> void visitCtField(CtField<T> f) {
		if (f.getDefaultExpression() == null
				&& child instanceof CtExpression) {
			f.setDefaultExpression((CtExpression<T>) child);
			return;
		}
		super.visitCtField(f);
	}

	@Override
	public void visitCtFor(CtFor forLoop) {
		if (this.jdtTreeBuilder.context.forinit && child instanceof CtStatement) {
			forLoop.addForInit((CtStatement) child);
			return;
		}
		if (!this.jdtTreeBuilder.context.forupdate && forLoop.getExpression() == null
				&& child instanceof CtExpression) {
			forLoop.setExpression((CtExpression<Boolean>) child);
			return;
		}
		if (this.jdtTreeBuilder.context.forupdate && child instanceof CtStatement) {
			forLoop.addForUpdate((CtStatement) child);
			return;
		}
		super.visitCtFor(forLoop);
	}

	@Override
	public void visitCtForEach(CtForEach foreach) {
		if (foreach.getVariable() == null && child instanceof CtVariable) {
			foreach.setVariable((CtLocalVariable<?>) child);
			return;
		} else if (foreach.getExpression() == null
				&& child instanceof CtExpression) {
			foreach.setExpression((CtExpression<?>) child);
			return;
		} else if (child instanceof CtStatement) {
			foreach.setBody((CtStatement) child);
			return;
		}
		super.visitCtForEach(foreach);
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		if (ifElement.getCondition() == null
				&& child instanceof CtExpression) {
			ifElement.setCondition((CtExpression<Boolean>) child);
			return;
		} else if (child instanceof CtStatement) {
			if (ifElement.getThenStatement() == null) {
				ifElement.setThenStatement((CtStatement) child);
				return;
			} else if (ifElement.getElseStatement() == null) {
				ifElement.setElseStatement((CtStatement) child);
				return;
			}
		}
		super.visitCtIf(ifElement);
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		if (this.jdtTreeBuilder.context.isArgument(invocation) && child instanceof CtExpression) {
			invocation.addArgument((CtExpression<?>) child);
			return;
		} else if (child instanceof CtExpression) {
			invocation.setTarget((CtExpression<?>) child);
			return;
		}
		super.visitCtInvocation(invocation);
	}

	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		if (this.jdtTreeBuilder.context.isArgument(newArray)) {
			newArray.addDimensionExpression((CtExpression<Integer>) child);
			return;
		} else if (child instanceof CtExpression) {
			newArray.addElement((CtExpression<?>) child);
			return;
		}
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		if (this.jdtTreeBuilder.context.isArgument(newClass) && child instanceof CtExpression) {
			newClass.addArgument((CtExpression<?>) child);
			return;
		} else if (child instanceof CtClass) {
			newClass.setAnonymousClass((CtClass<?>) child);
			// this can be an interface but we don't know it so we set it to
			// the superclass
			((CtClass<?>) child).setSuperclass(newClass.getType());
			return;
		}
		super.visitCtNewClass(newClass);
	}

	@Override
	public void visitCtPackage(CtPackage ctPackage) {
		if (child instanceof CtSimpleType) {
			if (ctPackage.getTypes().contains(child)) {
				ctPackage.getTypes().remove(child);
			}
			ctPackage.getTypes().add((CtSimpleType<?>) child);
			this.jdtTreeBuilder.context.addCreatedType((CtSimpleType<?>) child);
			if (child.getPosition() != null
					&& child.getPosition().getCompilationUnit() != null) {
				child.getPosition().getCompilationUnit().getDeclaredTypes()
						.add((CtSimpleType<?>) child);
			}
			return;
		}
		super.visitCtPackage(ctPackage);
	}

	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		if (child instanceof CtExpression) {
			returnStatement.setReturnedExpression((CtExpression<R>) child);
			return;
		}
		super.visitCtReturn(returnStatement);
	}

	@Override
	public <E> void visitCtSwitch(CtSwitch<E> switchStatement) {
		if (switchStatement.getSelector() == null
				&& child instanceof CtExpression) {
			switchStatement.setSelector((CtExpression<E>) child);
			return;
		}
		if (child instanceof CtCase) {
			switchStatement.addCase((CtCase<E>) child);
			return;
		}
		super.visitCtSwitch(switchStatement);
	}

	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		if (synchro.getExpression() == null
				&& child instanceof CtExpression) {
			synchro.setExpression((CtExpression<?>) child);
			return;
		}
		if (synchro.getBlock() == null && child instanceof CtBlock) {
			synchro.setBlock((CtBlock<?>) child);
			return;
		}
		super.visitCtSynchronized(synchro);
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		if (throwStatement.getThrownExpression() == null) {
			throwStatement
					.setThrownExpression((CtExpression<? extends Throwable>) child);
			return;
		}
		super.visitCtThrow(throwStatement);
	}

	@Override
	public void visitCtTry(CtTry tryBlock) {
		if (child instanceof CtBlock) {
			if (!this.jdtTreeBuilder.context.finallyzer.isEmpty()
					&& this.jdtTreeBuilder.context.finallyzer.peek() == tryBlock)
				tryBlock.setFinalizer((CtBlock<?>) child);
			else
				tryBlock.setBody((CtBlock<?>) child);
			return;
		} else if (child instanceof CtLocalVariable) {
			if (tryBlock.getResources() == null) {
				tryBlock.setResources(new ArrayList<CtLocalVariable<? extends AutoCloseable>>());
			}
			tryBlock.addResource((CtLocalVariable<? extends AutoCloseable>) child);
		} else if (child instanceof CtCatch) {
			tryBlock.addCatcher((CtCatch) child);
			return;
		}
		super.visitCtTry(tryBlock);
	}

	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		if (operator.getOperand() == null && child instanceof CtExpression) {
			operator.setOperand((CtExpression<T>) child);
			return;
		}
		super.visitCtUnaryOperator(operator);
	}

	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		if (whileLoop.getLoopingExpression() == null
				&& child instanceof CtExpression) {
			whileLoop.setLoopingExpression((CtExpression<Boolean>) child);
			return;
		}
		super.visitCtWhile(whileLoop);
	}
}