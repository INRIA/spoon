/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VoidTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.PackageFactory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.support.reflect.CtExtendedModifier;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchPackage;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchType;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchTypeBinding;

public class ReferenceBuilder {

	// Allow to detect circular references and to avoid endless recursivity
	// when resolving parameterizedTypes (e.g. Enum<E extends Enum<E>>)
	private Map<TypeBinding, CtTypeReference> exploringParameterizedBindings = new HashMap<>();

	private boolean bounds = false;

	private final JDTTreeBuilder jdtTreeBuilder;

	ReferenceBuilder(JDTTreeBuilder jdtTreeBuilder) {
		this.jdtTreeBuilder = jdtTreeBuilder;
	}

	private CtTypeReference<?> getBoundedTypeReference(TypeBinding binding) {
		bounds = true;
		CtTypeReference<?> ref = getTypeReference(binding);
		bounds = false;
		return ref;
	}

	/**
	 * Builds a type reference from a {@link TypeReference}.
	 *
	 * @param type  Type from JDT.
	 * @param scope Scope of the parent element.
	 * @param <T>   Type of the type reference.
	 * @return a type reference.
	 */
	<T> CtTypeReference<T> buildTypeReference(TypeReference type, Scope scope) {
		return buildTypeReference(type, scope, false);
	}
	<T> CtTypeReference<T> buildTypeReference(TypeReference type, Scope scope, boolean isTypeCast) {
		if (type == null) {
			return null;
		}
		CtTypeReference<T> typeReference = this.<T>getTypeReference(type.resolvedType, type);
		return buildTypeReferenceInternal(typeReference, type, scope, isTypeCast);
	}

	/**
	 * Builds a qualified type reference from a {@link TypeReference}.
	 *
	 * @param type Qualified type from JDT.
	 * @param scope Scope of the parent element.
	 * @return
	 */
	<T> CtTypeReference<T> buildTypeReference(QualifiedTypeReference type, Scope scope) {
		CtTypeReference<T> accessedType = buildTypeReference((TypeReference) type, scope);
		final TypeBinding receiverType = type != null ? type.resolvedType : null;
		if (receiverType != null) {
			final CtTypeReference<T> ref = getQualifiedTypeReference(type.tokens, receiverType, receiverType.enclosingType(), new JDTTreeBuilder.OnAccessListener() {
				@Override
				public boolean onAccess(char[][] tokens, int index) {
					return true;
				}
			});
			if (ref != null) {
				accessedType = ref;
			}
		}
		return accessedType;
	}

	/**
	 * Builds a type parameter reference from a {@link TypeReference}
	 *
	 * @param type  Type from JDT.
	 * @param scope Scope of the parent element.
	 * @return a type parameter reference.
	 */
	private CtTypeParameterReference buildTypeParameterReference(TypeReference type, Scope scope) {
		if (type == null) {
			return null;
		}
		return (CtTypeParameterReference) this.buildTypeReferenceInternal(this.getTypeParameterReference(type.resolvedType, type), type, scope, false);
	}


	private <T> CtTypeReference<T> buildTypeReferenceInternal(CtTypeReference<T> typeReference, TypeReference type, Scope scope, boolean isTypeCast) {
		if (type == null) {
			return null;
		}
		CtTypeReference<?> currentReference = typeReference;

		for (int position = type.getTypeName().length - 1; position >= 0; position--) {
			if (currentReference == null) {
				break;
			}
			this.jdtTreeBuilder.getContextBuilder().isBuildTypeCast = isTypeCast;
			this.jdtTreeBuilder.getContextBuilder().enter(currentReference, type);
			this.jdtTreeBuilder.getContextBuilder().isBuildTypeCast = false;
			if (type.annotations != null && type.annotations.length - 1 <= position && type.annotations[position] != null && type.annotations[position].length > 0) {
				for (Annotation annotation : type.annotations[position]) {
					if (scope instanceof ClassScope) {
						annotation.traverse(this.jdtTreeBuilder, (ClassScope) scope);
					} else if (scope instanceof BlockScope) {
						annotation.traverse(this.jdtTreeBuilder, (BlockScope) scope);
					} else {
						annotation.traverse(this.jdtTreeBuilder, (BlockScope) null);
					}
				}
			}
			if (type.getTypeArguments() != null && type.getTypeArguments().length - 1 <= position && type.getTypeArguments()[position] != null && type.getTypeArguments()[position].length > 0) {
				CtTypeReference<?> componentReference = getTypeReferenceOfArrayComponent(currentReference);
				componentReference.getActualTypeArguments().clear();
				for (TypeReference typeArgument : type.getTypeArguments()[position]) {
					if (typeArgument instanceof Wildcard || typeArgument.resolvedType instanceof WildcardBinding || typeArgument.resolvedType instanceof TypeVariableBinding) {
						componentReference.addActualTypeArgument(buildTypeParameterReference(typeArgument, scope));
					} else {
						componentReference.addActualTypeArgument(buildTypeReference(typeArgument, scope));
					}
				}
			} else if ((type instanceof ParameterizedSingleTypeReference || type instanceof ParameterizedQualifiedTypeReference)
					&& !isTypeArgumentExplicit(type.getTypeArguments())) {
				for (CtTypeReference<?> actualTypeArgument : currentReference.getActualTypeArguments()) {
					actualTypeArgument.setImplicit(true);
					if (actualTypeArgument instanceof CtArrayTypeReference) {
						((CtArrayTypeReference) actualTypeArgument).getComponentType().setImplicit(true);
					}
				}
			}
			if (type instanceof Wildcard && typeReference instanceof CtWildcardReference) {
				((CtWildcardReference) typeReference).setBoundingType(buildTypeReference(((Wildcard) type).bound, scope));
			}
			this.jdtTreeBuilder.getContextBuilder().exit(type);
			currentReference = currentReference.getDeclaringType();
		}
		//detect whether something is implicit
		if (type instanceof SingleTypeReference) {
			typeReference.setSimplyQualified(true);
		} else if (type instanceof QualifiedTypeReference) {
			jdtTreeBuilder.getHelper().handleImplicit((QualifiedTypeReference) type, typeReference);
		}
		return typeReference;
	}

