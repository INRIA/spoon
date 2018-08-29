/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
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
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.CtExtendedModifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.getModifiers;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.isLhsAssignment;

public class JDTTreeBuilderHelper {
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
		return poolName.substring(poolName.lastIndexOf(CtType.INNERTTYPE_SEPARATOR) + 1);
	}

	/**
	 * Creates a qualified type name from a two-dimensional array.
	 *
	 * @param typeName
	 * 		two-dimensional array which represents the qualified name expected.
	 * @return Qualified name.
	 */
	static String createQualifiedTypeName(char[][] typeName) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < typeName.length - 1; i++) {
			s.append(CharOperation.charToString(typeName[i])).append(".");
		}
		s.append(CharOperation.charToString(typeName[typeName.length - 1]));
		return s.toString();
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
		final Set<CtExtendedModifier> modifiers = getModifiers(jdtCatch.modifiers, false, false);

		CtCatchVariable<Throwable> result = jdtTreeBuilder.getFactory().Core().createCatchVariable();
		result.<CtCatchVariable>setSimpleName(CharOperation.charToString(jdtCatch.name)).setExtendedModifiers(modifiers);
		if (typeReference instanceof UnionTypeReference) {
			//do not set type of variable yet. It will be initialized later by visit of multiple types. Each call then ADDs one type
			return result;
		} else {
			CtTypeReference ctTypeReference = jdtTreeBuilder.getReferencesBuilder().<Throwable>getTypeReference(typeReference.resolvedType);
			return result.<CtCatchVariable>setType(ctTypeReference);
		}
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
		final TypeFactory typeFactory = jdtTreeBuilder.getFactory().Type();
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
				// nothing

			} else {
				// Unfortunately, we can not use `variable.getReference()` here as some parent
				// references (in terms of Java objects) have not been set up yet. Thus, we need to
				// create the required parameter reference by our own.

				// Since the given parameter has not been declared in a lambda expression it must
				// have been declared by a method/constructor.
				final CtExecutable executable = (CtExecutable) variable.getParent();

				// find executable's corresponding jdt element
				AbstractMethodDeclaration executableJDT = null;
				for (final ASTPair astPair : contextBuilder.stack) {
					if (astPair.element == executable) {
						executableJDT = (AbstractMethodDeclaration) astPair.node;
					}
				}
				assert executableJDT != null;

				// create a reference to executable's declaring class
				final CtTypeReference declaringReferenceOfExecutable =
						// `binding` may be null for anonymous classes which means we have to
						// create an 'empty' type reference since we have no further information
						// available
						executableJDT.binding == null ? coreFactory.createTypeReference()
								: referenceBuilder.getTypeReference(
										executableJDT.binding.declaringClass);

				// If executable is a constructor, `executable.getType()` returns null since the
				// parent is not available yet. Fortunately, however, the return type of a
				// constructor is its declaring class which, in our case, is already available with
				// declaringReferenceOfExecutable.
				CtTypeReference executableTypeReference = executable instanceof CtConstructor
						// IMPORTANT: Create a clone of the type reference (rt) if retrieved by
						// other AST elements as `executableFactory.createReference` (see below)
						// indirectly sets the parent of `rt` and, thus, may break the AST!
						? declaringReferenceOfExecutable.clone()
						: executable.getType().clone();

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

		ref.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));

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
				sourceEnd = (int) (positions[i]);
				va.setPosition(jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
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
			final CtTypeReference<Object> qualifiedRef = jdtTreeBuilder.getReferencesBuilder().getQualifiedTypeReference(
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
				try {
					final PackageBinding aPackage = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createPackage(packageName);
					final MissingTypeBinding declaringType = jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createMissingType(aPackage, className);

					typeReference = jdtTreeBuilder.getReferencesBuilder().getTypeReference(declaringType);
				} catch (NullPointerException e) {
					typeReference = jdtTreeBuilder.getFactory().Type().createReference(qualifiedNameReference.toString());
				}
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
		p.setExtendedModifiers(getModifiers(argument.modifiers, false, false));
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

		// Setting modifiers
		type.setExtendedModifiers(getModifiers(typeDeclaration.modifiers, false, false));

		jdtTreeBuilder.getContextBuilder().enter(type, typeDeclaration);

		if (typeDeclaration.superInterfaces != null) {
			for (TypeReference ref : typeDeclaration.superInterfaces) {
				final CtTypeReference superInterface = jdtTreeBuilder.references.buildTypeReference(ref, null);
				type.addSuperInterface(superInterface);
			}
		}

		if (type instanceof CtClass) {
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

		return type;
	}

	/**
	 * Creates an entire object CtModule from a module declaration.
	 * @return a CtModule
	 */
	CtModule createModule(ModuleDeclaration moduleDeclaration) {
		CtModule module = jdtTreeBuilder.getFactory().Module().getOrCreate(new String(moduleDeclaration.moduleName));
		module.setIsOpenModule(moduleDeclaration.isOpen());
		jdtTreeBuilder.getContextBuilder().enter(module, moduleDeclaration);

		if (moduleDeclaration.requires != null && moduleDeclaration.requires.length > 0) {
			List<CtModuleRequirement> moduleRequirements = new ArrayList<>();
			for (RequiresStatement requiresStatement : moduleDeclaration.requires) {
				moduleRequirements.add(this.createModuleRequirement(requiresStatement));
			}

			module.setRequiredModules(moduleRequirements);
		}

		if (moduleDeclaration.exports != null && moduleDeclaration.exports.length > 0) {
			List<CtPackageExport> moduleExports = new ArrayList<>();
			for (ExportsStatement exportsStatement : moduleDeclaration.exports) {
				moduleExports.add(this.createModuleExport(exportsStatement));
			}

			module.setExportedPackages(moduleExports);
		}

		if (moduleDeclaration.opens != null && moduleDeclaration.opens.length > 0) {
			List<CtPackageExport> moduleOpens = new ArrayList<>();
			for (OpensStatement opensStatement : moduleDeclaration.opens) {
				moduleOpens.add(this.createModuleExport(opensStatement));
			}

			module.setOpenedPackages(moduleOpens);
		}

		if (moduleDeclaration.uses != null && moduleDeclaration.uses.length > 0) {
			List<CtUsedService> consumedServices = new ArrayList<>();
			for (UsesStatement consumedService : moduleDeclaration.uses) {
				consumedServices.add(this.createUsedService(consumedService));
			}

			module.setUsedServices(consumedServices);
		}

		if (moduleDeclaration.services != null && moduleDeclaration.services.length > 0) {
			List<CtProvidedService> moduleProvidedServices = new ArrayList<>();
			for (ProvidesStatement providesStatement : moduleDeclaration.services) {
				moduleProvidedServices.add(this.createModuleProvidedService(providesStatement));
			}

			module.setProvidedServices(moduleProvidedServices);
		}
		module.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(moduleDeclaration.declarationSourceStart, moduleDeclaration.declarationSourceEnd));
		return module;
	}

	CtUsedService createUsedService(UsesStatement usesStatement) {
		CtTypeReference typeReference = this.jdtTreeBuilder.references.getTypeReference(usesStatement.serviceInterface);
		CtUsedService usedService = this.jdtTreeBuilder.getFactory().Module().createUsedService(typeReference);
		usedService.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(usesStatement.sourceStart, usesStatement.sourceEnd));
		return usedService;
	}

	CtModuleRequirement createModuleRequirement(RequiresStatement requiresStatement) {
		int sourceStart = requiresStatement.sourceStart;
		int sourceEnd = requiresStatement.sourceEnd;

		CtModuleReference ctModuleReference = jdtTreeBuilder.references.getModuleReference(requiresStatement.module);
		CtModuleRequirement moduleRequirement = jdtTreeBuilder.getFactory().Module().createModuleRequirement(ctModuleReference);

		Set<CtModuleRequirement.RequiresModifier> modifiers = new HashSet<>();
		if (requiresStatement.isStatic()) {
			modifiers.add(CtModuleRequirement.RequiresModifier.STATIC);
		}
		if (requiresStatement.isTransitive()) {
			modifiers.add(CtModuleRequirement.RequiresModifier.TRANSITIVE);
		}
		moduleRequirement.setRequiresModifiers(modifiers);
		moduleRequirement.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
		return moduleRequirement;
	}

	CtPackageExport createModuleExport(ExportsStatement exportsStatement) {
		String packageName = new String(exportsStatement.pkgName);
		int sourceStart = exportsStatement.sourceStart;
		int sourceEnd = exportsStatement.sourceEnd;

		CtPackageReference ctPackageReference = jdtTreeBuilder.references.getPackageReference(packageName);
		CtPackageExport moduleExport = jdtTreeBuilder.getFactory().Module().createPackageExport(ctPackageReference);

		if (exportsStatement.targets != null && exportsStatement.targets.length > 0) {
			List<CtModuleReference> moduleReferences = new ArrayList<>();

			for (ModuleReference moduleReference : exportsStatement.targets) {
				moduleReferences.add(this.jdtTreeBuilder.references.getModuleReference(moduleReference));
			}

			moduleExport.setTargetExport(moduleReferences);
		}

		moduleExport.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
		return moduleExport;
	}

	CtPackageExport createModuleExport(OpensStatement opensStatement) {
		String packageName = new String(opensStatement.pkgName);
		int sourceStart = opensStatement.sourceStart;
		int sourceEnd = opensStatement.sourceEnd;

		CtPackageReference ctPackageReference = jdtTreeBuilder.references.getPackageReference(packageName);
		CtPackageExport moduleExport = jdtTreeBuilder.getFactory().Module().createPackageExport(ctPackageReference);

		if (opensStatement.targets != null && opensStatement.targets.length > 0) {
			List<CtModuleReference> moduleReferences = new ArrayList<>();

			for (ModuleReference moduleReference : opensStatement.targets) {
				moduleReferences.add(this.jdtTreeBuilder.references.getModuleReference(moduleReference));
			}

			moduleExport.setTargetExport(moduleReferences);
		}

		moduleExport.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
		return moduleExport;
	}

	CtProvidedService createModuleProvidedService(ProvidesStatement providesStatement) {
		int sourceStart = providesStatement.sourceStart;
		int sourceEnd = providesStatement.sourceEnd;

		CtTypeReference provideService = this.jdtTreeBuilder.references.getTypeReference(providesStatement.serviceInterface);
		List<CtTypeReference> implementations = new ArrayList<>();

		for (TypeReference typeReference : providesStatement.implementations) {
			implementations.add(this.jdtTreeBuilder.references.getTypeReference(typeReference));
		}

		CtProvidedService providedService = this.jdtTreeBuilder.getFactory().Module().createProvidedService(provideService);
		providedService.setImplementationTypes(implementations);
		providedService.setPosition(this.jdtTreeBuilder.getPositionBuilder().buildPosition(sourceStart, sourceEnd));
		return providedService;
	}
}
