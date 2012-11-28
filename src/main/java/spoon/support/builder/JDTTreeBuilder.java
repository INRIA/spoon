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

package spoon.support.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

import spoon.reflect.CoreFactory;
import spoon.reflect.Factory;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
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
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
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
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.template.Template;

/**
 * A visitor for iterating through the parse tree.
 */
public class JDTTreeBuilder extends ASTVisitor {

	public class ASTPair {
		public CtElement element;

		public ASTNode node;

		public ASTPair(CtElement element, ASTNode node) {
			super();
			this.element = element;
			this.node = node;
		}
	}

	public class BuilderContext {
		Stack<String> annotationValueName = new Stack<String>();

		Stack<CtElement> arguments = new Stack<CtElement>();

		Stack<CtElement> arrayInitializer = new Stack<CtElement>();

		List<CtTypeReference<?>> casts = new ArrayList<CtTypeReference<?>>();

		CompilationUnitDeclaration compilationunitdeclaration;

		List<CtSimpleType<?>> createdTypes = new ArrayList<CtSimpleType<?>>();

		Stack<CtTry> finallyzer = new Stack<CtTry>();

		boolean forinit = false;

		boolean forupdate = false;

		Stack<String> label = new Stack<String>();

		boolean selector = false;

		/**
		 * Stack of all parents elements
		 */
		Stack<ASTPair> stack = new Stack<ASTPair>();

		Stack<CtTargetedExpression<?, ?>> target = new Stack<CtTargetedExpression<?, ?>>();

		public void addCreatedType(CtSimpleType<?> type) {
			createdTypes.add(type);
		}

		@SuppressWarnings("unchecked")
		void enter(CtElement e, ASTNode node) {
			stack.push(new ASTPair(e, node));
			// aststack.push(node);
			if (compilationunitdeclaration != null) {
				CoreFactory cf = factory.Core();
				int sourceStart = node.sourceStart;
				int sourceEnd = node.sourceEnd;
				if ((e instanceof CtBlock)
						&& (node instanceof MethodDeclaration)) {
					sourceStart = ((MethodDeclaration) node).bodyStart;
					sourceEnd = ((MethodDeclaration) node).bodyEnd;
				}
				CompilationUnit cu = factory.CompilationUnit().create(
						new String(compilationunitdeclaration.getFileName()));
				e.setPosition(cf
						.createSourcePosition(
								cu,
								sourceStart,
								sourceEnd,
								compilationunitdeclaration.compilationResult.lineSeparatorPositions));
			}
			ASTPair pair = stack.peek();
			CtElement current = pair.element;

			if (current instanceof CtExpression) {
				while (!casts.isEmpty())
					((CtExpression<?>) current).getTypeCasts().add(
							casts.remove(0));
			}
			if (current instanceof CtStatement && !context.label.isEmpty()) {
				((CtStatement) current).setLabel(context.label.pop());
			}

			if (e instanceof CtTypedElement && node instanceof Expression) {
				if (((CtTypedElement<?>) e).getType() == null)
					((CtTypedElement<?>) e)
							.setType(references
									.getTypeReference(((Expression) node).resolvedType));
			}

		}

		@SuppressWarnings("unchecked")
		void exit(ASTNode node) {
			ASTPair pair = stack.pop();
			if (pair.node != node)
				throw new RuntimeException("Unconsistant Stack " + node);
			CtElement current = pair.element;
			if (!stack.isEmpty()) {
				current.setParent(stack.peek().element);
				exiter.child = current;
				exiter.scan(stack.peek().element);
			}
			if (template && (current instanceof CtClass)) {
				factory.Template().add((CtClass<? extends Template>) current);
			}
		}

		public List<CtSimpleType<?>> getCreatedTypes() {
			return createdTypes;
		}

		public boolean isArgument(CtElement e) {
			return arguments.size() > 0 && arguments.peek() == e;
		}

		private void popArgument(CtElement e) {
			if (arguments.pop() != e)
				throw new RuntimeException("Unconsistant stack");
		}

		private void pushArgument(CtElement e) {
			arguments.push(e);
		}
	}

	@SuppressWarnings("unchecked")
	public class ParentExiter extends CtInheritanceScanner {
		CtElement child;

		@Override
		public void scanCtElement(CtElement e) {
			if (child instanceof CtAnnotation
					&& context.annotationValueName.isEmpty()) {
				e.getAnnotations().add((CtAnnotation) child);
				return;
			}
		}

		@Override
		public <R> void scanCtExecutable(CtExecutable<R> e) {
			if (child instanceof CtParameter) {
				e.getParameters().add((CtParameter<?>) child);
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
				t.getNestedTypes().add((CtSimpleType<?>) child);
				return;
			} else if (child instanceof CtField) {
				t.getFields().add((CtField<?>) child);
				return;
			} else if (child instanceof CtConstructor) {
				return;
			}
			super.scanCtSimpleType(t);
		}

		@Override
		public <T, E extends CtExpression<?>> void scanCtTargetedExpression(
				CtTargetedExpression<T, E> targetedExpression) {
			if (!context.target.isEmpty()
					&& context.target.peek() == targetedExpression) {
				targetedExpression.setTarget((E) child);
				return;
			}
			super.scanCtTargetedExpression(targetedExpression);
		}

		@Override
		public <T> void scanCtType(CtType<T> type) {
			if (child instanceof CtMethod) {
				type.getMethods().add((CtMethod<?>) child);
				return;
			}
			super.scanCtType(type);
		}

		@Override
		public <T> void scanCtVariable(CtVariable<T> v) {
			if (child instanceof CtExpression && !context.arguments.isEmpty()
					&& context.arguments.peek() == v) {
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

			String name = context.annotationValueName.peek();
			Object value = child;

			if (value instanceof CtVariableAccess)
				value = ((CtVariableAccess) value).getVariable();
			if (value instanceof CtFieldReference
					&& ((CtFieldReference) value).getSimpleName().equals(
							"class")) {
				value = ((CtFieldReference) value).getType();
			}

			if (!context.arrayInitializer.isEmpty()
					&& context.arrayInitializer.peek() == annotation) {
				// Array
				if (annotation.getElementValues().containsKey(name)) {
					if (annotation.getElementValues().get(name) instanceof ArrayList) {
						ArrayList list = (ArrayList) annotation
								.getElementValues().get(name);
						list.add(value);
					} else {
						ArrayList lst = new ArrayList();
						lst.add(value);
						annotation.getElementValues().put(name, lst);
					}
				} else {
					ArrayList lst = new ArrayList();
					lst.add(value);
					annotation.getElementValues().put(name, lst);
				}
			} else {
				annotation.getElementValues().put(name, value);
			}
			super.visitCtAnnotation(annotation);
		}

		@Override
		public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
			if (child instanceof CtBlock) {
				e.setBody((CtBlock) child);
				return;
			}
			super.visitCtAnonymousExecutable(e);
		}