	private CtTypeReference<?> getTypeReferenceOfArrayComponent(CtTypeReference<?> currentReference) {
		while (currentReference instanceof CtArrayTypeReference) {
			currentReference = ((CtArrayTypeReference<?>) currentReference).getComponentType();
		}
		return currentReference;
	}

	private boolean isTypeArgumentExplicit(TypeReference[][] typeArguments) {
		if (typeArguments == null) {
			return true;
		}
		boolean isGenericTypeExplicit = true;
		// This loop is necessary because it is the only way to know if the generic type
		// is implicit or not.
		for (TypeReference[] typeArgument : typeArguments) {
			isGenericTypeExplicit = typeArgument != null && typeArgument.length > 0;
			if (isGenericTypeExplicit) {
				break;
			}
		}
		return isGenericTypeExplicit;
	}

	/**
	 * Builds a type reference from a qualified name when a type specified in the name isn't available.
	 *
	 * @param tokens        Qualified name.
	 * @param receiverType  Last type in the qualified name.
	 * @param enclosingType Enclosing type of the type name.
	 * @param listener      Listener to know if we must build the type reference.
	 * @return a type reference.
	 */
	<T> CtTypeReference<T> getQualifiedTypeReference(char[][] tokens, TypeBinding receiverType, ReferenceBinding enclosingType, JDTTreeBuilder.OnAccessListener listener) {
		final List<CtExtendedModifier> listPublicProtected = Arrays.asList(new CtExtendedModifier(ModifierKind.PUBLIC), new CtExtendedModifier(ModifierKind.PROTECTED));
		if (enclosingType != null && Collections.disjoint(listPublicProtected, JDTTreeBuilderQuery.getModifiers(enclosingType.modifiers, false, false))) {
			String access = "";
			int i = 0;
			final CompilationUnitDeclaration[] units = ((TreeBuilderCompiler) this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.typeRequestor).unitsToProcess;
			for (; i < tokens.length; i++) {
				final char[][] qualified = Arrays.copyOfRange(tokens, 0, i + 1);
				if (searchPackage(qualified, units) == null) {
					access = CharOperation.toString(qualified);
					break;
				}
			}
			if (!access.contains(CtPackage.PACKAGE_SEPARATOR)) {
				access = searchType(access, this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.imports);
			}
			final TypeBinding accessBinding = searchTypeBinding(access, units);
			if (accessBinding != null && listener.onAccess(tokens, i)) {
				final TypeBinding superClassBinding = searchTypeBinding(accessBinding.superclass(), CharOperation.charToString(tokens[i + 1]));
				if (superClassBinding != null) {
					return this.getTypeReference(superClassBinding.clone(accessBinding));
				} else {
					return this.getTypeReference(receiverType);
				}
			} else {
				return this.getTypeReference(receiverType);
			}
		}
		return null;
	}

