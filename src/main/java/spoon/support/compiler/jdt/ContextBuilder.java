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
import spoon.compiler.Environment;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.InterfaceFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.support.SpoonClassNotFoundException;

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

	@SuppressWarnings("unchecked")
	private <T, U extends CtVariable<T>> U getVariableDeclaration(
			final String name, final Class<U> clazz) {
		final CoreFactory coreFactory = jdtTreeBuilder.getFactory().Core();
		final TypeFactory typeFactory = jdtTreeBuilder.getFactory().Type();
		final ClassFactory classFactory = jdtTreeBuilder.getFactory().Class();
		final InterfaceFactory interfaceFactory = jdtTreeBuilder.getFactory().Interface();
		final FieldFactory fieldFactory = jdtTreeBuilder.getFactory().Field();
		final ReferenceBuilder referenceBuilder = jdtTreeBuilder.getReferencesBuilder();
		final Environment environment = jdtTreeBuilder.getFactory().getEnvironment();
		// there is some extra work to do if we are looking for CtFields (and subclasses)
		final boolean lookingForFields = clazz == null
				|| coreFactory.createField().getClass().isAssignableFrom(clazz);

		// try to find the variable on stack beginning with the most recent element
		for (final ASTPair astPair : stack) {
			// the variable may have been declared directly by one of these elements
			final ScopeRespectingVariableScanner<U> scanner =
					new ScopeRespectingVariableScanner(name, clazz);
			astPair.element.accept(scanner);
			if (scanner.getResult() != null) {
				return scanner.getResult();
			}

			// the variable may have been declared in a super class/interface
			if (lookingForFields && astPair.node instanceof TypeDeclaration) {
				final TypeDeclaration nodeDeclaration = (TypeDeclaration) astPair.node;
				final Deque<ReferenceBinding> referenceBindings = new ArrayDeque<>();
				// add super class if any
				if (nodeDeclaration.superclass != null
						&& nodeDeclaration.superclass.resolvedType instanceof ReferenceBinding) {
					referenceBindings.push((ReferenceBinding) nodeDeclaration.superclass.resolvedType);
				}
				// add interfaces if any
				if (nodeDeclaration.superInterfaces != null) {
					for (final TypeReference tr : nodeDeclaration.superInterfaces) {
						if (tr.resolvedType instanceof ReferenceBinding) {
							referenceBindings.push((ReferenceBinding) tr.resolvedType);
						}
					}
				}

				while (!referenceBindings.isEmpty()) {
					final ReferenceBinding referenceBinding = referenceBindings.pop();
					for (final FieldBinding fieldBinding : referenceBinding.fields()) {
						if (name.equals(new String(fieldBinding.readableName()))) {
							final String qualifiedNameOfParent =
									new String(referenceBinding.readableName());
							final CtType parentOfField = referenceBinding.isClass()
									? classFactory.create(qualifiedNameOfParent)
									: interfaceFactory.create(qualifiedNameOfParent);
							return (U) fieldFactory.create(parentOfField,
									JDTTreeBuilderQuery.getModifiers(fieldBinding.modifiers),
									referenceBuilder.getTypeReference(fieldBinding.type),
									name);
						}
					}
					// add super class if any
					final ReferenceBinding superclass = referenceBinding.superclass();
					if (superclass != null) {
						referenceBindings.push(superclass);
					}
					// add interfaces if any
					final ReferenceBinding[] interfaces = referenceBinding.superInterfaces();
					if (interfaces != null) {
						for (ReferenceBinding rb : interfaces) {
							referenceBindings.push(rb);
						}
					}
				}
			}
		}

		// the variable may have been imported statically from another class/interface
		if (lookingForFields) {
			final CtReference potentialReferenceToField =
					referenceBuilder.getDeclaringReferenceFromImports(name.toCharArray());
			if (potentialReferenceToField != null
					&& potentialReferenceToField instanceof CtTypeReference) {
				final CtTypeReference typeReference = (CtTypeReference) potentialReferenceToField;
				try {
					final Class classOfType = typeReference.getActualClass();
					if (classOfType != null) {
						final CtType declaringTypeOfField = typeReference.isInterface()
								? interfaceFactory.get(classOfType) : classFactory.get(classOfType);
						final CtField field = declaringTypeOfField.getField(name);
						if (field != null) {
							return (U) field;
						}
					}
				} catch (final SpoonClassNotFoundException scnfe) {
					// in noclasspath mode we do some heuristics to determine if `name` could be a
					// field that has been imported statically from another class (or interface).
					if (environment.getNoClasspath()) {
						// if `potentialReferenceToField` is a `CtTypeReference` then `name` must
						// have been imported statically. Otherwise, `potentialReferenceToField`
						// would be a CtPackageReference!

						// if `name` consists only of upper case characters separated by '_', we
						// assume a constant value according to JLS.
						if (name.toUpperCase().equals(name)) {
							final CtType parentOfField =
									classFactory.create(typeReference.getQualifiedName());
							// it is the best thing we can do
							final CtField field = coreFactory.createField();
							field.setParent(parentOfField);
							field.setSimpleName(name);
							// it is the best thing we can do
							field.setType(typeFactory.nullType());
							return (U) field;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * An {@link EarlyTerminatingScanner} that is supposed to find a {@link CtVariable} with
	 * specific name respecting the current scope given by {@link ContextBuilder#stack}.

	 * @param <T> The actual type of the {@link CtVariable} we are looking for. Examples include
	 *            {@link CtLocalVariable}, {@link CtField}, and so on.
	 */
	private class ScopeRespectingVariableScanner<T extends CtVariable>
			extends EarlyTerminatingScanner<T> {

		/**
		 * The class object of {@link T} that is required to filter particular elements in
		 * {@link #scan(CtElement)}.
		 */
		private final Class<T> clazz;

		/**
		 * The name of the variable we are looking for ({@link CtVariable#getSimpleName()}).
		 */
		final String name;

		/**
		 * Creates a new {@link EarlyTerminatingScanner} that tries to find a {@link CtVariable}
		 * with name {@code pName} (using {@link CtVariable#getSimpleName()}) and upper type bound
		 * {@code pType}.
		 *
		 * @param pName	The name of the variable we are looking for.
		 * @param pType	{@link T}'s class object ({@link Object#getClass()}). {@link null} values
		 *              are permitted and indicate that we are looking for any subclass of
		 *              {@link CtVariable} (including {@link CtVariable} itself).
		 */
		ScopeRespectingVariableScanner(final String pName, final Class<T> pType) {
			clazz =  (Class<T>) (pType == null ? CtVariable.class : pType);
			name = pName;
		}

		@Override
		public void scan(final CtElement element) {
			if (element != null && clazz.isAssignableFrom(element.getClass())) {
				final T potentialVariable = (T) element;
				if (name.equals(potentialVariable.getSimpleName())) {
					// Since the AST is not completely available yet, we can not check if element's
					// parent (ep) contains the innermost element of `stack` (ie). Therefore, we
					// have to check if one of the following condition holds:
					//
					//    1) Does `stack` contain `ep`?
					//    2) Is `ep` the body of one of `stack`'s CtExecutable elements?
					//
					// The first condition is easy to see. If `stack` contains `ep` then `ep` and
					// all it's declared variables are in scope of `ie`. Unfortunately, there is a
					// special case in which a variable (a CtLocalVariable) has been declared in a
					// block (CtBlock) of, for instance, a method. Such a block is not contained in
					// `stack`. This peculiarity calls for the second condition.
					final CtElement parentOfPotentialVariable = potentialVariable.getParent();
					for (final ASTPair astPair : stack) {
						if (astPair.element == parentOfPotentialVariable) {
							finish(potentialVariable);
							return;
						} else if (astPair.element instanceof CtExecutable) {
							final CtExecutable executable = (CtExecutable) astPair.element;
							if (executable.getBody() == parentOfPotentialVariable) {
								finish(potentialVariable);
								return;
							}
						}
					}
				}
			}
			super.scan(element);
		}

		private void finish(final  T element) {
			setResult(element);
			terminate();
		}
	}
}
