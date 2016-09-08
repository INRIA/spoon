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

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getModifiers;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.isLhsAssignment;

class JDTTreeBuilderHelper {
	private final JDTTreeBuilder jdtTreeBuilder;

	JDTTreeBuilderHelper(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	/**
	 * Computes the anonymous simple name from its fully qualified type name.
	 *
	 * @param anonymousQualifiedName
	 * 		Qualified name which contains the anonymous name.
	 * @return Anonymous simple name.
	 */
	static String computeAnonymousName(char[] anonymousQualifiedName) {
		final String poolName = CharOperation.charToString(anonymousQualifiedName);
		return poolName.substring(poolName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR) + 1, poolName.length());
	}

	/**
	 * Creates a qualified type name from a two-dimensional array.
	 *
	 * @param typeName
	 * 		two-dimensional array which represents the qualified name expected.
	 * @return Qualified name.
	 */
	static String createQualifiedTypeName(char[][] typeName) {
		String s = "";
		for (int i = 0; i < typeName.length - 1; i++) {
			s += CharOperation.charToString(typeName[i]) + ".";
		}
		s += CharOperation.charToString(typeName[typeName.length - 1]);
		return s;
	}

	/**
	 * Creates a catch variable from a type reference.
	 *
	 * @param typeReference
	 * 		Correspond to the exception type declared in the catch.
	 * @return a catch variable.
	 */
	CtCatchVariable<Throwable> createCatchVariable(TypeReference typeReference) {
		final Argument jdtCatch = (Argument) jdtTreeBuilder.getContextBuilder().stack.peekFirst().node;
		final Set<ModifierKind> modifiers = getModifiers(jdtCatch.modifiers);
		return jdtTreeBuilder.getFactory().Code().createCatchVariable(//
				jdtTreeBuilder.getReferencesBuilder().<Throwable>getTypeReference(typeReference.resolvedType), //
				CharOperation.charToString(jdtCatch.name), //
				modifiers.toArray(new ModifierKind[modifiers.size()]));
	}

	/**
	 * Creates variable access from a {@link CtVariableReference}. Think to move this method
	 * in the {@link spoon.reflect.factory.CodeFactory} if you think that is a good idea.
	 */
	public <T> CtVariableAccess<T> createVariableAccess(CtVariableReference<T> variableReference, boolean isReadAccess) {
		CtVariableAccess<T> variableAccess;
		if (isReadAccess) {
			variableAccess = jdtTreeBuilder.getFactory().Core().createVariableWrite();
		} else {
			variableAccess = jdtTreeBuilder.getFactory().Core().createVariableRead();
		}
		return variableAccess.setVariable(variableReference);
	}

	/**
	 * Creates a variable access from its single name.
	 *
	 * @param singleNameReference
	 * 		Used to build a variable reference which will be contained in the variable access.
	 * @return a variable access.
	 */
	<T> CtVariableAccess<T> createVariableAccess(SingleNameReference singleNameReference) {
		CtVariableAccess<T> va;
		if (isLhsAssignment(jdtTreeBuilder.getContextBuilder(), singleNameReference)) {
			va = jdtTreeBuilder.getFactory().Core().createVariableWrite();
		} else {
			va = jdtTreeBuilder.getFactory().Core().createVariableRead();
		}
		va.setVariable(jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference((VariableBinding) singleNameReference.binding));
		return va;
	}