	/**
	 * Try to get the declaring reference (package or type) from imports of the current
	 * compilation unit declaration (current class). This method returns a CtReference
	 * which can be a CtTypeReference if it retrieves the information in an static import,
	 * a CtPackageReference if it retrieves the information in an standard import, otherwise
	 * it returns null.
	 *
	 * @param expectedName Name expected in imports.
	 * @return CtReference which can be a CtTypeReference, a CtPackageReference or null.
	 */
	CtReference getDeclaringReferenceFromImports(char[] expectedName) {
		CompilationUnitDeclaration cuDeclaration = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration;
		if (cuDeclaration == null) {
			return null;
		}
		LookupEnvironment environment = cuDeclaration.scope.environment;

		if (cuDeclaration.imports != null) {
			for (ImportReference anImport : cuDeclaration.imports) {
				if (CharOperation.equals(anImport.getImportName()[anImport.getImportName().length - 1], expectedName)) {
					if (anImport.isStatic()) {
						int indexDeclaring = 2;
						if ((anImport.bits & ASTNode.OnDemand) != 0) {
							// With .*
							indexDeclaring = 1;
						}
						char[][] packageName = CharOperation.subarray(anImport.getImportName(), 0, anImport.getImportName().length - indexDeclaring);
						char[][] className = CharOperation.subarray(anImport.getImportName(), anImport.getImportName().length - indexDeclaring, anImport.getImportName().length - (indexDeclaring - 1));
						PackageBinding aPackage;
						try {
							if (packageName.length != 0) {
								aPackage = environment.createPackage(packageName);
							} else {
								aPackage = null;
							}
							final MissingTypeBinding declaringType = environment.createMissingType(aPackage, className);
							this.jdtTreeBuilder.getContextBuilder().ignoreComputeImports = true;
							final CtTypeReference<Object> typeReference = getTypeReference(declaringType);
							this.jdtTreeBuilder.getContextBuilder().ignoreComputeImports = false;
							return typeReference;
						} catch (NullPointerException e) {
							return null;
						}

					} else {
						PackageBinding packageBinding = null;
						char[][] chars = CharOperation.subarray(anImport.getImportName(), 0, anImport.getImportName().length - 1);
						// `findImport(chars, false, false);` and `createPackage(chars)` require
						// an array with a minimum length of 1 and throw an
						// ArrayIndexOutOfBoundsException if `chars.length == 0`. Fixes #759.
						if (chars.length > 0) {
							Binding someBinding = cuDeclaration.scope.findImport(chars, false, false);
							if (someBinding != null && someBinding.isValidBinding() && someBinding instanceof PackageBinding) {
								packageBinding = (PackageBinding) someBinding;
							} else {
								try {
									packageBinding = environment.createPackage(chars);
								} catch (NullPointerException e) {
									packageBinding = null;
								}
							}
						}
						if (packageBinding == null || packageBinding instanceof ProblemPackageBinding) {
							// Big crisis here. We are already in noclasspath mode but JDT doesn't support always
							// creation of a package in this mode. So, if we are in this brace, we make the job of JDT...
							packageBinding = new PackageBinding(chars, null, environment, environment.module) {
								// PackageBinding was a class instead of abstract class in earlier jdt versions.
								// To circumvent this change to an abstract class, an anonymous class is used here.
								@Override
								public PlainPackageBinding getIncarnation(ModuleBinding arg0) {
									// this method returns always null, because we dont know the enclosingModule here.
									// Link to original method from PlainPackageBinding:
									// https://github.com/eclipse/eclipse.jdt.core/blob/master/org.eclipse.jdt.core/compiler/org/eclipse/jdt/internal/compiler/lookup/PlainPackageBinding.java#L43
									return null;
								}
							};
													}
						return getPackageReference(packageBinding);
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	<T> CtExecutableReference<T> getExecutableReference(MethodBinding exec) {
		if (exec == null) {
			return null;
		}
		final CtExecutableReference ref = this.jdtTreeBuilder.getFactory().Core().createExecutableReference();
		if (exec.isConstructor()) {
			ref.setSimpleName(CtExecutableReference.CONSTRUCTOR_NAME);

			// in case of constructor of an array, it's the return type that we want
			if (exec.returnType instanceof VoidTypeBinding) {
				ref.setType(getTypeReference(exec.declaringClass, true));
			} else {
				ref.setType(getTypeReference(exec.returnType, true));
			}
		} else {
			ref.setSimpleName(new String(exec.selector));
			ref.setType(getTypeReference(exec.returnType, true));
		}
		if (exec instanceof ProblemMethodBinding) {
			if (exec.declaringClass != null && Arrays.asList(exec.declaringClass.methods()).contains(exec)) {
				ref.setDeclaringType(getTypeReference(exec.declaringClass));
			} else {
				final CtReference declaringType = getDeclaringReferenceFromImports(exec.constantPoolName());
				if (declaringType instanceof CtTypeReference) {
					ref.setDeclaringType((CtTypeReference<?>) declaringType);
				}
			}
			if (exec.isConstructor()) {
				// super() invocation have a good declaring class.
				ref.setDeclaringType(getTypeReference(exec.declaringClass));
			}
			ref.setStatic(true);
		} else {
			if (exec.isConstructor() && !(exec.returnType instanceof VoidTypeBinding)) {
				ref.setDeclaringType(getTypeReference(exec.returnType));
			} else {
				ref.setDeclaringType(getTypeReference(exec.declaringClass));
			}
			ref.setStatic(exec.isStatic());
		}

		if (exec.declaringClass instanceof ParameterizedTypeBinding) {
			ref.setDeclaringType(getTypeReference(exec.declaringClass.actualType()));
		}

		// original() method returns a result not null when the current method is generic.
		if (exec.original() != null) {
			final List<CtTypeReference<?>> parameters = new ArrayList<>(exec.original().parameters.length);
			for (TypeBinding b : exec.original().parameters) {
				parameters.add(getTypeReference(b, true));
			}
			ref.setParameters(parameters);
		} else if (exec.parameters != null) {
			// This is a method without a generic argument.
			final List<CtTypeReference<?>> parameters = new ArrayList<>();
			for (TypeBinding b : exec.parameters) {
				parameters.add(getTypeReference(b, true));
			}
			ref.setParameters(parameters);
		}

		return ref;
	}

	<T> CtExecutableReference<T> getExecutableReference(AllocationExpression allocationExpression) {
		CtExecutableReference<T> ref;
		if (allocationExpression.binding != null) {
			ref = getExecutableReference(allocationExpression.binding);
		} else {
			ref = jdtTreeBuilder.getFactory().Core().createExecutableReference();
			ref.setSimpleName(CtExecutableReference.CONSTRUCTOR_NAME);
			ref.setDeclaringType(getTypeReference(null, allocationExpression.type));

			final List<CtTypeReference<?>> parameters = new ArrayList<>(allocationExpression.argumentTypes.length);
			for (TypeBinding b : allocationExpression.argumentTypes) {
				parameters.add(getTypeReference(b, true));
			}
			ref.setParameters(parameters);
		}
		if (allocationExpression.type == null) {
			ref.setType(this.<T>getTypeReference(allocationExpression.expectedType(), true));
		}
		return ref;
	}

	<T> CtExecutableReference<T> getExecutableReference(MessageSend messageSend) {
		if (messageSend.binding != null) {
			return getExecutableReference(messageSend.binding);
		}
		CtExecutableReference<T> ref = jdtTreeBuilder.getFactory().Core().createExecutableReference();
		ref.setSimpleName(CharOperation.charToString(messageSend.selector));
		ref.setType(this.<T>getTypeReference(messageSend.expectedType(), true));
		if (messageSend.receiver.resolvedType == null) {
			// It is crisis dude! static context, we don't have much more information.
			ref.setStatic(true);
			if (messageSend.receiver instanceof SingleNameReference) {
				ref.setDeclaringType(jdtTreeBuilder.getHelper().createTypeAccessNoClasspath((SingleNameReference) messageSend.receiver).getAccessedType());
			} else if (messageSend.receiver instanceof QualifiedNameReference) {
				ref.setDeclaringType(jdtTreeBuilder.getHelper().createTypeAccessNoClasspath((QualifiedNameReference) messageSend.receiver).getAccessedType());
			}
		} else {
			ref.setDeclaringType(getTypeReference(messageSend.receiver.resolvedType));
		}
		if (messageSend.arguments != null) {
			final List<CtTypeReference<?>> parameters = new ArrayList<>();
			for (Expression expression : messageSend.arguments) {
				parameters.add(getTypeReference(expression.resolvedType, true));
			}
			ref.setParameters(parameters);
		}
		return ref;
	}

	private CtPackageReference getPackageReference(PackageBinding reference) {
		return getPackageReference(new String(reference.shortReadableName()));
	}

	public CtPackageReference getPackageReference(String name) {
		if (name.isEmpty()) {
			return this.jdtTreeBuilder.getFactory().Package().topLevel();
		}
		CtPackageReference ref = this.jdtTreeBuilder.getFactory().Core().createPackageReference();
		ref.setSimpleName(name);
		return ref;
	}

	final Map<TypeBinding, CtTypeReference> bindingCache = new HashMap<>();

	<T> CtTypeReference<T> getTypeReference(TypeBinding binding, TypeReference ref) {
		CtTypeReference<T> ctRef = getTypeReference(binding);
		if (ctRef != null && isCorrectTypeReference(ref)) {
			insertGenericTypesInNoClasspathFromJDTInSpoon(ref, ctRef);
		} else {
			ctRef = getTypeReference(ref);
		}
		if (ref instanceof SingleTypeReference) {
			ctRef.setSimplyQualified(true);
		} else if (ref instanceof QualifiedTypeReference) {
			jdtTreeBuilder.getHelper().handleImplicit((QualifiedTypeReference) ref, ctRef);
		}
		return ctRef;
	}

	CtTypeReference<Object> getTypeParameterReference(TypeBinding binding, TypeReference ref) {
		CtTypeReference<Object> ctRef = getTypeReference(binding);
		if (ctRef != null && isCorrectTypeReference(ref)) {
			if (!(ctRef instanceof CtTypeParameterReference)) {
				CtTypeParameterReference typeParameterRef = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
				typeParameterRef.setSimpleName(ctRef.getSimpleName());
				typeParameterRef.setDeclaringType(ctRef.getDeclaringType());
				typeParameterRef.setPackage(ctRef.getPackage());
				ctRef = typeParameterRef;
			}
			insertGenericTypesInNoClasspathFromJDTInSpoon(ref, ctRef);
			return ctRef;
		}
		return getTypeParameterReference(CharOperation.toString(ref.getParameterizedTypeName()));
	}

	/**
	 * In no classpath, the model of the super interface isn't always correct.
	 */
	private boolean isCorrectTypeReference(TypeReference ref) {
		if (ref.resolvedType == null) {
			return false;
		}
		if (!(ref.resolvedType instanceof ProblemReferenceBinding)) {
			return true;
		}
		final String[] compoundName = CharOperation.charArrayToStringArray(((ProblemReferenceBinding) ref.resolvedType).compoundName);
		final String[] typeName = CharOperation.charArrayToStringArray(ref.getTypeName());
		if (compoundName.length == 0 || typeName.length == 0) {
			return false;
		}
		return compoundName[compoundName.length - 1].equals(typeName[typeName.length - 1]);
	}

	private <T> void insertGenericTypesInNoClasspathFromJDTInSpoon(TypeReference original, CtTypeReference<T> type) {
		if (original.resolvedType instanceof ProblemReferenceBinding && original.getTypeArguments() != null) {
			for (TypeReference[] typeReferences : original.getTypeArguments()) {
				if (typeReferences != null) {
					for (TypeReference typeReference : typeReferences) {
						type.addActualTypeArgument(this.getTypeReference(typeReference.resolvedType));
					}
				}
			}
		}

		if (original.isParameterizedTypeReference() && !type.isParameterized()) {
			tryRecoverTypeArguments(type);
		}
	}

	/**
	 * In noclasspath mode, empty diamonds in constructor calls on generic types can be lost. This happens if any
	 * of the following apply:
	 *
	 * <ul>
	 *     <li>The generic type is not on the classpath.</li>
	 *     <li>The generic type is used in a context where the type arguments cannot be inferred, such as in an
	 *     unresolved method
	 *     </li>
	 * </ul>
	 *
	 * See #3360 for details.
	 */
	private void tryRecoverTypeArguments(CtTypeReference<?> type) {
		final Deque<ASTPair> stack = jdtTreeBuilder.getContextBuilder().stack;
		if (stack.peek() == null || !(stack.peek().node instanceof AllocationExpression)) {
			// have thus far only ended up here with a generic array type,
			// don't know if we want or need to deal with those
			return;
		}

		AllocationExpression alloc = (AllocationExpression) stack.peek().node;
		if (alloc.expectedType() == null || !(alloc.expectedType() instanceof ParameterizedTypeBinding)) {
			// the expected type is not available/parameterized if the constructor call occurred in e.g. an unresolved
			// method, or in a method that did not expect a parameterized argument
			type.addActualTypeArgument(jdtTreeBuilder.getFactory().Type().OMITTED_TYPE_ARG_TYPE.clone());
		} else {
			ParameterizedTypeBinding expectedType = (ParameterizedTypeBinding) alloc.expectedType();
			// type arguments can be recovered from the expected type
			for (TypeBinding binding : expectedType.typeArguments()) {
				CtTypeReference<?> typeArgRef = getTypeReference(binding);
				typeArgRef.setImplicit(true);
				type.addActualTypeArgument(typeArgRef);
			}
		}
	}

	/**
	 * JDT doesn't return a correct AST with the resolved type of the reference.
	 * This method try to build a correct Spoon AST from the name of the JDT
	 * reference, thanks to the parsing of the string, the name parameterized from
	 * the JDT reference and java convention.
	 * Returns a complete Spoon AST when the name is correct, otherwise a spoon type
	 * reference with a name that correspond to the name of the JDT type reference.
	 */
	<T> CtTypeReference<T> getTypeReference(TypeReference ref) {
		if (ref == null) {
			return null;
		}
		CtTypeReference<T> res = null;
		CtTypeReference inner = null;
		final String[] namesParameterized = CharOperation.charArrayToStringArray(ref.getParameterizedTypeName());
		String nameParameterized = CharOperation.toString(ref.getParameterizedTypeName());
		String typeName = CharOperation.toString(ref.getTypeName());

		int index = namesParameterized.length - 1;
		for (; index >= 0; index--) {
			// Start at the end to get the class name first.
			CtTypeReference main = getTypeReference(namesParameterized[index]);
			if (main == null) {
				break;
			}
			if (res == null) {
				res = (CtTypeReference<T>) main;
			} else {
				inner.setDeclaringType((CtTypeReference<?>) main);
			}
			inner = main;
		}
		if (res == null) {
			return this.jdtTreeBuilder.getFactory().Type().createReference(nameParameterized);
		}

		if (inner.getPackage() == null) {
			PackageFactory packageFactory = this.jdtTreeBuilder.getFactory().Package();
			CtPackageReference packageReference = index >= 0 ? packageFactory.getOrCreate(concatSubArray(namesParameterized, index)).getReference() : packageFactory.topLevel();
			inner.setPackage(packageReference);
		}
		if (!res.toStringDebug().replace(", ?", ",?").endsWith(nameParameterized)) {
			// verify that we did not match a class that have the same name in a different package
			return this.jdtTreeBuilder.getFactory().Type().createReference(typeName);
		}
		return res;
	}

	private String concatSubArray(String[] a, int endIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < endIndex; i++) {
			sb.append(a[i]).append('.');
		}
		sb.append(a[endIndex]);
		return sb.toString();
	}

	/**
	 * Try to build a CtTypeReference from a simple name with specified generic types but
	 * returns null if the name doesn't correspond to a type (not start by an upper case).
	 */
	public <T> CtTypeReference<T> getTypeReference(String name) {
		CtTypeReference<T> main = null;
		if (name.matches(".*(<.+>)")) {
			Pattern pattern = Pattern.compile("([^<]+)<(.+)>");
			Matcher m = pattern.matcher(name);
			if (name.startsWith("?")) {
				main = (CtTypeReference) this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
			} else {
				main = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			}
			if (m.find()) {
				main.setSimpleName(m.group(1));
				final String[] split = m.group(2).split(",");
				for (String parameter : split) {
					main.addActualTypeArgument(getTypeParameterReference(parameter.trim()));
				}
			}
		} else if (Character.isUpperCase(name.charAt(0))) {
			if (name.endsWith("[]")) {
				main = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
				name = name.substring(0, name.length() - 2);
				((CtArrayTypeReference<T>) main).setComponentType(this.getTypeReference(name));
			} else {
				main = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			}
			main.setSimpleName(name);
			final CtReference declaring = this.getDeclaringReferenceFromImports(name.toCharArray());
			setPackageOrDeclaringType(main, declaring);
		} else if (name.startsWith("?")) {
			return (CtTypeReference) this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
		}
		return main;
	}

	/**
	 * Try to build a CtTypeParameterReference from a single name with specified generic types but
	 * keep in mind that if you give wrong data in the strong, reference will be wrong.
	 */
	private CtTypeReference<Object> getTypeParameterReference(String name) {
		CtTypeReference<Object> param = null;
		if (name.contains("extends") || name.contains("super")) {
			String[] split = name.contains("extends") ? name.split("extends") : name.split("super");
			param = getTypeParameterReference(split[0].trim());
			if (param instanceof CtWildcardReference) {
				((CtWildcardReference) param).setBoundingType(getTypeReference(split[split.length - 1].trim()));
			}
		} else if (name.matches(".*(<.+>)")) {
			Pattern pattern = Pattern.compile("([^<]+)<(.+)>");
			Matcher m = pattern.matcher(name);
			if (m.find()) {
				param = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
				param.setSimpleName(m.group(1));
				final String[] split = m.group(2).split(",");
				for (String parameter : split) {
					param.addActualTypeArgument(getTypeParameterReference(parameter.trim()));
				}
			}
		} else if (name.contains("?")) {
			param = this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
		} else {
			param = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
			param.setSimpleName(name);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	<T> CtTypeReference<T> getTypeReference(TypeBinding binding) {
		return getTypeReference(binding, false);
	}
	/**
	 * @param resolveGeneric if true then it never returns CtTypeParameterReference, but it's superClass instead
	 */
	<T> CtTypeReference<T> getTypeReference(TypeBinding binding, boolean resolveGeneric) {
		if (binding == null) {
			return null;
		}

		CtTypeReference<?> ref;

		if (binding instanceof RawTypeBinding) {
			ref = getTypeReference(((ParameterizedTypeBinding) binding).genericType());
		} else if (binding instanceof ParameterizedTypeBinding) {
			if (binding.actualType() != null && binding.actualType() instanceof LocalTypeBinding) {
				// When we define a nested class in a method and when the enclosing class of this method
				// is a parameterized type binding, JDT give a ParameterizedTypeBinding for the nested class
				// and hide the real class in actualType().
				ref = getTypeReference(binding.actualType());
			} else {
				ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
				this.exploringParameterizedBindings.put(binding, ref);
				if (binding.isAnonymousType()) {
					ref.setSimpleName("");
				} else {
					ref.setSimpleName(String.valueOf(binding.sourceName()));
					if (binding.enclosingType() != null) {
						ref.setDeclaringType(getTypeReference(binding.enclosingType()));
					} else {
						ref.setPackage(getPackageReference(binding.getPackage()));
					}
				}
			}
			if (binding.actualType() instanceof MissingTypeBinding) {
				ref = getTypeReference(binding.actualType());
			}

			if (((ParameterizedTypeBinding) binding).arguments != null) {
				for (TypeBinding b : ((ParameterizedTypeBinding) binding).arguments) {
					if (bindingCache.containsKey(b)) {
						ref.addActualTypeArgument(getCtCircularTypeReference(b));
					} else {
						if (!this.exploringParameterizedBindings.containsKey(b)) {
							this.exploringParameterizedBindings.put(b, null);
							CtTypeReference typeRefB = getTypeReference(b);
							this.exploringParameterizedBindings.put(b, typeRefB);
							ref.addActualTypeArgument(typeRefB);
						} else {
							CtTypeReference typeRefB = this.exploringParameterizedBindings.get(b);
							if (typeRefB != null) {
								ref.addActualTypeArgument(typeRefB.clone());
							}
						}
					}
				}
			}
		} else if (binding instanceof MissingTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(new String(binding.sourceName()));
			ref.setPackage(getPackageReference(binding.getPackage()));
			if (!this.jdtTreeBuilder.getContextBuilder().ignoreComputeImports) {
				final CtReference declaring = this.getDeclaringReferenceFromImports(binding.sourceName());
				if (declaring instanceof CtPackageReference) {
					ref.setPackage((CtPackageReference) declaring);
				} else if (declaring instanceof CtTypeReference) {
					ref.setDeclaringType((CtTypeReference) declaring);
				}
			}
		} else if (binding instanceof BinaryTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			if (binding.enclosingType() != null) {
				ref.setDeclaringType(getTypeReference(binding.enclosingType()));
			} else {
				CtPackageReference packageReference = getPackageReference(binding.getPackage());
				ref.setPackage(packageReference);
			}
			ref.setSimpleName(new String(binding.sourceName()));
		} else if (binding instanceof TypeVariableBinding) {
			boolean oldBounds = bounds;

			if (binding instanceof CaptureBinding) {
				ref = this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
				bounds = true;
			} else {
				TypeVariableBinding typeParamBinding = (TypeVariableBinding) binding;
				if (resolveGeneric) {
					//it is called e.g. by ExecutableReference, which must not use CtParameterTypeReference
					//but it needs it's bounding type instead
					ReferenceBinding superClass = typeParamBinding.superclass;
					ReferenceBinding[] superInterfaces = typeParamBinding.superInterfaces();

					CtTypeReference refSuperClass = null;

					// if the type parameter has a super class other than java.lang.Object, we get it
					// superClass.superclass() is null if it's java.lang.Object
					if (superClass != null && !(superClass.superclass() == null)) {

						// this case could happen with Enum<E extends Enum<E>> for example:
						// in that case we only want to have E -> Enum -> E
						// to conserve the same behavior as JavaReflectionTreeBuilder
						if (!(superClass instanceof ParameterizedTypeBinding) || !this.exploringParameterizedBindings.containsKey(superClass)) {
							refSuperClass = this.getTypeReference(superClass, resolveGeneric);
						}

					// if the type parameter has a super interface, then we'll get it too, as a superclass
					// type parameter can only extends an interface or a class, so we don't make the distinction
					// in Spoon. Moreover we can only have one extends in a type parameter.
					} else if (superInterfaces != null && superInterfaces.length == 1) {
						refSuperClass = this.getTypeReference(superInterfaces[0], resolveGeneric);
					}
					if (refSuperClass == null) {
						refSuperClass = this.jdtTreeBuilder.getFactory().Type().getDefaultBoundingType();
					}
					ref = refSuperClass.clone();
				} else {
					ref = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
					ref.setSimpleName(new String(binding.sourceName()));
				}
			}
			TypeVariableBinding b = (TypeVariableBinding) binding;
			if (bounds) {
				if (b instanceof CaptureBinding && ((CaptureBinding) b).wildcard != null) {
					bounds = oldBounds;
					return getTypeReference(((CaptureBinding) b).wildcard, resolveGeneric);
				} else if (b.superclass != null && b.firstBound == b.superclass) {
					bounds = false;
					bindingCache.put(binding, ref);
					if (ref instanceof CtWildcardReference) {
						((CtWildcardReference) ref).setBoundingType(getTypeReference(b.superclass, resolveGeneric));
					}
					bounds = oldBounds;
				}
			}
			if (bounds && b.superInterfaces != null && b.superInterfaces != Binding.NO_SUPERINTERFACES) {
				bindingCache.put(binding, ref);
				List<CtTypeReference<?>> bounds = new ArrayList<>();
				CtTypeParameterReference typeParameterReference = (CtTypeParameterReference) ref;
				if (!(typeParameterReference.isDefaultBoundingType())) { // if it's object we can ignore it
					bounds.add(typeParameterReference.getBoundingType());
				}
				for (ReferenceBinding superInterface : b.superInterfaces) {
					bounds.add(getTypeReference(superInterface, resolveGeneric));
				}
				if (ref instanceof CtWildcardReference) {
					((CtWildcardReference) ref).setBoundingType(this.jdtTreeBuilder.getFactory().Type().createIntersectionTypeReferenceWithBounds(bounds));
				}
			}
			if (binding instanceof CaptureBinding) {
				bounds = false;
			}
		} else if (binding instanceof BaseTypeBinding) {
			String name = new String(binding.sourceName());
			//always create new TypeReference, because clonning from a cache clones invalid SourcePosition
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(name);
		} else if (binding instanceof WildcardBinding) {
			WildcardBinding wildcardBinding = (WildcardBinding) binding;
			CtWildcardReference wref = this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
			ref = wref;

			if (wildcardBinding.boundKind == Wildcard.SUPER) {
				wref.setUpper(false);
			}

			if (wildcardBinding.bound != null) {
				if (bindingCache.containsKey(wildcardBinding.bound)) {
					wref.setBoundingType(getCtCircularTypeReference(wildcardBinding.bound));
				} else {
					wref.setBoundingType(getTypeReference(((WildcardBinding) binding).bound));
				}
			}
		} else if (binding instanceof LocalTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			if (binding.isAnonymousType()) {
				ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName(((SourceTypeBinding) binding).constantPoolName()));
				ref.setDeclaringType(getTypeReference(binding.enclosingType()));
			} else {
				ref.setSimpleName(new String(binding.sourceName()));
				if (((LocalTypeBinding) binding).enclosingMethod == null && binding.enclosingType() != null && binding.enclosingType() instanceof LocalTypeBinding) {
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				} else if (binding.enclosingMethod() != null) {
					ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName(((SourceTypeBinding) binding).constantPoolName()));
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				}
			}
		} else if (binding instanceof SourceTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			if (binding.isAnonymousType()) {
				ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName(((SourceTypeBinding) binding).constantPoolName()));
				ref.setDeclaringType(getTypeReference(binding.enclosingType()));
			} else {
				ref.setSimpleName(new String(binding.sourceName()));
				if (binding.enclosingType() != null) {
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				} else {
					ref.setPackage(getPackageReference(binding.getPackage()));
				}
			}
		} else if (binding instanceof ArrayBinding) {
			CtArrayTypeReference<Object> arrayref;
			arrayref = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
			ref = arrayref;
			for (int i = 1; i < binding.dimensions(); i++) {
				CtArrayTypeReference<Object> tmp = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
				arrayref.setComponentType(tmp);
				arrayref = tmp;
			}
			arrayref.setComponentType(getTypeReference(binding.leafComponentType(), resolveGeneric));
		} else if (binding instanceof PolyTypeBinding) {
			// JDT can't resolve the type of this binding and we only have a string.
			// In this case, we return a type Object because we can't know more about it.
			ref = this.jdtTreeBuilder.getFactory().Type().objectType();
		} else if (binding instanceof ProblemReferenceBinding) {
			// Spoon is able to analyze also without the classpath
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			char[] readableName = binding.readableName();
			StringBuilder sb = new StringBuilder();
			for (int i = readableName.length - 1; i >= 0; i--) {
				char c = readableName[i];
				if (c == '.') {
					break;
				}
				sb.append(c);
			}
			sb.reverse();
			ref.setSimpleName(sb.toString());
			final CtReference declaring = this.getDeclaringReferenceFromImports(binding.sourceName());
			setPackageOrDeclaringType(ref, declaring);
		} else if (binding instanceof JDTTreeBuilder.SpoonReferenceBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(new String(binding.sourceName()));
			ref.setDeclaringType(getTypeReference(binding.enclosingType()));
		} else if (binding instanceof IntersectionTypeBinding18) {
			List<CtTypeReference<?>> bounds = new ArrayList<>();
			for (ReferenceBinding superInterface : binding.getIntersectingTypes()) {
				bounds.add(getTypeReference(superInterface));
			}
			ref = this.jdtTreeBuilder.getFactory().Type().createIntersectionTypeReferenceWithBounds(bounds);
		} else {
			throw new RuntimeException("Unknown TypeBinding: " + binding.getClass() + " " + binding);
		}
		bindingCache.remove(binding);
		this.exploringParameterizedBindings.remove(binding);
		return (CtTypeReference<T>) ref;
	}

	private CtTypeReference<?> getCtCircularTypeReference(TypeBinding b) {
		return bindingCache.get(b).clone();
	}

	@SuppressWarnings("unchecked")
	<T> CtVariableReference<T> getVariableReference(MethodBinding methbin) {
		CtFieldReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createFieldReference();
		ref.setSimpleName(new String(methbin.selector));
		ref.setType(getTypeReference(methbin.returnType));

		if (methbin.declaringClass != null) {
			ref.setDeclaringType(getTypeReference(methbin.declaringClass));
		} else {
			ref.setDeclaringType(ref.getType());
		}
		return ref;
	}

	<T> CtFieldReference<T> getVariableReference(FieldBinding varbin) {
		CtFieldReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createFieldReference();
		if (varbin == null) {
			return ref;
		}
		ref.setSimpleName(new String(varbin.name));
		ref.setType(this.<T>getTypeReference(varbin.type));

		if (varbin.declaringClass != null) {
			ref.setDeclaringType(getTypeReference(varbin.declaringClass));
		} else {
			ref.setDeclaringType(ref.getType() == null ? null : ref.getType().clone());
		}
		ref.setFinal(varbin.isFinal());
		ref.setStatic((varbin.modifiers & ClassFileConstants.AccStatic) != 0);
		return ref;
	}

	<T> CtFieldReference<T> getVariableReference(FieldBinding fieldBinding, char[] tokens) {
		final CtFieldReference<T> ref = getVariableReference(fieldBinding);
		if (fieldBinding != null) {
			return ref;
		}
		ref.setSimpleName(CharOperation.charToString(tokens));
		return ref;
	}

	@SuppressWarnings("unchecked")
	<T> CtVariableReference<T> getVariableReference(VariableBinding varbin) {

		if (varbin instanceof FieldBinding) {
			return getVariableReference((FieldBinding) varbin);
		} else if (varbin instanceof LocalVariableBinding) {
			final LocalVariableBinding localVariableBinding = (LocalVariableBinding) varbin;
			if (localVariableBinding.declaration instanceof Argument && localVariableBinding.declaringScope instanceof MethodScope) {
				CtParameterReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createParameterReference();
				ref.setSimpleName(new String(varbin.name));
				ref.setType(getTypeReference(varbin.type));
				return ref;
			} else if (localVariableBinding.declaration.binding instanceof CatchParameterBinding) {
				CtCatchVariableReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createCatchVariableReference();
				ref.setSimpleName(new String(varbin.name));
				CtTypeReference<T> ref2 = getTypeReference(varbin.type);
				ref.setType(ref2);
				return ref;
			} else {
				CtLocalVariableReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createLocalVariableReference();
				ref.setSimpleName(new String(varbin.name));
				CtTypeReference<T> ref2 = getTypeReference(varbin.type);
				ref.setType(ref2);
				return ref;
			}
		} else {
			// unknown VariableBinding, the caller must do something
			return null;
		}
	}

	<T> CtVariableReference<T> getVariableReference(ProblemBinding binding) {
		CtFieldReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createFieldReference();
		if (binding == null) {
			return ref;
		}
		ref.setSimpleName(new String(binding.name));
		ref.setType(getTypeReference(binding.searchType));
		return ref;
	}

	List<CtTypeReference<?>> getBoundedTypesReferences(TypeBinding[] genericTypeArguments) {
		List<CtTypeReference<?>> res = new ArrayList<>(genericTypeArguments.length);
		for (TypeBinding tb : genericTypeArguments) {
			res.add(getBoundedTypeReference(tb));
		}
		return res;
	}

	/**
	 * Sets {@code declaring} as inner of {@code ref}, as either the package or the declaring type
	 */
	void setPackageOrDeclaringType(CtTypeReference<?> ref, CtReference declaring) {
		if (declaring instanceof CtPackageReference) {
			ref.setPackage((CtPackageReference) declaring);
		} else if (declaring instanceof CtTypeReference) {
			ref.setDeclaringType((CtTypeReference) declaring);
		} else if (declaring == null) {
			try {
				// sometimes JDT does not provide the information that ref comes from java.lang
				// it seems to occurs in particular with anonymous inner classes: see #1307
				// In that case, we try to load the class to check if it belongs to java.lang
				Class.forName("java.lang." + ref.getSimpleName());
				CtPackageReference javaLangPackageReference = this.jdtTreeBuilder.getFactory().Core().createPackageReference();
				javaLangPackageReference.setSimpleName("java.lang");
				ref.setPackage(javaLangPackageReference);
			} catch (NoClassDefFoundError | ClassNotFoundException e) {
				assert jdtTreeBuilder.getFactory().getEnvironment().getNoClasspath();
				ContextBuilder ctx = jdtTreeBuilder.getContextBuilder();
				if (containsStarImport(ctx.compilationunitdeclaration.imports)) {
					// If there is an unresolved star import in noclasspath,
					// we can't tell which package the type belongs to (#3337)
					CtPackageReference pkgRef = jdtTreeBuilder.getFactory().Core().createPackageReference();
					pkgRef.setImplicit(true);
					ref.setPackage(pkgRef);
				} else {
					// otherwise the type must belong to the CU's package (#1293)
					ref.setPackage(ctx.compilationUnitSpoon.getDeclaredPackage().getReference());
				}
			}
		} else {
			throw new AssertionError("unexpected declaring type: " + declaring.getClass() + " of " + declaring);
		}
	}

	private static boolean containsStarImport(ImportReference[] imports) {
		return imports != null && Arrays.stream(imports).anyMatch(imp -> imp.toString().endsWith("*"));
	}

	/**
	 * In noclasspath, lambda doesn't have always a binding for their variables accesses in their block/expression.
	 * Here, we make the job of JDT and bind their variables accesses to their parameters.
	 *
	 * @param singleNameReference Name of the variable access.
	 * @return executable reference which corresponds to the lambda.
	 */
	public CtExecutableReference<?> getLambdaExecutableReference(SingleNameReference singleNameReference) {
		ASTPair potentialLambda = null;
		for (ASTPair astPair : jdtTreeBuilder.getContextBuilder().stack) {
			if (astPair.node instanceof LambdaExpression) {
				potentialLambda = astPair;
				// stop at innermost lambda, fixes #1100
				break;
			}
		}
		if (potentialLambda == null) {
			return null;
		}
		LambdaExpression lambdaJDT = (LambdaExpression) potentialLambda.node;
		for (Argument argument : lambdaJDT.arguments()) {
			if (CharOperation.equals(argument.name, singleNameReference.token)) {
				CtTypeReference<?> declaringType = null;
				if (lambdaJDT.enclosingScope instanceof MethodScope) {
					declaringType = jdtTreeBuilder.getReferencesBuilder().getTypeReference(((MethodScope) lambdaJDT.enclosingScope).parent.enclosingSourceType());
				}
				CtLambda<?> ctLambda = (CtLambda<?>) potentialLambda.element;
				List<CtTypeReference<?>> parametersType = new ArrayList<>();
				List<CtParameter<?>> parameters = ctLambda.getParameters();
				for (CtParameter<?> parameter : parameters) {
					parametersType.add(getMethodParameterType(parameter));
				}
				return jdtTreeBuilder.getFactory().Executable().createReference(declaringType, ctLambda.getType(), ctLambda.getSimpleName(), parametersType);
			}
		}
		return null;
	}

	private CtTypeReference<?> getMethodParameterType(CtParameter<?> param) {
		CtTypeReference<?> paramType = param.getType();
		if (paramType instanceof CtTypeParameterReference) {
			paramType = ((CtTypeParameterReference) paramType).getBoundingType();
		}
		if (paramType == null) {
			paramType = param.getFactory().Type().OBJECT;
		}
		return paramType.clone();
	}

	public CtModuleReference getModuleReference(ModuleReference moduleReference) {
		String moduleName = new String(moduleReference.moduleName);
		CtModule module = this.jdtTreeBuilder.getFactory().Module().getModule(moduleName);
		if (module == null) {
			CtModuleReference ctModuleReference = this.jdtTreeBuilder.getFactory().Core().createModuleReference();
			ctModuleReference.setSimpleName(moduleName);
			return ctModuleReference;
		} else {
			return module.getReference();
		}
	}
}
