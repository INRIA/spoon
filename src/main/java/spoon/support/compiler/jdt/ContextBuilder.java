/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.EarlyTerminatingScanner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASTS_CONTAINER_DEFAULT_CAPACITY;

public class ContextBuilder {

	Deque<String> annotationValueName = new ArrayDeque<>();

	Deque<CtElement> arguments = new ArrayDeque<>();

	List<CtTypeReference<?>> casts = new ArrayList<>(CASTS_CONTAINER_DEFAULT_CAPACITY);

	CompilationUnitDeclaration compilationunitdeclaration;

	CompilationUnit compilationUnitSpoon;

	boolean forinit = false;

	boolean forupdate = false;

	boolean assigned = false;

	Deque<String> label = new ArrayDeque<>();

	boolean isBuildLambda = false;

	boolean isLambdaParameterImplicitlyTyped = true;

	boolean ignoreComputeImports = false;

	boolean isTypeParameter = false;

	/**
	 * Stack of all parents elements
	 */
	Deque<ASTPair> stack = new ArrayDeque<>();

	Deque<CtTargetedExpression<?, ?>> target = new ArrayDeque<>();

	private final JDTTreeBuilder jdtTreeBuilder;

	ContextBuilder(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	@SuppressWarnings("unchecked")
	void enter(CtElement e, ASTNode node) {
		stack.push(new ASTPair(e, node));
		if (!(e instanceof CtPackage) || (compilationUnitSpoon.getFile() != null && compilationUnitSpoon.getFile().getName().equals(DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION))) {
			if (compilationunitdeclaration != null) {
				e.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPositionCtElement(e, node));
			}
		}

		ASTPair pair = stack.peek();
		CtElement current = pair.element;

		if (current instanceof CtExpression) {
			while (!casts.isEmpty()) {
				((CtExpression<?>) current).addTypeCast(casts.remove(0));
			}
		}
		if (current instanceof CtStatement && !this.label.isEmpty()) {
			((CtStatement) current).setLabel(this.label.pop());
		}

		try {
			if (e instanceof CtTypedElement && !(e instanceof CtConstructorCall) && node instanceof Expression) {
				if (((CtTypedElement<?>) e).getType() == null) {
					((CtTypedElement<Object>) e).setType(this.jdtTreeBuilder.getReferencesBuilder().getTypeReference(((Expression) node).resolvedType));
				}
			}
		} catch (UnsupportedOperationException ignore) {
			// For some element, we throw an UnsupportedOperationException when we call setType().
		}

	}

	void exit(ASTNode node) {
		ASTPair pair = stack.pop();
		if (pair.node != node) {
			throw new RuntimeException("Inconsistent Stack " + node + "\n" + pair.node);
		}
		CtElement current = pair.element;
		if (!stack.isEmpty()) {
			this.jdtTreeBuilder.getExiter().setChild(current);
			this.jdtTreeBuilder.getExiter().setChild(pair.node);
			this.jdtTreeBuilder.getExiter().scan(stack.peek().element);
		}
	}

	<T> CtLocalVariable<T> getLocalVariableDeclaration(final String name) {
		for (ASTPair astPair : this.stack) {
			// TODO check if the variable is visible from here

			EarlyTerminatingScanner<CtLocalVariable<?>> scanner = new EarlyTerminatingScanner<CtLocalVariable<?>>() {
				@Override
				public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
					if (name.equals(localVariable.getSimpleName())) {
						setResult(localVariable);
						terminate();
						return;
					}
					super.visitCtLocalVariable(localVariable);
				}
			};
			astPair.element.accept(scanner);
			CtLocalVariable<T> var = (CtLocalVariable<T>) scanner.getResult();
			if (var != null) {
				return var;
			}
		}
		// note: this happens when using the new try(vardelc) structure
		this.jdtTreeBuilder.getLogger().error("could not find declaration for local variable " + name + " at " + this.stack.peek().element.getPosition());

		return null;
	}

	<T> CtCatchVariable<T> getCatchVariableDeclaration(final String name) {
		for (ASTPair astPair : this.stack) {
			EarlyTerminatingScanner<CtCatchVariable<?>> scanner = new EarlyTerminatingScanner<CtCatchVariable<?>>() {
				@Override
				public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
					if (name.equals(catchVariable.getSimpleName())) {
						setResult(catchVariable);
						terminate();
						return;
					}
					super.visitCtCatchVariable(catchVariable);
				}
			};
			astPair.element.accept(scanner);

			CtCatchVariable<T> var = (CtCatchVariable<T>) scanner.getResult();
			if (var != null) {
				return null;
			}
		}
		// note: this happens when using the new try(vardelc) structure
		this.jdtTreeBuilder.getLogger().error("could not find declaration for catch variable " + name + " at " + this.stack.peek().element.getPosition());

		return null;
	}

	boolean isArgument(CtElement e) {
		return arguments.size() > 0 && arguments.peek() == e;
	}

	void popArgument(CtElement e) {
		if (arguments.pop() != e) {
			throw new RuntimeException("Unconsistant stack");
		}
	}

	void pushArgument(CtElement e) {
		arguments.push(e);
	}

}