		@Override
		public <T, E extends CtExpression<?>> void visitCtArrayAccess(
				CtArrayAccess<T, E> arrayAccess) {
			if (context.arguments.size() > 0
					&& context.arguments.peek() == arrayAccess) {
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
				if (!context.arguments.isEmpty()
						&& context.arguments.peek() == asserted) {
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
				block.getStatements().add((CtStatement) child);
				return;
			}
			super.visitCtBlock(block);
		}

		@Override
		public <E> void visitCtCase(CtCase<E> caseStatement) {
			if (context.selector && caseStatement.getCaseExpression() == null
					&& child instanceof CtExpression) {
				caseStatement.setCaseExpression((CtExpression<E>) child);
				return;
			} else if (child instanceof CtStatement) {
				caseStatement.getStatements().add((CtStatement) child);
				return;
			}
			super.visitCtCase(caseStatement);
		}

		@Override
		public void visitCtCatch(CtCatch catchBlock) {
			if (child instanceof CtBlock) {
				catchBlock.setBody((CtBlock) child);
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
				CtConstructor c = (CtConstructor) child;
				ctClass.getConstructors().add(c);
				if (c.getPosition() != null
						&& c.getPosition().equals(ctClass.getPosition())) {
					c.setImplicit(true);
				}
			}
			if (child instanceof CtAnonymousExecutable) {
				ctClass.getAnonymousExecutables().add(
						(CtAnonymousExecutable) child);
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
			if (doLoop.getBody()!=null && child instanceof CtExpression
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
			if (context.forinit && child instanceof CtStatement) {
				forLoop.getForInit().add((CtStatement) child);
				return;
			}
			if (!context.forupdate && forLoop.getExpression() == null
					&& child instanceof CtExpression) {
				forLoop.setExpression((CtExpression<Boolean>) child);
				return;
			}
			if (context.forupdate && child instanceof CtStatement) {
				forLoop.getForUpdate().add((CtStatement) child);
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
			if (context.isArgument(invocation) && child instanceof CtExpression) {
				invocation.getArguments().add((CtExpression<?>) child);
				return;
			} else if (child instanceof CtExpression) {
				invocation.setTarget((CtExpression<?>) child);
				return;
			}
			super.visitCtInvocation(invocation);
		}

		@Override
		public <T> void visitCtNewArray(CtNewArray<T> newArray) {
			if (context.isArgument(newArray)) {
				newArray.getDimensionExpressions().add(
						(CtExpression<Integer>) child);
				return;
			} else if (child instanceof CtExpression) {
				newArray.getElements().add((CtExpression<?>) child);
				return;
			}
		}

		@Override
		public <T> void visitCtNewClass(CtNewClass<T> newClass) {
			if (context.isArgument(newClass) && child instanceof CtExpression) {
				newClass.getArguments().add((CtExpression<?>) child);
				return;
			} else if (child instanceof CtClass) {
				newClass.setAnonymousClass((CtClass<?>) child);
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
				context.addCreatedType((CtSimpleType<?>) child);
				if (child.getPosition() != null
						&& child.getPosition().getCompilationUnit() != null) {
					child.getPosition().getCompilationUnit().getDeclaredTypes()
							.add((CtSimpleType) child);
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
				switchStatement.getCases().add((CtCase) child);
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
				synchro.setBlock((CtBlock) child);
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
				if (!context.finallyzer.isEmpty()
						&& context.finallyzer.peek() == tryBlock)
					tryBlock.setFinalizer((CtBlock) child);
				else
					tryBlock.setBody((CtBlock) child);
				return;
			} else if (child instanceof CtCatch) {
				tryBlock.getCatchers().add((CtCatch) child);
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
	};

	public class ReferenceBuilder {

		Map<String, CtTypeReference<?>> basestypes = new TreeMap<String, CtTypeReference<?>>();

		Set<String> typevars = new TreeSet<String>();

		boolean bounds = false;

		public CtTypeReference<?> getBoundedTypeReference(TypeBinding binding) {
			bounds = true;
			CtTypeReference<?> ref = getTypeReference(binding);
			bounds = false;
			return ref;
		}

		@SuppressWarnings("unchecked")
		public CtExecutableReference getExecutableReference(MethodBinding exec) {
			CtExecutableReference ref = factory.Core()
					.createExecutableReference();
			ref.setDeclaringType(getTypeReference(exec.declaringClass));
			ref.setType(getTypeReference(exec.returnType));
			ref.setSimpleName(new String(exec.selector));
			ref.setStatic(exec.isStatic());
			if (exec.parameters != null) {
				for (TypeBinding b : exec.parameters)
					ref.getParameterTypes().add(getTypeReference(b));
			}
			if (exec.typeVariables != null) {
				for (TypeVariableBinding b : exec.typeVariables)
					ref.getActualTypeArguments().add(getTypeReference(b));
			}

			return ref;
		}

		public CtPackageReference getPackageReference(PackageBinding reference) {
			String name = new String(reference.shortReadableName());
			if (name.length() == 0)
				return null;
			CtPackageReference ref = factory.Core().createPackageReference();
			ref.setSimpleName(name);
			return ref;
		}

		@SuppressWarnings("unchecked")
		public CtTypeReference getTypeReference(TypeBinding binding) {
			CtTypeReference ref = null;
			if (binding == null)
				return null;
			if (binding instanceof RawTypeBinding) {
				ref = getTypeReference(((ParameterizedTypeBinding) binding)
						.genericType());
			} else if (binding instanceof ParameterizedTypeBinding) {
				ref = getTypeReference(((ParameterizedTypeBinding) binding)
						.genericType());
				if (((ParameterizedTypeBinding) binding).arguments != null)
					for (TypeBinding b : ((ParameterizedTypeBinding) binding).arguments) {
						ref.getActualTypeArguments().add(getTypeReference(b));
					}
			} else if (binding instanceof BinaryTypeBinding) {
				ref = factory.Core().createTypeReference();
				if (binding.enclosingType() != null) {
					ref.setDeclaringType(getTypeReference(binding
							.enclosingType()));
				} else {
					ref.setPackage(getPackageReference(binding.getPackage()));
				}
				ref.setSimpleName(new String(binding.sourceName()));

			} else if (binding instanceof TypeVariableBinding) {
				ref = factory.Core().createTypeParameterReference();
				if (binding instanceof CaptureBinding) {
					ref.setSimpleName("?");
					bounds = true;
				} else {
					ref.setSimpleName(new String(binding.sourceName()));
				}
				TypeVariableBinding b = (TypeVariableBinding) binding;
				if (bounds && b.superclass != null
						&& b.firstBound == b.superclass) {
					boolean oldBounds = bounds;
					bounds = false;
					((CtTypeParameterReference) ref).getBounds().add(
							getTypeReference(b.superclass));
					bounds = oldBounds;
				}
				if (bounds && b.superInterfaces != null
						&& b.superInterfaces != Binding.NO_SUPERINTERFACES) {
					bounds = false;
					for (int i = 0, length = b.superInterfaces.length; i < length; i++) {
						TypeBinding tb = b.superInterfaces[i];
						((CtTypeParameterReference) ref).getBounds().add(
								getTypeReference(tb));
					}
				}
				if (binding instanceof CaptureBinding)
					bounds = false;

			} else if (binding instanceof BaseTypeBinding) {
				String name = new String(binding.sourceName());
				ref = basestypes.get(name);
				if (ref == null) {
					ref = factory.Core().createTypeReference();
					ref.setSimpleName(name);
					basestypes.put(name, ref);
				}
			} else if (binding instanceof WildcardBinding) {
				CtTypeParameterReference reference = factory.Core()
						.createTypeParameterReference();
				reference.setSimpleName("?");
				if (((WildcardBinding) binding).boundKind == Wildcard.SUPER)
					reference.setUpper(false);

				if (((WildcardBinding) binding).bound != null)
					reference
							.getBounds()
							.add(getTypeReference(((WildcardBinding) binding).bound));
				ref = reference;
			} else if (binding instanceof LocalTypeBinding) {
				ref = factory.Core().createTypeReference();
				if (binding.isAnonymousType())
					ref.setSimpleName("");
				else{
					ref.setSimpleName(new String(binding.sourceName()));
					if (((LocalTypeBinding) binding).enclosingMethod == null && binding.enclosingType() != null && binding.enclosingType() instanceof LocalTypeBinding)
						ref.setDeclaringType(getTypeReference(binding
								.enclosingType()));
				}
			} else if (binding instanceof SourceTypeBinding) {
				ref = factory.Core().createTypeReference();
				if (binding.isAnonymousType()) {
					ref.setSimpleName("");
				} else {
					ref.setSimpleName(new String(binding.sourceName()));
					if (binding.enclosingType() != null)
						ref.setDeclaringType(getTypeReference(binding
								.enclosingType()));
					else
						ref.setPackage(getPackageReference(binding.getPackage()));
				}
			} else if (binding instanceof ArrayBinding) {
				CtArrayTypeReference arrayref = factory.Core()
						.createArrayTypeReference();
				ref = arrayref;
				for (int i = 1; i < binding.dimensions(); i++) {
					CtArrayTypeReference tmp = factory.Core()
							.createArrayTypeReference();
					arrayref.setComponentType(tmp);
					arrayref = tmp;
				}
				arrayref.setComponentType(getTypeReference(binding
						.leafComponentType()));
			} else {
				throw new RuntimeException("Unkown TypeBinding");
			}
			return ref;
		}

		@SuppressWarnings("unchecked")
		public CtVariableReference getVariableReference(VariableBinding varbin) {

			if (varbin instanceof FieldBinding) {
				CtFieldReference ref = factory.Core().createFieldReference();
				ref.setSimpleName(new String(varbin.name));
				ref.setType(getTypeReference(varbin.type));

				if (((FieldBinding) varbin).declaringClass != null)
					ref.setDeclaringType(getTypeReference(((FieldBinding) varbin).declaringClass));
				else {
					ref.setDeclaringType(ref.getType());
				}
				ref.setFinal(varbin.isFinal());
				ref.setStatic((varbin.modifiers & ClassFileConstants.AccStatic) != 0);
				return ref;
			} else if (varbin instanceof LocalVariableBinding) {
				if (((LocalVariableBinding) varbin).declaration instanceof Argument
						&& ((LocalVariableBinding) varbin).declaringScope instanceof MethodScope) {
					CtParameterReference ref = factory.Core()
							.createParameterReference();
					ref.setSimpleName(new String(varbin.name));
					ref.setType(getTypeReference(varbin.type));
					ref.setDeclaringExecutable(getExecutableReference(((AbstractMethodDeclaration) ((MethodScope) ((LocalVariableBinding) varbin).declaringScope)
							.referenceContext()).binding));
					return ref;
				} else {
					CtLocalVariableReference ref = factory.Core()
							.createLocalVariableReference();
					ref.setSimpleName(new String(varbin.name));
					CtTypeReference ref2 = getTypeReference(varbin.type);
					ref.setType(ref2);
					ref.setDeclaration(getLocalVariableDeclaration(ref
							.getSimpleName()));
					return ref;
				}
			} else {
				throw new RuntimeException("Unknow VariableBinding");
			}
		}

		public List<CtTypeReference<?>> getBoundedTypesReferences(
				TypeBinding[] genericTypeArguments) {
			List<CtTypeReference<?>> res = new ArrayList<CtTypeReference<?>>();
			for (TypeBinding tb : genericTypeArguments) {
				res.add(getBoundedTypeReference(tb));
			}
			return res;
		}
	}

	public static String cleanJavadoc(String doc) {
		StringBuffer ret = new StringBuffer();
		String[] docs = doc.split("\n");
		for (int i = 1; i < docs.length - 1; i++) {
			ret.append(docs[i].substring(docs[i].indexOf('*') + 1));
			ret.append("\n");
		}
		// clean '\r'
		StringBuffer ret2 = new StringBuffer();
		for (int i = 0; i < ret.length(); i++) {
			if (ret.charAt(i) != '\r')
				ret2.append(ret.charAt(i));
		}
		return ret2.toString();
	}

	public static Set<ModifierKind> getModifier(int mod) {
		Set<ModifierKind> ret = new TreeSet<ModifierKind>();
		if ((mod & ClassFileConstants.AccPublic) != 0)
			ret.add(ModifierKind.PUBLIC);
		if ((mod & ClassFileConstants.AccPrivate) != 0)
			ret.add(ModifierKind.PRIVATE);
		if ((mod & ClassFileConstants.AccProtected) != 0)
			ret.add(ModifierKind.PROTECTED);
		if ((mod & ClassFileConstants.AccStatic) != 0)
			ret.add(ModifierKind.STATIC);
		if ((mod & ClassFileConstants.AccFinal) != 0)
			ret.add(ModifierKind.FINAL);
		if ((mod & ClassFileConstants.AccSynchronized) != 0)
			ret.add(ModifierKind.SYNCHRONIZED);
		if ((mod & ClassFileConstants.AccVolatile) != 0)
			ret.add(ModifierKind.VOLATILE);
		if ((mod & ClassFileConstants.AccTransient) != 0)
			ret.add(ModifierKind.TRANSIENT);
		if ((mod & ClassFileConstants.AccAbstract) != 0)
			ret.add(ModifierKind.ABSTRACT);
		if ((mod & ClassFileConstants.AccStrictfp) != 0)
			ret.add(ModifierKind.STRICTFP);
		if ((mod & ClassFileConstants.AccNative) != 0)
			ret.add(ModifierKind.NATIVE);
		return ret;
	}

	/**
	 * Search the line number corresponding to a specific position
	 */
	public static final int searchLineNumber(int[] startLineIndexes,
			int position) {
		if (startLineIndexes == null)
			return 1;
		int length = startLineIndexes.length;
		if (length == 0)
			return 1;
		int g = 0, d = length - 1;
		int m = 0, start;
		while (g <= d) {
			m = (g + d) / 2;
			if (position < (start = startLineIndexes[m])) {
				d = m - 1;
			} else if (position > start) {
				g = m + 1;
			} else {
				return m + 1;
			}
		}
		if (position < startLineIndexes[m]) {
			return m + 1;
		}
		return m + 2;
	}

	BuilderContext context = new BuilderContext();

	ParentExiter exiter = new ParentExiter();

	Factory factory;

	ReferenceBuilder references = new ReferenceBuilder();

	public boolean template = false;

	public JDTTreeBuilder(Factory factory) {
		super();
		this.factory = factory;
	}

	private void createExpression(StringLiteralConcatenation literal,
			BlockScope scope, List<Expression> rst) {
		if (rst.isEmpty())
			return;

		rst.get(0).traverse(this, scope);
		rst.remove(0);

		if (rst.size() > 1) {
			CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
			op.setKind(BinaryOperatorKind.PLUS);
			context.enter(op, literal);
			createExpression(literal, scope, rst);
			context.exit(literal);
		} else {
			createExpression(literal, scope, rst);
		}

	}

	CtSimpleType<?> createType(TypeDeclaration typeDeclaration) {
		CtSimpleType<?> type = null;
		if ((typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0) {
			type = (CtSimpleType<?>) factory.Core().createAnnotationType();
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccEnum) != 0) {
			CtEnum<?> e = factory.Core().createEnum();
			if (typeDeclaration.superInterfaces != null) {
				for (TypeReference ref : typeDeclaration.superInterfaces) {
					e.getSuperInterfaces().add(
							references.getTypeReference(ref.resolvedType));
				}
			}
			type = e;
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccInterface) != 0) {
			CtInterface<?> interf = factory.Core().createInterface();
			if (typeDeclaration.superInterfaces != null) {
				for (TypeReference ref : typeDeclaration.superInterfaces) {
					interf.getSuperInterfaces().add(
							references.getTypeReference(ref.resolvedType));
				}
			}
			if (typeDeclaration.typeParameters != null)
				for (TypeParameter p : typeDeclaration.typeParameters) {
					interf.getFormalTypeParameters().add(
							references.getBoundedTypeReference(p.binding));
				}
			type = interf;
		} else {
			CtClass<?> cl = factory.Core().createClass();
			if (typeDeclaration.superclass != null) {
				cl.setSuperclass(references
						.getTypeReference(typeDeclaration.superclass.resolvedType));
			}
			if (typeDeclaration.superInterfaces != null) {
				for (TypeReference ref : typeDeclaration.superInterfaces) {
					cl.getSuperInterfaces().add(
							references.getTypeReference(ref.resolvedType));
				}
			}
			if (typeDeclaration.typeParameters != null)
				for (TypeParameter p : typeDeclaration.typeParameters) {
					cl.getFormalTypeParameters().add(
							references.getBoundedTypeReference(p.binding));
				}
			type = cl;
		}
		type.setSimpleName(new String(typeDeclaration.name));
		// Setting modifiers
		type.setModifiers(getModifier(typeDeclaration.modifiers));
		// type.setDocComment(getJavaDoc(typeDeclaration.javadoc));

		return type;
	}

	@Override
	public void endVisit(AllocationExpression allocationExpression,
			BlockScope scope) {
		context.exit(allocationExpression);
	}

	@Override
	public void endVisit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		context.exit(and_and_Expression);
	}

	@Override
	public void endVisit(AnnotationMethodDeclaration annotationTypeDeclaration,
			ClassScope classScope) {
		context.exit(annotationTypeDeclaration);
	}

	public void endVisit(Argument argument, BlockScope scope) {
		context.exit(argument);
	}

	@Override
	public void endVisit(ArrayAllocationExpression arrayAllocationExpression,
			BlockScope scope) {
		context.exit(arrayAllocationExpression);
	}

	@Override
	public void endVisit(ArrayInitializer arrayInitializer, BlockScope scope) {
		if (!context.arrayInitializer.isEmpty()) {
			context.arrayInitializer.pop();
		} else {
			context.exit(arrayInitializer);
		}
	}

	@Override
	public void endVisit(ArrayReference arrayReference, BlockScope scope) {
		context.exit(arrayReference);
	}

	@Override
	public void endVisit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		context.exit(arrayTypeReference);
	}

	@Override
	public void endVisit(AssertStatement assertStatement, BlockScope scope) {
		context.exit(assertStatement);
	}

	@Override
	public void endVisit(Assignment assignment, BlockScope scope) {
		context.exit(assignment);
	}

	@Override
	public void endVisit(BinaryExpression binaryExpression, BlockScope scope) {
		context.exit(binaryExpression);
	}

	@Override
	public void endVisit(Block block, BlockScope scope) {
		context.exit(block);
	}

	@Override
	public void endVisit(BreakStatement breakStatement, BlockScope scope) {
		context.exit(breakStatement);
	}

	@Override
	public void endVisit(CaseStatement caseStatement, BlockScope scope) {
		context.exit(caseStatement);
	}

	@Override
	public void endVisit(CharLiteral charLiteral, BlockScope scope) {
		context.exit(charLiteral);
	}

	@Override
	public void endVisit(ClassLiteralAccess classLiteral, BlockScope scope) {
		context.exit(classLiteral);
	}

	@Override
	public void endVisit(CompoundAssignment compoundAssignment, BlockScope scope) {
		context.exit(compoundAssignment);
	}

	@Override
	public void endVisit(ConditionalExpression conditionalExpression,
			BlockScope scope) {
		context.exit(conditionalExpression);
	}

	public void endVisit(ConstructorDeclaration constructorDeclaration,
			ClassScope scope) {
		context.exit(constructorDeclaration);
		if (context.stack.peek().node == constructorDeclaration)
			context.exit(constructorDeclaration);
	}

	@Override
	public void endVisit(ContinueStatement continueStatement, BlockScope scope) {
		context.exit(continueStatement);
	}

	@Override
	public void endVisit(DoStatement doStatement, BlockScope scope) {
		context.exit(doStatement);
	}

	@Override
	public void endVisit(DoubleLiteral doubleLiteral, BlockScope scope) {
		context.exit(doubleLiteral);
	}

	@Override
	public void endVisit(EqualExpression equalExpression, BlockScope scope) {
		context.exit(equalExpression);
	}

	@Override
	public void endVisit(ExplicitConstructorCall explicitConstructor,
			BlockScope scope) {
		context.exit(explicitConstructor);
	}

	public void endVisit(ExtendedStringLiteral extendedStringLiteral,
			BlockScope scope) {
		context.exit(extendedStringLiteral);
	}

	@Override
	public void endVisit(FalseLiteral falseLiteral, BlockScope scope) {
		context.exit(falseLiteral);
	}

	public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		context.exit(fieldDeclaration);
	}

	@SuppressWarnings("unchecked")
	public void endVisit(FieldReference fieldReference, BlockScope scope) {
		context.exit(fieldReference);
	}

	@Override
	public void endVisit(FloatLiteral floatLiteral, BlockScope scope) {
		context.exit(floatLiteral);
	}

	public void endVisit(ForeachStatement forStatement, BlockScope scope) {
		context.exit(forStatement);
	}

	@Override
	public void endVisit(ForStatement forStatement, BlockScope scope) {
		context.exit(forStatement);
	}

	@Override
	public void endVisit(IfStatement ifStatement, BlockScope scope) {
		context.exit(ifStatement);
	}

	@Override
	public void endVisit(Initializer initializer, MethodScope scope) {
		context.exit(initializer);
	}

	@Override
	public void endVisit(InstanceOfExpression instanceOfExpression,
			BlockScope scope) {
		context.exit(instanceOfExpression);
	}

	@Override
	public void endVisit(IntLiteral intLiteral, BlockScope scope) {
		context.exit(intLiteral);
	}

	@Override
	public void endVisit(LocalDeclaration localDeclaration, BlockScope scope) {
		context.exit(localDeclaration);
	}

	public void endVisit(LongLiteral longLiteral, BlockScope scope) {
		context.exit(longLiteral);
	}

	@Override
	public void endVisit(MarkerAnnotation annotation, BlockScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(MemberValuePair pair, BlockScope scope) {
		if (!context.annotationValueName.pop().equals(new String(pair.name))) {
			throw new RuntimeException("Unconsistant Stack");
		}
	}

	@Override
	public void endVisit(MessageSend messageSend, BlockScope scope) {
		context.exit(messageSend);
	}

	public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
		// Exit from method and Block
		context.exit(methodDeclaration);
		if (context.stack.peek().node == methodDeclaration)
			context.exit(methodDeclaration);
	}

	public void endVisit(NormalAnnotation annotation, BlockScope scope) {
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(NullLiteral nullLiteral, BlockScope scope) {
		context.exit(nullLiteral);
	}

	@Override
	public void endVisit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		context.exit(or_or_Expression);
	}

	@Override
	public void endVisit(
			ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference,
			ClassScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedQualifiedTypeReference);
	}

	@Override
	public void endVisit(
			ParameterizedSingleTypeReference parameterizedSingleTypeReference,
			BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedSingleTypeReference);
	}

