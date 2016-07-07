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
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;

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

	Deque<CtTry> finallyzer = new ArrayDeque<>();

	boolean forinit = false;

	boolean forupdate = false;

	boolean assigned = false;

	Deque<String> label = new ArrayDeque<>();

	boolean selector = false;

	boolean isGenericTypeExplicit = true;

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
		// aststack.push(node);
		if (compilationunitdeclaration != null) {
			e.setPosition(this.jdtTreeBuilder.getPosition().buildPositionCtElement(e, node));
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
			if (e instanceof CtTypedElement && node instanceof Expression) {
				if (((CtTypedElement<?>) e).getType() == null) {
					((CtTypedElement<Object>) e).setType(this.jdtTreeBuilder.getReferences().getTypeReference(((Expression) node).resolvedType));
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
			this.jdtTreeBuilder.getExiter().scan(stack.peek().element);
		}
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