	/**
	 * Analyzes if {@code singleNameReference} points to a {@link CtVariable} visible in current
	 * scope and, if existent, returns its corresponding {@link CtVariableAccess}. Returns
	 * {@code null} if {@code singleNameReference} could not be resolved as variable access. Since
	 * we are in noclasspath mode this function may also returns {@code null} if
	 * {@code singleNameReference} points to a variable declared by an unknown class.
	 *
	 * @param singleNameReference
	 * 		The potential variable access.
	 * @return A {@link CtVariableAccess} if {@code singleNameReference} points to a variable
	 * 		   visible in current scope, {@code null} otherwise.
	 */
	<T> CtVariableAccess<T> createVariableAccessNoClasspath(SingleNameReference singleNameReference) {
		final CoreFactory coreFactory = jdtTreeBuilder.getFactory().Core();
		final ExecutableFactory executableFactory = jdtTreeBuilder.getFactory().Executable();
		final ContextBuilder contextBuilder = jdtTreeBuilder.getContextBuilder();
		final ReferenceBuilder referenceBuilder = jdtTreeBuilder.getReferencesBuilder();
		final PositionBuilder positionBuilder = jdtTreeBuilder.getPositionBuilder();

		final String name = CharOperation.charToString(singleNameReference.token);
		final CtVariable<T> variable = contextBuilder.getVariableDeclaration(name);
		if (variable == null) {
			return null;
		}

		final CtVariableReference<T> variableReference;
		final CtVariableAccess<T> variableAccess;
		if (variable instanceof CtParameter) {
			// create variable of concrete type to avoid type casting while calling methods
			final CtParameterReference<T> parameterReference = coreFactory.createParameterReference();
			if (variable.getParent() instanceof CtLambda) {
				parameterReference.setDeclaringExecutable(
						referenceBuilder.getLambdaExecutableReference(singleNameReference));
			} else {
				// Unfortunately, we can not use `variable.getReference()` here as some parent
				// references (in terms of Java objects) have not been set up yet. Thus, we need to
				// create the required parameter reference by our own.

				// since the given parameter has not been declared in a lambda expression it must
				// have been declared by a method!
				final CtMethod method = (CtMethod) variable.getParent();

				// create list of method's parameter types
				final List<CtTypeReference<?>> parameterTypesOfMethod = new ArrayList<>();
				final List<CtParameter<?>> parametersOfMethod = method.getParameters();
				for (CtParameter<?> parameter : parametersOfMethod) {
					if (parameter.getType() != null) {
						parameterTypesOfMethod.add(parameter.getType().clone());
					}
				}

				// find method's corresponding jdt element
				MethodDeclaration methodJDT = null;
				for (final ASTPair astPair : contextBuilder.stack) {
					if (astPair.element == method) {
						methodJDT = (MethodDeclaration) astPair.node;
						break;
					}
				}
				assert methodJDT != null;

				// create a reference to method's declaring class
				final CtTypeReference declaringReferenceOfMethod =
						// `binding` may be null for anonymous classes which means we have to
						// create an 'empty' type reference since we have no further information
						methodJDT.binding == null ? coreFactory.createTypeReference()
								: referenceBuilder.getTypeReference(methodJDT.binding.declaringClass);

				// create a reference to the method of the currently processed parameter reference
				final CtExecutableReference methodReference =
						executableFactory.createReference(declaringReferenceOfMethod,
								// we need to clone method's return type (rt) before passing to
								// `createReference` since this method (indirectly) sets the parent
								// of the rt and, therefore, may break the AST
								method.getType().clone(),
								// no need to clone/copy as Strings are immutable
								method.getSimpleName(),
								// no need to clone/copy as we just created this object
								parameterTypesOfMethod);

				// finally, we can set the method reference...
				parameterReference.setDeclaringExecutable(methodReference);
			}
			variableReference = parameterReference;
			variableAccess = isLhsAssignment(contextBuilder, singleNameReference)
					? coreFactory.<T>createVariableWrite() : coreFactory.<T>createVariableRead();
		} else if (variable instanceof CtField) {
			variableReference = variable.getReference();
			variableAccess = isLhsAssignment(contextBuilder, singleNameReference)
					? coreFactory.<T>createFieldWrite() : coreFactory.<T>createFieldRead();
		} else { // CtLocalVariable, CtCatchVariable, ...
			variableReference = variable.getReference();
			variableAccess = isLhsAssignment(contextBuilder, singleNameReference)
					? coreFactory.<T>createVariableWrite() : coreFactory.<T>createVariableRead();
		}
		variableReference.setSimpleName(name);
		variableReference.setPosition(positionBuilder.buildPosition(
				singleNameReference.sourceStart(), singleNameReference.sourceEnd()));
		variableAccess.setVariable(variableReference);
		return variableAccess;
	}