	@Override
	public void endVisit(
			ParameterizedSingleTypeReference parameterizedSingleTypeReference,
			ClassScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(parameterizedSingleTypeReference);
	}

	@Override
	public void endVisit(PostfixExpression postfixExpression, BlockScope scope) {
		context.exit(postfixExpression);
	}

	@Override
	public void endVisit(PrefixExpression prefixExpression, BlockScope scope) {
		context.exit(prefixExpression);
	}

	@Override
	public void endVisit(
			QualifiedAllocationExpression qualifiedAllocationExpression,
			BlockScope scope) {
		endVisit((AllocationExpression) qualifiedAllocationExpression, scope);
	}

	public void endVisit(QualifiedNameReference qualifiedNameReference,
			BlockScope scope) {
		if (context.stack.peek().node == qualifiedNameReference)
			context.exit(qualifiedNameReference);
	}

	@Override
	public void endVisit(QualifiedThisReference qualifiedThisReference,
			BlockScope scope) {
		endVisit((ThisReference) qualifiedThisReference, scope);
	}

	@Override
	public void endVisit(QualifiedTypeReference arg0, BlockScope arg1) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(arg0);
	}

	@Override
	public void endVisit(ReturnStatement returnStatement, BlockScope scope) {
		context.exit(returnStatement);
	}

