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
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CASTS_CONTAINER_DEFAULT_CAPACITY;
import static java.lang.String.format;

public class ContextBuilder {

	Deque<String> annotationValueName = new ArrayDeque<>();

	List<CtTypeReference<?>> casts = new ArrayList<>(CASTS_CONTAINER_DEFAULT_CAPACITY);

	CompilationUnitDeclaration compilationunitdeclaration;

	CompilationUnit compilationUnitSpoon;

	Deque<String> label = new ArrayDeque<>();

	boolean isBuildLambda = false;

	boolean ignoreComputeImports = false;

	/**
	 * Stack of all parents elements
	 */
	Deque<ASTPair> stack = new ArrayDeque<>();

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

	@SuppressWarnings("unchecked")
	<T> CtLocalVariable<T> getLocalVariableDeclaration(final String name) {
		final Class<CtLocalVariable<T>> clazz = (Class<CtLocalVariable<T>>)
				jdtTreeBuilder.getFactory().Core().createLocalVariable().getClass();
		final CtLocalVariable<T> localVariable =
				this.<T, CtLocalVariable<T>>getVariableDeclaration(name, clazz);
		if (localVariable == null) {
			// note: this happens when using the new try(vardelc) structure
			this.jdtTreeBuilder.getLogger().error(
					format("Could not find declaration for local variable %s at %s",
							name, stack.peek().element.getPosition()));
		}
		return localVariable;
	}

	@SuppressWarnings("unchecked")
	<T> CtCatchVariable<T> getCatchVariableDeclaration(final String name) {
		final Class<CtCatchVariable<T>> clazz = (Class<CtCatchVariable<T>>)
				jdtTreeBuilder.getFactory().Core().createCatchVariable().getClass();
		final CtCatchVariable<T> catchVariable =
				this.<T, CtCatchVariable<T>>getVariableDeclaration(name, clazz);
		if (catchVariable == null) {
			// note: this happens when using the new try(vardelc) structure
			this.jdtTreeBuilder.getLogger().error(
					format("Could not find declaration for catch variable %s at %s",
							name, stack.peek().element.getPosition()));
		}
		return catchVariable;
	}

	<T> CtVariable<T> getVariableDeclaration(final String name) {
		final CtVariable<T> variable = this.<T, CtVariable<T>>getVariableDeclaration(name, null);
		if (variable == null) {
			// note: this happens when using the new try(vardelc) structure
			this.jdtTreeBuilder.getLogger().error(
					format("Could not find declaration for variable %s at %s",
							name, stack.peek().element.getPosition()));
		}
		return variable;
	}

	private <T, U extends CtVariable<T>> U getVariableDeclaration(
			final String name, final Class<U> clazz) {
		final CoreFactory coreFactory = jdtTreeBuilder.getFactory().Core();
		final ReferenceBuilder referenceBuilder = jdtTreeBuilder.getReferencesBuilder();
		// there is some extra work to do if we are looking for CtFields (and subclasses)
		final boolean lookingForFields = clazz == null
				|| coreFactory.createField().getClass().isAssignableFrom(clazz);

		// try to find the variable on stack beginning with the most recent element
		for (final ASTPair astPair : stack) {
			// the variable may have been declared directly by one of these elements
			final List<U> variables = astPair.element.getElements(new AbstractFilter<U>() {
				@Override
				public boolean matches(final U element) {
					if (name.equals(element.getSimpleName())) {
						final U castedElement = (U) element;
						for (CtElement parent = castedElement.getParent();
								parent != null; parent = parent.getParent()) {
							if (astPair.element.equals(parent)) {
								return true;
							}
						}
					}
					return false;
				}

				@Override
				public Class<U> getType() {
					return clazz != null ? clazz : super.getType();
				}
			});
			if (!variables.isEmpty()) {
				return variables.get(0);
			}

			// the variable may have been declared in a super class/interface
			if (lookingForFields && astPair.node instanceof TypeDeclaration) {
				final TypeDeclaration nodeDeclaration = (TypeDeclaration) astPair.node;
				final Deque<ReferenceBinding> tds = new ArrayDeque<>();
				// add super class if any
				if (nodeDeclaration.superclass != null
						&& nodeDeclaration.superclass.resolvedType instanceof ReferenceBinding) {
					tds.add((ReferenceBinding) nodeDeclaration.superclass.resolvedType);
				}
				// add interfaces if any
				if (nodeDeclaration.superInterfaces != null) {
					for (final TypeReference tr : nodeDeclaration.superInterfaces) {
						if (tr.resolvedType instanceof ReferenceBinding) {
							tds.add((ReferenceBinding) tr.resolvedType);
						}
					}
				}

				while (!tds.isEmpty()) {
					final ReferenceBinding rb = tds.pop();
					for (final FieldBinding fb : rb.fields()) {
						if (name.equals(new String(fb.readableName()))) {
							// extract TypeDeclaration to create a CtField from it.
							break;
						}
					}
				}
			}
		}

		// the variable may have been imported statically from another class/interface
		if (lookingForFields) {
			final CtReference potentialReferenceToField =
					referenceBuilder.getDeclaringReferenceFromImports(name.toCharArray());
			if (potentialReferenceToField != null) {
				final CtElement potentialField = potentialReferenceToField.getDeclaration();
				if (potentialField instanceof CtField) {
					return (U) potentialField;
				}
			}
		}

		return null;
	}
}