	/**
	 * Creates a variable or a field access from its qualified name.
	 *
	 * @param qualifiedNameReference
	 * 		Used to build the variable access. See all sub methods of this class to understand its usage.
	 * @return a variable access.
	 */
	<T> CtVariableAccess<T> createVariableAccess(QualifiedNameReference qualifiedNameReference) {
		long[] positions = qualifiedNameReference.sourcePositions;
		int sourceStart = qualifiedNameReference.sourceStart();
		int sourceEnd = qualifiedNameReference.sourceEnd();
		if (qualifiedNameReference.indexOfFirstFieldBinding < positions.length) {
			sourceEnd = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding] >>> 32) - 2;
		}
		CtVariableAccess<T> va;
		CtVariableReference<T> ref;
		boolean fromAssignment = isLhsAssignment(jdtTreeBuilder.getContextBuilder(), qualifiedNameReference);
		boolean isOtherBinding = qualifiedNameReference.otherBindings == null || qualifiedNameReference.otherBindings.length == 0;
		if (qualifiedNameReference.binding instanceof FieldBinding) {
			ref = jdtTreeBuilder.getReferencesBuilder().getVariableReference(qualifiedNameReference.fieldBinding());
			ref.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));

			va = createFieldAccess(ref, createTargetFieldAccess(qualifiedNameReference, (CtFieldReference<Object>) ref), isOtherBinding && fromAssignment);
		} else {
			ref = jdtTreeBuilder.getReferencesBuilder().getVariableReference((VariableBinding) qualifiedNameReference.binding);
			ref.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));

			va = createVariableAccess(ref, isOtherBinding && fromAssignment);
		}

		if (qualifiedNameReference.otherBindings != null) {
			int i = 0; //positions index;
			va.setPosition(ref.getPosition());
			sourceStart = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding - 1] >>> 32);
			for (FieldBinding b : qualifiedNameReference.otherBindings) {
				isOtherBinding = qualifiedNameReference.otherBindings.length == i + 1;
				CtFieldAccess<T> other = createFieldAccess(//
						jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference(b, qualifiedNameReference.tokens[i + 1]), va, isOtherBinding && fromAssignment);
				//set source position of fa
				if (i + qualifiedNameReference.indexOfFirstFieldBinding >= qualifiedNameReference.otherBindings.length) {
					sourceEnd = qualifiedNameReference.sourceEnd();
				} else {
					sourceEnd = (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding + i + 1] >>> 32) - 2;
				}
				other.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
				va = other;
				i++;
			}
		} else if (!(qualifiedNameReference.binding instanceof FieldBinding) && qualifiedNameReference.tokens.length > 1) {
			sourceStart = (int) (positions[0] >>> 32);
			for (int i = 1; i < qualifiedNameReference.tokens.length; i++) {
				isOtherBinding = qualifiedNameReference.tokens.length == i + 1;
				CtFieldAccess<T> other = createFieldAccess(//
						jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference(null, qualifiedNameReference.tokens[i]), va, isOtherBinding && fromAssignment);
				//set source position of va;
				CompilationUnit cu = jdtTreeBuilder.getFactory().CompilationUnit().create(new String(jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.getFileName()));
				sourceEnd = (int) (positions[i]);
				final int[] lineSeparatorPositions = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.compilationResult.lineSeparatorPositions;
				va.setPosition(jdtTreeBuilder.getFactory().Core().createSourcePosition(cu, sourceStart, sourceStart, sourceEnd, lineSeparatorPositions));
				va = other;
			}
		}
		va.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(qualifiedNameReference.sourceStart(), qualifiedNameReference.sourceEnd()));
		return va;
	}

	/**
	 * Creates variable access from a {@link CtVariableReference}. Think to move this method
	 * in the {@link spoon.reflect.factory.CodeFactory} if you think that is a good idea.
	 */
	public <T> CtFieldAccess<T> createFieldAccess(CtVariableReference<T> variableReference, CtExpression<?> target, boolean isReadAccess) {
		CtFieldAccess<T> fieldAccess;
		if (isReadAccess) {
			fieldAccess = jdtTreeBuilder.getFactory().Core().createFieldWrite();
		} else {
			fieldAccess = jdtTreeBuilder.getFactory().Core().createFieldRead();
		}
		fieldAccess.setVariable(variableReference);
		fieldAccess.setTarget(target);
		return fieldAccess;
	}

	/**
	 * Creates a field access from its single name.
	 *
	 * @param singleNameReference
	 * 		Used to build a variable reference and a target which will be contained in the field access.
	 * @return a field access.
	 */
	<T> CtFieldAccess<T> createFieldAccess(SingleNameReference singleNameReference) {
		CtFieldAccess<T> va;
		if (isLhsAssignment(jdtTreeBuilder.getContextBuilder(), singleNameReference)) {
			va = jdtTreeBuilder.getFactory().Core().createFieldWrite();
		} else {
			va = jdtTreeBuilder.getFactory().Core().createFieldRead();
		}
		va.setVariable(jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference(singleNameReference.fieldBinding().original()));
		if (va.getVariable() != null) {
			final CtFieldReference<T> ref = va.getVariable();
			if (ref.isStatic() && !ref.getDeclaringType().isAnonymous()) {
				va.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(ref.getDeclaringType()));
			} else if (!ref.isStatic()) {
				va.setTarget(jdtTreeBuilder.getFactory().Code().createThisAccess(jdtTreeBuilder.getReferencesBuilder().getTypeReference(singleNameReference.actualReceiverType), true));
			}
		}
		return va;
	}

	/**
	 * In no classpath mode, when we build a field access, we have a binding typed by ProblemBinding.
	 * This binding doesn't contain all information but we can get some of them.
	 *
	 * @param singleNameReference
	 * 		Used to get the problem binding of the field access and the name of the declaring type.
	 * @return a field access.
	 */
	<T> CtFieldAccess<T> createFieldAccessNoClasspath(SingleNameReference singleNameReference) {
		CtFieldAccess<T> va;
		if (isLhsAssignment(jdtTreeBuilder.getContextBuilder(), singleNameReference)) {
			va = jdtTreeBuilder.getFactory().Core().createFieldWrite();
		} else {
			va = jdtTreeBuilder.getFactory().Core().createFieldRead();
		}
		va.setVariable(jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference((ProblemBinding) singleNameReference.binding));
		final CtReference declaring = jdtTreeBuilder.getReferencesBuilder().getDeclaringReferenceFromImports(singleNameReference.token);
		if (declaring instanceof CtTypeReference && va.getVariable() != null) {
			final CtTypeReference<Object> declaringRef = (CtTypeReference<Object>) declaring;
			va.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(declaringRef));
			va.getVariable().setDeclaringType(declaringRef);
			va.getVariable().setStatic(true);
		}
		return va;
	}

	/**
	 * In no classpath mode, when we build a field access, we have a binding typed by ProblemBinding.
	 * We try to get all information we can get from this binding.
	 *
	 * @param qualifiedNameReference
	 * 		Used to get the problem binding of the field access and the name of the declaring type.
	 * @return a field access.
	 */
	<T> CtFieldAccess<T> createFieldAccessNoClasspath(QualifiedNameReference qualifiedNameReference) {
		boolean fromAssignment = isLhsAssignment(jdtTreeBuilder.getContextBuilder(), qualifiedNameReference);
		CtFieldAccess<T> fieldAccess = createFieldAccess(jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference((ProblemBinding) qualifiedNameReference.binding), null, fromAssignment);
		// In no classpath mode and with qualified name, the type given by JDT is wrong...
		final char[][] declaringClass = CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1);
		final MissingTypeBinding declaringType = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createMissingType(null, declaringClass);
		final CtTypeReference<T> declaringRef = jdtTreeBuilder.getReferencesBuilder().getTypeReference(declaringType);
		fieldAccess.getVariable().setDeclaringType(declaringRef);
		fieldAccess.getVariable().setStatic(true);
		fieldAccess.setTarget(jdtTreeBuilder.getFactory().Code().createTypeAccess(declaringRef));
		// In no classpath mode and with qualified name, the binding don't have a good name.
		fieldAccess.getVariable()
				.setSimpleName(createQualifiedTypeName(CharOperation.subarray(qualifiedNameReference.tokens, qualifiedNameReference.tokens.length - 1, qualifiedNameReference.tokens.length)));
		return fieldAccess;
	}

	/**
	 * Creates a field access from a field reference.
	 *
	 * @param fieldReference
	 * 		Used to build the spoon variable reference and the type of the field access.
	 * @return a field access.
	 */
	<T> CtFieldAccess<T> createFieldAccess(FieldReference fieldReference) {
		CtFieldAccess<T> fieldAccess;
		if (isLhsAssignment(jdtTreeBuilder.getContextBuilder(), fieldReference)) {
			fieldAccess = jdtTreeBuilder.getFactory().Core().createFieldWrite();
		} else {
			fieldAccess = jdtTreeBuilder.getFactory().Core().createFieldRead();
		}
		fieldAccess.setVariable(jdtTreeBuilder.getReferencesBuilder().<T>getVariableReference(fieldReference.binding, fieldReference.token));
		fieldAccess.setType(jdtTreeBuilder.getReferencesBuilder().<T>getTypeReference(fieldReference.resolvedType));
		return fieldAccess;
	}

	/**
	 * Creates a type access from its qualified name and with a field reference.
	 *
	 * @param qualifiedNameReference
	 * 		Used to update the field reference if necessary.
	 * @param fieldReference
	 * 		Used to get its declaring class and to put it in the type access.
	 * @return a type access.
	 */
	CtTypeAccess<?> createTypeAccess(QualifiedNameReference qualifiedNameReference, CtFieldReference<?> fieldReference) {
		final TypeBinding receiverType = qualifiedNameReference.actualReceiverType;
		if (receiverType != null) {
			final CtTypeReference<Object> qualifiedRef = jdtTreeBuilder.getReferencesBuilder().getQualifiedTypeReference(//
					qualifiedNameReference.tokens, receiverType, qualifiedNameReference.fieldBinding().declaringClass.enclosingType(), new JDTTreeBuilder.OnAccessListener() {
						@Override
						public boolean onAccess(char[][] tokens, int index) {
							return !CharOperation.equals(tokens[index + 1], tokens[tokens.length - 1]);
						}
					});
			if (qualifiedRef != null) {
				fieldReference.setDeclaringType(qualifiedRef);
			} else {
				fieldReference.setDeclaringType(jdtTreeBuilder.getReferencesBuilder().getTypeReference(receiverType));
			}
		}

		CtTypeAccess<?> typeAccess = jdtTreeBuilder.getFactory().Code().createTypeAccess(fieldReference.getDeclaringType());
		if (qualifiedNameReference.indexOfFirstFieldBinding > 1) {
			// the array sourcePositions contains the position of each element of the qualifiedNameReference
			// the last element contains the position of the field
			long[] positions = qualifiedNameReference.sourcePositions;
			typeAccess.setPosition(
					jdtTreeBuilder.getPositionBuilder().buildPosition(qualifiedNameReference.sourceStart(), (int) (positions[qualifiedNameReference.indexOfFirstFieldBinding - 1] >>> 32) - 2));
		} else {
			typeAccess.setImplicit(qualifiedNameReference.isImplicitThis());
		}

		return typeAccess;
	}

	/**
	 * Creates a type access from its qualified name.
	 *
	 * @param qualifiedNameReference
	 * 		Used to get the declaring class of this type. This qualified type should have a type as target.
	 * @return a type access.
	 */
	<T> CtTypeAccess<T> createTypeAccessNoClasspath(QualifiedNameReference qualifiedNameReference) {
		CtTypeReference<T> typeReference;
		if (qualifiedNameReference.binding instanceof ProblemBinding) {
			typeReference = jdtTreeBuilder.getFactory().Type().<T>createReference(CharOperation.toString(qualifiedNameReference.tokens));
		} else if (qualifiedNameReference.binding instanceof FieldBinding) {
			final ReferenceBinding declaringClass = ((FieldBinding) qualifiedNameReference.binding).declaringClass;
			typeReference = jdtTreeBuilder.getReferencesBuilder().<T>getTypeReference(declaringClass);
		} else {
			// TODO try to determine package/class boundary by upper case
			char[][] packageName = CharOperation.subarray(qualifiedNameReference.tokens, 0, qualifiedNameReference.tokens.length - 1);
			char[][] className = CharOperation.subarray(qualifiedNameReference.tokens, qualifiedNameReference.tokens.length - 1, qualifiedNameReference.tokens.length);
			if (packageName.length > 0) {
				final PackageBinding aPackage = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createPackage(packageName);
				final MissingTypeBinding declaringType = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createMissingType(aPackage, className);

				typeReference = jdtTreeBuilder.getReferencesBuilder().getTypeReference(declaringType);
			} else {
				typeReference = jdtTreeBuilder.getFactory().Type().createReference(qualifiedNameReference.toString());
			}
		}
		final CtTypeAccess<T> typeAccess = jdtTreeBuilder.getFactory().Code().createTypeAccess(typeReference);

		int sourceStart = qualifiedNameReference.sourceStart();
		int sourceEnd = qualifiedNameReference.sourceEnd();
		typeAccess.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));

		return typeAccess;
	}

	/**
	 * Creates a type access from its single name.
	 *
	 * @param singleNameReference
	 * 		Used to get the simple name of the type.
	 * @return a type access.
	 */
	<T> CtTypeAccess<T> createTypeAccessNoClasspath(SingleNameReference singleNameReference) {
		final CtTypeReference<T> typeReference = jdtTreeBuilder.getFactory().Core().createTypeReference();
		if (singleNameReference.binding == null) {
			typeReference.setSimpleName(CharOperation.charToString(singleNameReference.token));
		} else {
			typeReference.setSimpleName(CharOperation.charToString(singleNameReference.binding.readableName()));
		}
		jdtTreeBuilder.getReferencesBuilder().setPackageOrDeclaringType(typeReference, jdtTreeBuilder.getReferencesBuilder().getDeclaringReferenceFromImports(singleNameReference.token));
		return jdtTreeBuilder.getFactory().Code().createTypeAccess(typeReference);
	}

	/**
	 * Creates the target of a field access.
	 *
	 * @param qualifiedNameReference
	 * 		Used to get the declaring class of the target.
	 * @param ref
	 * 		Used in a static context.
	 * @return an expression.
	 */
	CtExpression<?> createTargetFieldAccess(QualifiedNameReference qualifiedNameReference, CtFieldReference<Object> ref) {
		CtExpression<?> target = null;
		if (JDTTreeBuilderQuery.isValidProblemBindingField(qualifiedNameReference)) {
			target = createTypeAccessNoClasspath(qualifiedNameReference);
		} else if (ref.isStatic()) {
			target = createTypeAccess(qualifiedNameReference, ref);
		} else if (!ref.isStatic() && !ref.getDeclaringType().isAnonymous()) {
			target = jdtTreeBuilder.getFactory().Code().createThisAccess(jdtTreeBuilder.getReferencesBuilder().<Object>getTypeReference(qualifiedNameReference.actualReceiverType), true);
		}
		return target;
	}

	/**
	 * Creates a parameter. If the argument have a type == null, we get the type from its binding. A type == null is possible when
	 * this type is implicit like in a lambda where you don't need to specify the type of parameters.
	 *
	 * @param argument
	 * 		Used to get the name of the parameter, the modifiers, know if it is a var args parameter.
	 * @return a parameter.
	 */
	<T> CtParameter<T> createParameter(Argument argument) {
		CtParameter<T> p = jdtTreeBuilder.getFactory().Core().createParameter();
		p.setSimpleName(CharOperation.charToString(argument.name));
		p.setVarArgs(argument.isVarArgs());
		p.setModifiers(getModifiers(argument.modifiers));
		if (argument.binding != null && argument.binding.type != null && argument.type == null) {
			p.setType(jdtTreeBuilder.getReferencesBuilder().<T>getTypeReference(argument.binding.type));
			p.getType().setImplicit(argument.type == null);
			if (p.getType() instanceof CtArrayTypeReference) {
				((CtArrayTypeReference) p.getType()).getComponentType().setImplicit(argument.type == null);
			}
		}
		return p;
	}

	/**
	 * Creates an executable reference expression.
	 *
	 * @param referenceExpression
	 * 		Used to get the executable reference.
	 * @return an executable reference expression.
	 */
	<T, E extends CtExpression<?>> CtExecutableReferenceExpression<T, E> createExecutableReferenceExpression(ReferenceExpression referenceExpression) {
		CtExecutableReferenceExpression<T, E> executableRef = jdtTreeBuilder.getFactory().Core().createExecutableReferenceExpression();
		CtExecutableReference<T> executableReference = jdtTreeBuilder.getReferencesBuilder().getExecutableReference(referenceExpression.binding);
		if (executableReference == null) {
			// No classpath mode.
			executableReference = jdtTreeBuilder.getFactory().Core().createExecutableReference();
			executableReference.setSimpleName(CharOperation.charToString(referenceExpression.selector));
			executableReference.setDeclaringType(jdtTreeBuilder.getReferencesBuilder().getTypeReference(referenceExpression.lhs.resolvedType));
		}
		final CtTypeReference<T> declaringType = (CtTypeReference<T>) executableReference.getDeclaringType();
		executableReference.setType(declaringType == null ? null : declaringType.clone());
		executableRef.setExecutable(executableReference);
		return executableRef;
	}

	/**
	 * Creates a class, an enum, an interface or a annotation type.
	 *
	 * @return a type.
	 */
	CtType<?> createType(TypeDeclaration typeDeclaration) {
		CtType<?> type;
		if ((typeDeclaration.modifiers & ClassFileConstants.AccAnnotation) != 0) {
			type = jdtTreeBuilder.getFactory().Core().<java.lang.annotation.Annotation>createAnnotationType();
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccEnum) != 0) {
			type = jdtTreeBuilder.getFactory().Core().createEnum();
		} else if ((typeDeclaration.modifiers & ClassFileConstants.AccInterface) != 0) {
			type = jdtTreeBuilder.getFactory().Core().createInterface();
		} else {
			type = jdtTreeBuilder.getFactory().Core().createClass();
		}
		jdtTreeBuilder.getContextBuilder().enter(type, typeDeclaration);

		if (typeDeclaration.superInterfaces != null) {
			for (TypeReference ref : typeDeclaration.superInterfaces) {
				final CtTypeReference superInterface = jdtTreeBuilder.references.buildTypeReference(ref, null);
				type.addSuperInterface(superInterface);
			}
		}

		if (type instanceof CtClass) {
			if (typeDeclaration.superclass != null && typeDeclaration.superclass.resolvedType != null && typeDeclaration.enclosingType != null && !new String(
					typeDeclaration.superclass.resolvedType.qualifiedPackageName()).equals(new String(typeDeclaration.binding.qualifiedPackageName()))) {

				// Sorry for this hack but see the test case ImportTest#testImportOfAnInnerClassInASuperClassPackage.
				// JDT isn't smart enough to return me a super class available. So, I modify their AST when
				// superclasses aren't in the same package and when their visibilities are "default".
				List<ModifierKind> modifiers = Arrays.asList(ModifierKind.PUBLIC, ModifierKind.PROTECTED);
				final TypeBinding resolvedType = typeDeclaration.superclass.resolvedType;
				if ((resolvedType instanceof MemberTypeBinding || resolvedType instanceof BinaryTypeBinding)//
						&& resolvedType.enclosingType() != null && typeDeclaration.enclosingType.superclass != null//
						&& Collections.disjoint(modifiers, getModifiers(resolvedType.enclosingType().modifiers))) {
					typeDeclaration.superclass.resolvedType = jdtTreeBuilder.new SpoonReferenceBinding(typeDeclaration.superclass.resolvedType.sourceName(),
							(ReferenceBinding) typeDeclaration.enclosingType.superclass.resolvedType);
				}
			}
			if (typeDeclaration.superclass != null) {
				((CtClass) type).setSuperclass(jdtTreeBuilder.references.buildTypeReference(typeDeclaration.superclass, typeDeclaration.scope));
			}
			if (typeDeclaration.binding.isAnonymousType() || (typeDeclaration.binding instanceof LocalTypeBinding && typeDeclaration.binding.enclosingMethod() != null)) {
				type.setSimpleName(computeAnonymousName(typeDeclaration.binding.constantPoolName()));
			} else {
				type.setSimpleName(new String(typeDeclaration.name));
			}
		} else {
			type.setSimpleName(new String(typeDeclaration.name));
		}

		// Setting modifiers
		type.setModifiers(getModifiers(typeDeclaration.modifiers));

		return type;
	}
}