	@Override
	public void endVisit(SingleMemberAnnotation annotation, BlockScope scope) {
		if (!context.annotationValueName.pop().equals("value")) {
			throw new RuntimeException("unconsistant Stack");
		}
		context.exit(annotation);
		skipTypeInAnnotation = false;
	}

	@Override
	public void endVisit(SingleNameReference singleNameReference,
			BlockScope scope) {
		if (context.stack.peek().node == singleNameReference)
			context.exit(singleNameReference);
	}

	@Override
	public void endVisit(SingleTypeReference singleTypeReference,
			BlockScope scope) {
		if (skipTypeInAnnotation) {
			skipTypeInAnnotation = false;
			return;
		}
		context.exit(singleTypeReference);
	}

	@Override
	public void endVisit(SingleTypeReference singleTypeReference,
			ClassScope scope) {
		context.exit(singleTypeReference);
	}

	@Override
	public void endVisit(StringLiteral stringLiteral, BlockScope scope) {
		context.exit(stringLiteral);
	}

	@Override
	public void endVisit(StringLiteralConcatenation literal, BlockScope scope) {
		context.exit(literal);
	}

	@Override
	public void endVisit(QualifiedSuperReference qualifiedsuperReference, BlockScope scope) {
		context.exit(qualifiedsuperReference);
	}
	@Override
	public void endVisit(SuperReference superReference, BlockScope scope) {
		context.exit(superReference);
	}

	@Override
	public void endVisit(SwitchStatement switchStatement, BlockScope scope) {
		context.exit(switchStatement);
	}

	@Override
	public void endVisit(SynchronizedStatement synchronizedStatement,
			BlockScope scope) {
		context.exit(synchronizedStatement);
	}

	@Override
	public void endVisit(ThisReference thisReference, BlockScope scope) {
		context.exit(thisReference);
	}

	public void endVisit(ThrowStatement throwStatement, BlockScope scope) {
		context.exit(throwStatement);
	}

	@Override
	public void endVisit(TrueLiteral trueLiteral, BlockScope scope) {
		context.exit(trueLiteral);
	}

	@Override
	public void endVisit(TryStatement tryStatement, BlockScope scope) {
		context.exit(tryStatement);
	}

	@Override
	public void endVisit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		context.exit(localTypeDeclaration);
	}

	@Override
	public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		while (!context.stack.isEmpty()
				&& context.stack.peek().node == memberTypeDeclaration) {
			context.exit(memberTypeDeclaration);
		}
	}

	@Override
	public void endVisit(TypeDeclaration typeDeclaration,
			CompilationUnitScope scope) {
		while (!context.stack.isEmpty()
				&& context.stack.peek().node == typeDeclaration) {
			context.exit(typeDeclaration);
		}
		context.compilationunitdeclaration = null;
	}

	@Override
	public void endVisit(UnaryExpression unaryExpression, BlockScope scope) {
		context.exit(unaryExpression);
	}

	@Override
	public void endVisit(WhileStatement whileStatement, BlockScope scope) {
		context.exit(whileStatement);
	}

	BinaryOperatorKind getBinaryOperatorKind(int bits) {
		// switch ((bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
		switch (bits) {
		case OperatorIds.EQUAL_EQUAL:
			return BinaryOperatorKind.EQ;
		case OperatorIds.LESS_EQUAL:
			return BinaryOperatorKind.LE;
		case OperatorIds.GREATER_EQUAL:
			return BinaryOperatorKind.GE;
		case OperatorIds.NOT_EQUAL:
			return BinaryOperatorKind.NE;
		case OperatorIds.LEFT_SHIFT:
			return BinaryOperatorKind.SL;
		case OperatorIds.RIGHT_SHIFT:
			return BinaryOperatorKind.SR;
		case OperatorIds.UNSIGNED_RIGHT_SHIFT:
			return BinaryOperatorKind.USR;
		case OperatorIds.OR_OR:
			return BinaryOperatorKind.OR;
		case OperatorIds.AND_AND:
			return BinaryOperatorKind.AND;
		case OperatorIds.PLUS:
			return BinaryOperatorKind.PLUS;
		case OperatorIds.MINUS:
			return BinaryOperatorKind.MINUS;
		case OperatorIds.NOT:
			return BinaryOperatorKind.NE;
		case OperatorIds.REMAINDER:
			return BinaryOperatorKind.MOD;
		case OperatorIds.XOR:
			return BinaryOperatorKind.BITXOR;
		case OperatorIds.AND:
			return BinaryOperatorKind.BITAND;
		case OperatorIds.MULTIPLY:
			return BinaryOperatorKind.MUL;
		case OperatorIds.OR:
			return BinaryOperatorKind.BITOR;
		case OperatorIds.DIVIDE:
			return BinaryOperatorKind.DIV;
		case OperatorIds.GREATER:
			return BinaryOperatorKind.GT;
		case OperatorIds.LESS:
			return BinaryOperatorKind.LT;
		case OperatorIds.QUESTIONCOLON:
			throw new RuntimeException("Unknow operator");
		case OperatorIds.EQUAL:
			return BinaryOperatorKind.EQ;
		}
		return null;
	}

	public List<CtSimpleType<?>> getCreatedTypes() {
		return context.getCreatedTypes();
	}

	public String getJavaDoc(Javadoc javadoc,
			CompilationUnitDeclaration declaration) {
		if (javadoc != null) {
			try{
			String s = new String(
					declaration.compilationResult.compilationUnit.getContents(),
					javadoc.sourceStart, javadoc.sourceEnd
							- javadoc.sourceStart + 1);
			return cleanJavadoc(s);
			}catch(StringIndexOutOfBoundsException e){//BCUTAG trouver cause
				return null;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected <T> CtLocalVariable<T> getLocalVariableDeclaration(
			final String name) {
		List<CtElement> reversedElements = new ArrayList<CtElement>();
		for (ASTPair element : context.stack) {
			reversedElements.add(0, element.element);
		}

		for (CtElement element : reversedElements) {
			// TODO check if the variable is visible from here

			List<CtLocalVariable> var = Query.getElements(element,
					new TypeFilter<CtLocalVariable>(CtLocalVariable.class) {
						@Override
						public boolean matches(CtLocalVariable element) {
							return name.equals(element.getSimpleName())
									&& super.matches(element);
						}
					});

			if (var.size() > 0) {
				return var.get(0);
			}
		}
		throw new IllegalStateException(
				"Could not find declaration for local variable " + name);
	}

	UnaryOperatorKind getUnaryOperator(int op) {
		switch (op) {
		case OperatorIds.PLUS:
			return UnaryOperatorKind.POS;
		case OperatorIds.MINUS:
			return UnaryOperatorKind.NEG;
		case OperatorIds.NOT:
			return UnaryOperatorKind.NOT;
		case OperatorIds.TWIDDLE:
			return UnaryOperatorKind.COMPL;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(AllocationExpression allocationExpression,
			BlockScope scope) {
		CtNewClass<?> c = factory.Core().createNewClass();
		if (allocationExpression.type != null)
			c.setType(references
					.getTypeReference(allocationExpression.type.resolvedType));
		c.setExecutable(references
				.getExecutableReference(allocationExpression.binding));
		c.getExecutable().setType(
				(CtTypeReference) c.getExecutable().getDeclaringType());
		context.enter(c, allocationExpression);

		if (allocationExpression.enclosingInstance() != null) {
			context.target.push(c);
			allocationExpression.enclosingInstance().traverse(this, scope);
			context.target.pop();
		}

		context.pushArgument(c);
		if (allocationExpression.arguments != null) {
			for (Expression e : allocationExpression.arguments)
				e.traverse(this, scope);
		}
		context.popArgument(c);
		return false;
	}

	@Override
	public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((and_and_Expression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, and_and_Expression);
		return true; // do nothing by default, keep traversing
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(AnnotationMethodDeclaration annotationTypeDeclaration,
			ClassScope classScope) {
		CtField<?> f = factory.Core().createField();
		f.setSimpleName(new String(annotationTypeDeclaration.selector));
		f.setType(references
				.getTypeReference(annotationTypeDeclaration.binding.returnType));
		context.enter(f, annotationTypeDeclaration);

		if (annotationTypeDeclaration.annotations != null) {
			int annotationsLength = annotationTypeDeclaration.annotations.length;
			for (int i = 0; i < annotationsLength; i++)
				annotationTypeDeclaration.annotations[i].traverse(this,
						annotationTypeDeclaration.scope);
		}

		if (annotationTypeDeclaration.defaultValue != null) {
			annotationTypeDeclaration.defaultValue.traverse(this,
					annotationTypeDeclaration.scope);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(Argument argument, BlockScope scope) {
		CtParameter<?> p = factory.Core().createParameter();
		p.setSimpleName(new String(argument.name));
		p.setVarArgs(argument.isVarArgs());
		p.setModifiers(getModifier(argument.modifiers));
		if (argument.type != null)
			p.setType(references.getTypeReference(argument.type.resolvedType));
		context.enter(p, argument);
		if (argument.initialization != null)
			argument.initialization.traverse(this, scope);

		if (argument.annotations != null)
			for (Annotation a : argument.annotations)
				a.traverse(this, scope);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ArrayAllocationExpression arrayAllocationExpression,
			BlockScope scope) {
		CtNewArray<?> array = factory.Core().createNewArray();
		array.setType(references
				.getTypeReference(arrayAllocationExpression.resolvedType));

		context.enter(array, arrayAllocationExpression);

		context.pushArgument(array);
		if (arrayAllocationExpression.dimensions != null)
			for (Expression e : arrayAllocationExpression.dimensions)
				if (e != null)
					e.traverse(this, scope);
		context.popArgument(array);

		if (arrayAllocationExpression.initializer != null
				&& arrayAllocationExpression.initializer.expressions != null) {
			for (Expression e : arrayAllocationExpression.initializer.expressions)
				e.traverse(this, scope);
		}
		return false;
	}

	@Override
	public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {

		if (context.annotationValueName.isEmpty()) {
			CtNewArray<?> array = factory.Core().createNewArray();
			context.enter(array, arrayInitializer);
		} else {
			context.arrayInitializer.push(context.stack.peek().element);
		}
		return super.visit(arrayInitializer, scope);
	}

	@Override
	public boolean visit(ArrayReference arrayReference, BlockScope scope) {
		CtArrayAccess<?, ?> a = factory.Core().createArrayAccess();
		context.enter(a, arrayReference);
		arrayReference.receiver.traverse(this, scope);
		context.arguments.push(a);
		arrayReference.position.traverse(this, scope);
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references.getTypeReference(arrayTypeReference.resolvedType));
		context.enter(l, arrayTypeReference);
		return true;
	}

	@Override
	public boolean visit(AssertStatement assertStatement, BlockScope scope) {
		CtAssert<?> a = factory.Core().createAssert();
		context.enter(a, assertStatement);
		assertStatement.assertExpression.traverse(this, scope);
		context.arguments.push(a);
		if (assertStatement.exceptionArgument != null) {
			assertStatement.exceptionArgument.traverse(this, scope);
		}
		context.arguments.pop();
		return false;
	}

	@Override
	public boolean visit(Assignment assignment, BlockScope scope) {
		CtAssignment<Object, Object> assign = factory.Core().createAssignment();
		context.enter(assign, assignment);
		return true;
	}

	@Override
	public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((binaryExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, binaryExpression);
		return true;
	}

	@Override
	public boolean visit(Block block, BlockScope scope) {
		CtBlock<?> b = factory.Core().createBlock();
		context.enter(b, block);
		return true;
	}

	@Override
	public boolean visit(BreakStatement breakStatement, BlockScope scope) {
		CtBreak b = factory.Core().createBreak();
		if (breakStatement.label != null)
			b.setTargetLabel(new String(breakStatement.label));
		context.enter(b, breakStatement);
		return true;
	}

	@Override
	public boolean visit(CaseStatement caseStatement, BlockScope scope) {
		CtCase<?> c = factory.Core().createCase();
		context.enter(c, caseStatement);

		if (caseStatement.constantExpression != null) {
			context.selector = true;
			caseStatement.constantExpression.traverse(this, scope);
			context.selector = false;
		}
		return false;
	}

	@Override
	public boolean visit(CastExpression castExpression, BlockScope scope) {
		context.casts.add(references
				.getTypeReference(castExpression.resolvedType));
		castExpression.expression.traverse(this, scope);
		return false;
	}

	@Override
	public boolean visit(CharLiteral charLiteral, BlockScope scope) {
		CtLiteral<Character> l = factory.Core().createLiteral();
		l.setValue(charLiteral.constant.charValue());
		context.enter(l, charLiteral);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
		CtTypeReference ref = references
				.getTypeReference(classLiteral.targetType);
		CtFieldReference fr = factory.Core().createFieldReference();
		fr.setSimpleName("class");
		fr.setType(ref);
		fr.setDeclaringType(ref);

		CtFieldAccess<Class> fa = factory.Core().createFieldAccess();
		fa.setType(ref);
		fa.setVariable(fr);

		context.enter(fa, classLiteral);

		return true;
	}

	@Override
	public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
		CtOperatorAssignment<Object, Object> a = factory.Core()
				.createOperatorAssignment();
		a.setKind(getBinaryOperatorKind(compoundAssignment.operator));
		context.enter(a, compoundAssignment);
		return super.visit(compoundAssignment, scope);
	}

	@Override
	public boolean visit(ConditionalExpression conditionalExpression,
			BlockScope scope) {
		CtConditional<?> c = factory.Core().createConditional();
		context.enter(c, conditionalExpression);
		return super.visit(conditionalExpression, scope);
	}

	@SuppressWarnings("unchecked")
	public boolean visit(ConstructorDeclaration constructorDeclaration,
			ClassScope scope) {
		CtConstructor c = factory.Core().createConstructor();
		c.setModifiers(getModifier(constructorDeclaration.modifiers));

		c.setDocComment(getJavaDoc(constructorDeclaration.javadoc,
				scope.referenceCompilationUnit()));

		context.enter(c, constructorDeclaration);

		if (constructorDeclaration.annotations != null) {
			int annotationsLength = constructorDeclaration.annotations.length;
			for (int i = 0; i < annotationsLength; i++)
				constructorDeclaration.annotations[i].traverse(this,
						constructorDeclaration.scope);
		}

		context.pushArgument(c);
		if (constructorDeclaration.arguments != null) {
			int argumentLength = constructorDeclaration.arguments.length;
			for (int i = 0; i < argumentLength; i++)
				constructorDeclaration.arguments[i].traverse(this,
						constructorDeclaration.scope);
		}
		context.popArgument(c);

		if (constructorDeclaration.thrownExceptions != null) {
			for (TypeReference r : constructorDeclaration.thrownExceptions)
				c.getThrownTypes().add(
						references.getTypeReference(r.resolvedType));
		}
		for (TypeBinding b : constructorDeclaration.binding.typeVariables) {
			c.getFormalTypeParameters().add(
					references.getBoundedTypeReference(b));
		}

		// Create block
		if (!constructorDeclaration.isAbstract()) {
			CtBlock<?> b = factory.Core().createBlock();
			context.enter(b, constructorDeclaration);
		}

		if (constructorDeclaration.constructorCall != null)
			constructorDeclaration.constructorCall.traverse(this,
					constructorDeclaration.scope);

		if (constructorDeclaration.statements != null) {
			for (Statement s : constructorDeclaration.statements)
				s.traverse(this, constructorDeclaration.scope);
		}
		return false;
	}

	@Override
	public boolean visit(ContinueStatement continueStatement, BlockScope scope) {
		CtContinue c = factory.Core().createContinue();
		context.enter(c, continueStatement);
		if (continueStatement.label != null) {
			c.setTargetLabel(new String(continueStatement.label));
		}
		return true;
	}

	@Override
	public boolean visit(DoStatement doStatement, BlockScope scope) {
		CtDo d = factory.Core().createDo();
		context.enter(d, doStatement);
		return true;
	}

	@Override
	public boolean visit(DoubleLiteral doubleLiteral, BlockScope scope) {
		CtLiteral<Double> d = factory.Core().createLiteral();
		d.setValue(doubleLiteral.constant.doubleValue());
		context.enter(d, doubleLiteral);
		return true;
	}

	@Override
	public boolean visit(EqualExpression equalExpression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((equalExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, equalExpression);
		return true; // do nothing by default, keep traversing
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ExplicitConstructorCall explicitConstructor,
			BlockScope scope) {
		CtInvocation inv = factory.Core().createInvocation();
		if (explicitConstructor.isImplicitSuper()) {
			inv.setImplicit(true);
		}
		inv.setExecutable(references
				.getExecutableReference(explicitConstructor.binding));
		inv.getExecutable().setType(inv.getExecutable().getDeclaringType());
		inv.setType(inv.getExecutable().getType());

		context.enter(inv, explicitConstructor);

		if (explicitConstructor.qualification != null) {
			explicitConstructor.qualification.traverse(this, scope);
		}
		if (explicitConstructor.typeArguments != null) {
			for (int i = 0, typeArgumentsLength = explicitConstructor.typeArguments.length; i < typeArgumentsLength; i++) {
				explicitConstructor.typeArguments[i].traverse(this, scope);
			}
		}

		context.arguments.push(inv);
		if (explicitConstructor.arguments != null) {
			for (int i = 0, argumentLength = explicitConstructor.arguments.length; i < argumentLength; i++)
				explicitConstructor.arguments[i].traverse(this, scope);
		}
		context.arguments.pop();

		return false;
	}

	public boolean visit(ExtendedStringLiteral extendedStringLiteral,
			BlockScope scope) {
		CtLiteral<String> l = factory.Core().createLiteral();
		l.setValue(new String(extendedStringLiteral.source()));
		context.enter(l, extendedStringLiteral);
		return true;
	}

	@Override
	public boolean visit(FalseLiteral falseLiteral, BlockScope scope) {
		CtLiteral<Boolean> l = factory.Core().createLiteral();
		l.setValue(false);
		context.enter(l, falseLiteral);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		CtField<?> field = factory.Core().createField();
		field.setSimpleName(new String(fieldDeclaration.name));
		if (fieldDeclaration.type != null)
			field.setType(references
					.getTypeReference(fieldDeclaration.type.resolvedType));
		field.setModifiers(getModifier(fieldDeclaration.modifiers));

		field.setDocComment(getJavaDoc(fieldDeclaration.javadoc,
				scope.referenceCompilationUnit()));

		context.enter(field, fieldDeclaration);

		if (fieldDeclaration.annotations != null) {
			int annotationsLength = fieldDeclaration.annotations.length;
			for (int i = 0; i < annotationsLength; i++)
				fieldDeclaration.annotations[i].traverse(this, scope);
		}

		if (fieldDeclaration.initialization != null)
			fieldDeclaration.initialization.traverse(this, scope);
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(FieldReference fieldReference, BlockScope scope) {
		CtFieldAccess<?> acc = factory.Core().createFieldAccess();
		acc.setVariable(references.getVariableReference(fieldReference.binding));
		acc.setType(references.getTypeReference(fieldReference.resolvedType));

		// Hmmm Maybe this should not be commented, but I cannot see why we need
		// it.
		// Anyway, the problem is that in jdt-core 3.5+ fieldReferences no
		// longer have a receiverType,
		// As far as I can tell, this if makes sure that pika.length if pika is
		// an array, gets the correct type.
		// if anything, I guess that the jdt-core now does not think that length
		// is a field... so we wouldn;t need this anymore.

		// if (fieldReference.receiverType instanceof ArrayBinding
		// && new String(fieldReference.token).equals("length")) {
		// acc.getVariable().setDeclaringType(
		// references.getTypeReference(fieldReference.receiverType));
		// }
		context.enter(acc, fieldReference);

		context.target.push(acc);
		fieldReference.receiver.traverse(this, scope);
		context.target.pop();
		return false;
	}

	@Override
	public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {
		CtLiteral<Float> l = factory.Core().createLiteral();
		l.setValue(floatLiteral.constant.floatValue());
		context.enter(l, floatLiteral);
		return true;
	}

	@Override
	public boolean visit(ForeachStatement forStatement, BlockScope scope) {
		CtForEach fe = factory.Core().createForEach();
		context.enter(fe, forStatement);
		return true;
	}

	@Override
	public boolean visit(ForStatement forStatement, BlockScope scope) {
		CtFor for1 = factory.Core().createFor();
		context.enter(for1, forStatement);

		if (forStatement.initializations != null) {
			context.forinit = true;
			int initializationsLength = forStatement.initializations.length;
			for (int i = 0; i < initializationsLength; i++)
				forStatement.initializations[i].traverse(this, scope);
			context.forinit = false;
		}
		if (forStatement.condition != null)
			forStatement.condition.traverse(this, scope);

		if (forStatement.increments != null) {
			context.forupdate = true;
			int incrementsLength = forStatement.increments.length;
			for (int i = 0; i < incrementsLength; i++)
				forStatement.increments[i].traverse(this, scope);
			context.forupdate = false;
		}
		if (forStatement.action != null)
			forStatement.action.traverse(this, scope);

		return false;
	}

	@Override
	public boolean visit(IfStatement ifStatement, BlockScope scope) {
		CtIf ifs = factory.Core().createIf();
		context.enter(ifs, ifStatement);
		return super.visit(ifStatement, scope);
	}

	@Override
	public boolean visit(Initializer initializer, MethodScope scope) {
		CtAnonymousExecutable b = factory.Core().createAnonymousExecutable();
		if (initializer.isStatic())
			b.getModifiers().add(ModifierKind.STATIC);
		context.enter(b, initializer);
		return true;
	}

	@Override
	public boolean visit(InstanceOfExpression instanceOfExpression,
			BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(BinaryOperatorKind.INSTANCEOF);
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references.getBoundedTypeReference(instanceOfExpression.type.resolvedType));
		op.setRightHandOperand(l);
		context.enter(op, instanceOfExpression);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(IntLiteral intLiteral, BlockScope scope) {
		CtLiteral<Integer> l = factory.Core().createLiteral();
		l.setType(references.getTypeReference(intLiteral.resolvedType));
		l.setValue(intLiteral.value);
		context.enter(l, intLiteral);
		return true;
	}

	@Override
	public boolean visit(LabeledStatement labeledStatement, BlockScope scope) {
		context.label.push(new String(labeledStatement.label));
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
		CtLocalVariable<?> v = factory.Core().createLocalVariable();
		v.setSimpleName(new String(localDeclaration.name));
		v.setType(references
				.getTypeReference(localDeclaration.type.resolvedType));
		v.setModifiers(getModifier(localDeclaration.modifiers));
		context.enter(v, localDeclaration);

		if (localDeclaration.initialization != null) {
			context.arguments.push(v);
			localDeclaration.initialization.traverse(this, scope);
			context.arguments.pop();
		}

		if (localDeclaration.annotations != null)
			for (Annotation a : localDeclaration.annotations)
				a.traverse(this, scope);

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(LongLiteral longLiteral, BlockScope scope) {
		CtLiteral<Long> l = factory.Core().createLiteral();
		l.setValue(longLiteral.constant.longValue());
		l.setType(references.getTypeReference(longLiteral.resolvedType));
		context.enter(l, longLiteral);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MarkerAnnotation annotation, BlockScope scope) {
		CtAnnotation a = factory.Core().createAnnotation();
		a.setAnnotationType(references
				.getTypeReference(annotation.resolvedType));
		context.enter(a, annotation);
		skipTypeInAnnotation = true;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MemberValuePair pair, BlockScope scope) {
		context.annotationValueName.push(new String(pair.name));
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MessageSend messageSend, BlockScope scope) {
		CtInvocation<?> inv = factory.Core().createInvocation();
		if (messageSend.binding != null)
			inv.setExecutable(references
					.getExecutableReference(messageSend.binding));
		// inv
		// .setType(references
		// .getTypeReference(messageSend.binding.returnType));
		context.enter(inv, messageSend);
		if (!(messageSend.receiver.getClass().equals(ThisReference.class)))
			messageSend.receiver.traverse(this, scope);
		context.pushArgument(inv);
		if (messageSend.arguments != null)
			for (Expression e : messageSend.arguments) {
				e.traverse(this, scope);
			}
		if (messageSend.genericTypeArguments != null)
			inv.setGenericTypes(references.getBoundedTypesReferences(messageSend.genericTypeArguments));
		context.popArgument(inv);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		CtMethod<?> m = factory.Core().createMethod();
		m.setSimpleName(new String(methodDeclaration.selector));
		m.setType(references
				.getTypeReference(methodDeclaration.returnType.resolvedType));
		m.setModifiers(getModifier(methodDeclaration.modifiers));
		if (methodDeclaration.thrownExceptions != null) {
			for (TypeReference r : methodDeclaration.thrownExceptions)
				m.getThrownTypes().add(
						references.getTypeReference(r.resolvedType));
		}
		for (TypeBinding b : methodDeclaration.binding.typeVariables) {
			m.getFormalTypeParameters().add(
					references.getBoundedTypeReference(b));
		}

		m.setDocComment(getJavaDoc(methodDeclaration.javadoc,
				scope.referenceCompilationUnit()));

		context.enter(m, methodDeclaration);

		if (methodDeclaration.annotations != null)
			for (Annotation a : methodDeclaration.annotations)
				a.traverse(this, methodDeclaration.scope);

		if (methodDeclaration.arguments != null)
			for (Argument a : methodDeclaration.arguments)
				a.traverse(this, methodDeclaration.scope);

		// Create block
		if (!methodDeclaration.isAbstract()
				&& (methodDeclaration.modifiers & ClassFileConstants.AccNative) == 0) {
			CtBlock<?> b = factory.Core().createBlock();
			context.enter(b, methodDeclaration);
		}

		if (methodDeclaration.statements != null) {
			for (Statement s : methodDeclaration.statements)
				s.traverse(this, methodDeclaration.scope);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(NormalAnnotation annotation, BlockScope scope) {
		CtAnnotation a = factory.Core().createAnnotation();
		a.setAnnotationType(references
				.getTypeReference(annotation.resolvedType));
		context.enter(a, annotation);
		skipTypeInAnnotation = true;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(NullLiteral nullLiteral, BlockScope scope) {
		CtLiteral<?> lit = factory.Core().createLiteral();
		CtTypeReference ref = factory.Core().createTypeReference();
		ref.setSimpleName(CtTypeReference.NULL_TYPE_NAME);
		lit.setType(ref);
		context.enter(lit, nullLiteral);
		return true;
	}

	@Override
	public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		CtBinaryOperator<?> op = factory.Core().createBinaryOperator();
		op.setKind(getBinaryOperatorKind((or_or_Expression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, or_or_Expression);
		return true;
	}

	@Override
	public boolean visit(
			ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference,
			ClassScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references
				.getBoundedTypeReference(parameterizedQualifiedTypeReference.resolvedType));
		context.enter(l, parameterizedQualifiedTypeReference);
		return true;
	}

	@Override
	public boolean visit(
			ParameterizedSingleTypeReference parameterizedSingleTypeReference,
			BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references
				.getBoundedTypeReference(parameterizedSingleTypeReference.resolvedType));
		context.enter(l, parameterizedSingleTypeReference);
		return true;
	}

	@Override
	public boolean visit(
			ParameterizedSingleTypeReference parameterizedSingleTypeReference,
			ClassScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references
				.getBoundedTypeReference(parameterizedSingleTypeReference.resolvedType));
		context.enter(l, parameterizedSingleTypeReference);
		return super.visit(parameterizedSingleTypeReference, scope);
	}

	@Override
	public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		if (postfixExpression.operator == OperatorIds.PLUS)
			op.setKind(UnaryOperatorKind.POSTINC);
		if (postfixExpression.operator == OperatorIds.MINUS)
			op.setKind(UnaryOperatorKind.POSTDEC);
		context.enter(op, postfixExpression);
		return true;
	}

	@Override
	public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		if (prefixExpression.operator == OperatorIds.PLUS)
			op.setKind(UnaryOperatorKind.PREINC);
		if (prefixExpression.operator == OperatorIds.MINUS)
			op.setKind(UnaryOperatorKind.PREDEC);
		context.enter(op, prefixExpression);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(
			QualifiedAllocationExpression qualifiedAllocationExpression,
			BlockScope scope) {
		boolean ret = visit(
				(AllocationExpression) qualifiedAllocationExpression, scope);
		if (qualifiedAllocationExpression.enclosingInstance != null)
			qualifiedAllocationExpression.enclosingInstance.traverse(this,
					scope);
		if (qualifiedAllocationExpression.anonymousType != null)
			qualifiedAllocationExpression.anonymousType.traverse(this, scope);

		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(QualifiedNameReference qualifiedNameReference,
			BlockScope scope) {
		if (qualifiedNameReference.binding instanceof FieldBinding) {
			CtFieldAccess<?> fa = factory.Core().createFieldAccess();
			fa.setVariable(references
					.getVariableReference(qualifiedNameReference.fieldBinding()));

			if (qualifiedNameReference.otherBindings != null)
				for (FieldBinding b : qualifiedNameReference.otherBindings) {
					if (b != null) {
						CtFieldAccess other = factory.Core()
								.createFieldAccess();
						other.setVariable(references.getVariableReference(b));
						other.setTarget(fa);
						fa.setParent(other);
						fa = other;
					}
				}
			context.enter(fa, qualifiedNameReference);
			return true;
		} else if (qualifiedNameReference.binding instanceof VariableBinding) {
			CtVariableAccess va = factory.Core().createVariableAccess();
			va.setVariable(references
					.getVariableReference((VariableBinding) qualifiedNameReference.binding));
			va.setType(va.getVariable().getType());
			if (qualifiedNameReference.otherBindings != null) {
				for (FieldBinding b : qualifiedNameReference.otherBindings) {
					CtFieldAccess fa = factory.Core().createFieldAccess();
					fa.setTarget(va);
					fa.setVariable(references.getVariableReference(b));
					fa.setType(references
							.getTypeReference(qualifiedNameReference.resolvedType));
					va.setParent(fa);
					va = fa;
				}
			}
			context.enter(va, qualifiedNameReference);
			return false;
		}
		return false;
	}

	@Override
	public boolean visit(QualifiedThisReference qualifiedThisReference,
			BlockScope scope) {
		return visit((ThisReference) qualifiedThisReference, scope);
	}

	@Override
	public boolean visit(QualifiedTypeReference arg0, BlockScope arg1) {
		if (skipTypeInAnnotation) {
			return true;
		}
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references.getTypeReference(arg0.resolvedType));
		context.enter(l, arg0);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(ReturnStatement returnStatement, BlockScope scope) {
		CtReturn<?> ret = factory.Core().createReturn();
		context.enter(ret, returnStatement);
		return true;
	}

	boolean skipTypeInAnnotation = false;

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SingleMemberAnnotation annotation, BlockScope scope) {
		CtAnnotation a = factory.Core().createAnnotation();
		a.setAnnotationType(references
				.getTypeReference(annotation.resolvedType));
		context.enter(a, annotation);
		context.annotationValueName.push("value");
		skipTypeInAnnotation = true;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SingleNameReference singleNameReference,
			BlockScope scope) {
		CtVariableAccess<?> va = null;
		if (singleNameReference.binding instanceof FieldBinding) {
			va = factory.Core().createFieldAccess();
			va.setVariable(references.getVariableReference(singleNameReference
					.fieldBinding()));
		} else if (singleNameReference.binding instanceof VariableBinding) {
			va = factory.Core().createVariableAccess();
			va.setVariable(references
					.getVariableReference((VariableBinding) singleNameReference.binding));
		}
		if (va != null)
			context.enter(va, singleNameReference);
		return true;
	}
	
	@Override
	public boolean visit(
    		QualifiedSuperReference qualifiedSuperReference,
    		BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		CtTypeReference<?> withoutSuper = references.getTypeReference(qualifiedSuperReference.qualification.resolvedType);
		withoutSuper.setSuperReference(true);
		l.setValue(withoutSuper);
		context.enter(l, qualifiedSuperReference);
		return false;
	}
	
	@Override
	public boolean visit(SingleTypeReference singleTypeReference,
			BlockScope scope) {
		if (skipTypeInAnnotation) {
			return true;
		}
		// if(context.annotationValueName.peek().equals("value")) return true;
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references
				.getTypeReference(singleTypeReference.resolvedType));
		context.enter(l, singleTypeReference);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(SingleTypeReference singleTypeReference,
			ClassScope scope) {
		CtLiteral<CtTypeReference<?>> l = factory.Core().createLiteral();
		l.setValue(references
				.getTypeReference(singleTypeReference.resolvedType));
		context.enter(l, singleTypeReference);
		return true; // do nothing by default, keep traversing
	}

	@Override
	public boolean visit(StringLiteral stringLiteral, BlockScope scope) {
		CtLiteral<String> s = factory.Core().createLiteral();
		// references.getTypeReference(stringLiteral.resolvedType) can be null
		s.setType(factory.Type().createReference(String.class));
		s.setValue(new String(stringLiteral.source()));
		context.enter(s, stringLiteral);
		return true;
	}

	@Override
	public boolean visit(StringLiteralConcatenation literal, BlockScope scope) {
		CtBinaryOperator<String> op = factory.Core().createBinaryOperator();
		op.setKind(BinaryOperatorKind.PLUS);
		context.enter(op, literal);

		List<Expression> exp = new ArrayList<Expression>();
		for (int i = 0; i < literal.counter; i++)
			exp.add(literal.literals[i]);

		createExpression(literal, scope, exp);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperReference superReference, BlockScope scope) {
		CtFieldReference<?> fr = factory.Core().createFieldReference();
		CtTypeReference ref = references
				.getTypeReference(superReference.resolvedType);
		fr.setSimpleName("super");
		fr.setDeclaringType(ref);
		fr.setType(ref);

		CtFieldAccess fa = factory.Core().createFieldAccess();
		fa.setVariable(fr);
		context.enter(fa, superReference);
		return super.visit(superReference, scope);
	}

	@Override
	public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
		CtSwitch<?> s = factory.Core().createSwitch();
		context.enter(s, switchStatement);

		switchStatement.expression.traverse(this, switchStatement.scope);

		if (switchStatement.statements != null) {
			int statementsLength = switchStatement.statements.length;
			for (int i = 0; i < statementsLength; i++) {
				if (switchStatement.statements[i] instanceof CaseStatement) {
					if (context.stack.peek().element instanceof CtCase) {
						context.exit(context.stack.peek().node);
					}
					CaseStatement cas = (CaseStatement) switchStatement.statements[i];

					visit(cas, switchStatement.scope);
				} else {
					switchStatement.statements[i].traverse(this,
							switchStatement.scope);
				}
			}
			if (context.stack.peek().element instanceof CtCase) {
				context.exit(context.stack.peek().node);
			}
		}
		return false;
	}

	@Override
	public boolean visit(SynchronizedStatement synchronizedStatement,
			BlockScope scope) {
		CtSynchronized s = factory.Core().createSynchronized();
		context.enter(s, synchronizedStatement);
		return super.visit(synchronizedStatement, scope);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ThisReference thisReference, BlockScope scope) {
		CtFieldReference fr = factory.Core().createFieldReference();
		CtTypeReference typeref = references
				.getTypeReference(thisReference.resolvedType);
		fr.setDeclaringType(typeref);
		fr.setType(typeref);
		fr.setSimpleName("this");

		CtFieldAccess fa = factory.Core().createFieldAccess();
		fa.setVariable(fr);
		fa.setType(typeref);

		context.enter(fa, thisReference);
		return true;
	}

	public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
		CtThrow t = factory.Core().createThrow();
		context.enter(t, throwStatement);
		return true;
	}

	@Override
	public boolean visit(TrueLiteral trueLiteral, BlockScope scope) {
		CtLiteral<Boolean> l = factory.Core().createLiteral();
		l.setValue(true);
		context.enter(l, trueLiteral);
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(TryStatement tryStatement, BlockScope scope) {
		CtTry t = factory.Core().createTry();
		context.enter(t, tryStatement);
		tryStatement.tryBlock.traverse(this, scope);
		if (tryStatement.catchArguments != null) {
			for (int i = 0; i < tryStatement.catchArguments.length; i++) {
				CtCatch c = factory.Core().createCatch();
				context.enter(c, tryStatement.catchBlocks[i]);
				CtLocalVariable var = factory.Core().createLocalVariable();
				var.setSimpleName(new String(
						tryStatement.catchArguments[i].name));
				var.setType(references
						.getTypeReference(tryStatement.catchArguments[i].binding.type));
				var.setModifiers(getModifier(tryStatement.catchArguments[i].modifiers));
				context.enter(var, tryStatement.catchArguments[i]);
				context.exit(tryStatement.catchArguments[i]);
				tryStatement.catchBlocks[i].traverse(this, scope);
				context.exit(tryStatement.catchBlocks[i]);
			}
		}
		if (tryStatement.finallyBlock != null) {
			context.finallyzer.push(t);
			tryStatement.finallyBlock.traverse(this, scope);
			context.finallyzer.pop();
		}
		return false;
	}

	@Override
	public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
		CtSimpleType<?> t = createType(localTypeDeclaration);
		t.setDocComment(getJavaDoc(localTypeDeclaration.javadoc,
				scope.referenceCompilationUnit()));
		context.enter(t, localTypeDeclaration);

		// AST bug HACK (see TypeDeclaration.traverse)
		if (localTypeDeclaration.fields != null) {
			int length = localTypeDeclaration.fields.length;
			for (int i = 0; i < length; i++) {
				FieldDeclaration field;
				if ((field = localTypeDeclaration.fields[i]).isStatic()) {
					// local type actually can have static fields
					field.traverse(this, localTypeDeclaration.initializerScope);
				}
			}
		}

		return true;
	}

	@Override
	public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		CtSimpleType<?> type = createType(memberTypeDeclaration);
		type.setDocComment(getJavaDoc(memberTypeDeclaration.javadoc,
				scope.referenceCompilationUnit()));
		context.enter(type, memberTypeDeclaration);

		// AST bug HACK
		if (memberTypeDeclaration.annotations != null)
			for (Annotation a : memberTypeDeclaration.annotations) {
				a.traverse(this, (BlockScope) null);
			}

		return true;
	}

	@Override
	public boolean visit(TypeDeclaration typeDeclaration,
			CompilationUnitScope scope) {

		if (new String(typeDeclaration.name).equals("package-info")) {
			CtPackage pack = factory.Package()
					.getOrCreate(
							new String(typeDeclaration.binding.fPackage
									.readableName()));
			context.enter(pack, typeDeclaration);

			// AST bug HACK
			if (typeDeclaration.annotations != null)
				for (Annotation a : typeDeclaration.annotations) {
					a.traverse(this, (BlockScope) null);
				}
			return true;
		} else {
			CtSimpleType<?> type = createType(typeDeclaration);

			type.setDocComment(getJavaDoc(typeDeclaration.javadoc,
					scope.referenceContext));

			CtPackage pack = null;
			if (!template) {

				if (typeDeclaration.binding.fPackage.shortReadableName() != null
						&& typeDeclaration.binding.fPackage.shortReadableName().length > 0) {
					pack = factory.Package().getOrCreate(
							new String(typeDeclaration.binding.fPackage
									.shortReadableName()));
				} else {
					pack = factory.Package().getOrCreate(
							CtPackage.TOP_LEVEL_PACKAGE_NAME);
				}
				context.enter(pack, typeDeclaration);
			} else {
				char[] packname = typeDeclaration.binding.fPackage
						.shortReadableName();
				if (packname.length > 0) {
					pack = factory.Core().createPackage();
					pack.setSimpleName(new String(packname));
					type.setParent(pack);
					context.enter(pack, typeDeclaration);
				}
			}
			context.compilationunitdeclaration = scope.referenceContext;
			context.enter(type, typeDeclaration);

			// AST bug HACK
			if (typeDeclaration.annotations != null)
				for (Annotation a : typeDeclaration.annotations) {
					a.traverse(this, (BlockScope) null);
				}

			if (typeDeclaration.memberTypes != null) {
				int length = typeDeclaration.memberTypes.length;
				for (int i = 0; i < length; i++)
					typeDeclaration.memberTypes[i].traverse(this,
							typeDeclaration.scope);
			}
			if (typeDeclaration.fields != null) {
				int length = typeDeclaration.fields.length;
				for (int i = 0; i < length; i++) {
					FieldDeclaration field;
					if ((field = typeDeclaration.fields[i]).isStatic()) {
						field.traverse(this,
								typeDeclaration.staticInitializerScope);
					} else {
						field.traverse(this, typeDeclaration.initializerScope);
					}
				}
			}
			if (typeDeclaration.methods != null) {
				int length = typeDeclaration.methods.length;
				for (int i = 0; i < length; i++)
					typeDeclaration.methods[i].traverse(this,
							typeDeclaration.scope);
			}
			return false;
		}
	}

	@Override
	public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {
		CtUnaryOperator<?> op = factory.Core().createUnaryOperator();
		op.setKind(getUnaryOperator((unaryExpression.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT));
		context.enter(op, unaryExpression);
		return true;
	}

	@Override
	public boolean visit(WhileStatement whileStatement, BlockScope scope) {
		CtWhile w = factory.Core().createWhile();
		context.enter(w, whileStatement);
		return true;
	}

}