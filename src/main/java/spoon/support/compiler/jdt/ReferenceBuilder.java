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
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
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
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceBuilder {

	private Map<String, CtTypeReference<?>> basestypes = new TreeMap<>();

	private boolean bounds = false;

	private boolean isImplicit = false;

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
		if (type == null) {
			return null;
		}
		return buildTypeReferenceInternal(this.<T>getTypeReference(type.resolvedType, type), type, scope);
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
		return (CtTypeParameterReference) this.buildTypeReferenceInternal(this.getTypeParameterReference(type.resolvedType, type), type, scope);
	}


	private <T> CtTypeReference<T> buildTypeReferenceInternal(CtTypeReference<T> typeReference, TypeReference type, Scope scope) {
		if (type == null) {
			return null;
		}
		CtTypeReference<?> currentReference = typeReference;

		for (int position = type.getTypeName().length - 1; position >= 0; position--) {
			if (currentReference == null) {
				break;
			}
			this.jdtTreeBuilder.getContextBuilder().enter(currentReference, type);
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
				currentReference.getActualTypeArguments().clear();
				for (TypeReference typeArgument : type.getTypeArguments()[position]) {
					if (typeArgument instanceof Wildcard || typeArgument.resolvedType instanceof WildcardBinding || typeArgument.resolvedType instanceof TypeVariableBinding) {
						currentReference.addActualTypeArgument(buildTypeParameterReference(typeArgument, scope));
					} else {
						currentReference.addActualTypeArgument(buildTypeReference(typeArgument, scope));
					}
				}
			}
			if (type instanceof Wildcard && typeReference instanceof CtTypeParameterReference) {
				((CtTypeParameterReference) typeReference).setBoundingType(buildTypeReference(((Wildcard) type).bound, scope));
			}
			this.jdtTreeBuilder.getContextBuilder().exit(type);
			currentReference = currentReference.getDeclaringType();
		}
		return typeReference;
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
		if (enclosingType != null && Collections.disjoint(Arrays.asList(ModifierKind.PUBLIC, ModifierKind.PROTECTED), JDTTreeBuilderHelper.getModifiers(enclosingType.modifiers))) {
			String access = "";
			int i = 0;
			for (; i < tokens.length; i++) {
				final char[][] qualified = Arrays.copyOfRange(tokens, 0, i + 1);
				if (!JDTTreeBuilderHelper.isPackage(qualified, ((TreeBuilderCompiler) this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.typeRequestor))) {
					access = CharOperation.toString(qualified);
					break;
				}
			}
			if (!access.contains(CtPackage.PACKAGE_SEPARATOR)) {
				access = JDTTreeBuilderHelper.hasTypeInImports(access, this.jdtTreeBuilder.getContextBuilder());
			}
			final TypeBinding accessBinding = JDTTreeBuilderHelper.searchTypeBinding(access, ((TreeBuilderCompiler) this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.typeRequestor));
			if (accessBinding != null && listener.onAccess(tokens, i)) {
				final TypeBinding superClassBinding = JDTTreeBuilderHelper.searchTypeBinding(accessBinding.superclass(), CharOperation.charToString(tokens[i + 1]));
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
		if (this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration != null && this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.imports != null) {
			for (ImportReference anImport : this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.imports) {
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
						if (packageName.length != 0) {
							aPackage = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createPackage(packageName);
						} else {
							aPackage = null;
						}
						final MissingTypeBinding declaringType = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createMissingType(aPackage, className);
						this.jdtTreeBuilder.getContextBuilder().ignoreComputeImports = true;
						final CtTypeReference<Object> typeReference = getTypeReference(declaringType);
						this.jdtTreeBuilder.getContextBuilder().ignoreComputeImports = false;
						return typeReference;
					} else {
						char[][] chars = CharOperation.subarray(anImport.getImportName(), 0, anImport.getImportName().length - 1);
						Binding someBinding = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.findImport(chars, false, false);
						PackageBinding packageBinding;
						if (someBinding != null && someBinding.isValidBinding() && someBinding instanceof PackageBinding) {
							packageBinding = (PackageBinding) someBinding;
						} else {
							packageBinding = this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment.createPackage(chars);
							if (packageBinding == null) {
								// Big crisis here. We are already in noclasspath mode but JDT doesn't support always
								// creation of a package in this mode. So, if we are in this brace, we make the job of JDT...
								packageBinding = new PackageBinding(chars, null, this.jdtTreeBuilder.getContextBuilder().compilationunitdeclaration.scope.environment);
							}
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
		ref.setSimpleName(new String(exec.selector));
		ref.setType(getTypeReference(exec.returnType));

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
			ref.setDeclaringType(getTypeReference(exec.declaringClass));
			ref.setStatic(exec.isStatic());
		}

		if (exec.declaringClass instanceof ParameterizedTypeBinding) {
			ref.setDeclaringType(getTypeReference(exec.declaringClass.actualType()));
		}

		// original() method returns a result not null when the current method is generic.
		if (exec.original() != null) {
			final List<CtTypeReference<?>> parameters = new ArrayList<>(exec.original().parameters.length);
			for (TypeBinding b : exec.original().parameters) {
				parameters.add(getTypeReference(b));
			}
			ref.setParameters(parameters);
		} else if (exec.parameters != null) {
			// This is a method without a generic argument.
			final List<CtTypeReference<?>> parameters = new ArrayList<>();
			for (TypeBinding b : exec.parameters) {
				parameters.add(getTypeReference(b));
			}
			ref.setParameters(parameters);
		}

		return ref;
	}

	private CtPackageReference getPackageReference(PackageBinding reference) {
		String name = new String(reference.shortReadableName());
		if (name.length() == 0) {
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
			return ctRef;
		}
		return getTypeReference(ref);
	}

	CtTypeReference<Object> getTypeParameterReference(TypeBinding binding, TypeReference ref) {
		CtTypeReference<Object> ctRef = getTypeReference(binding);
		if (ctRef != null && isCorrectTypeReference(ref)) {
			if (!(ctRef instanceof CtTypeParameterReference)) {
				CtTypeParameterReference typeParameterRef = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
				typeParameterRef.setSimpleName(ctRef.getSimpleName());
				typeParameterRef.setDeclaringType(ctRef.getDeclaringType());
				typeParameterRef.setPackage(ctRef.getPackage());
				typeParameterRef.setActualTypeArguments(ctRef.getActualTypeArguments());
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
				for (TypeReference typeReference : typeReferences) {
					type.addActualTypeArgument(this.getTypeReference(typeReference.resolvedType));
				}
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
		CtTypeReference<T> res = null;
		CtTypeReference inner = null;
		final String[] namesParameterized = CharOperation.charArrayToStringArray(ref.getParameterizedTypeName());
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
			return this.jdtTreeBuilder.getFactory().Type().createReference(CharOperation.toString(ref.getParameterizedTypeName()));
		}
		CtPackageReference packageReference = index >= 0
				? this.jdtTreeBuilder.getFactory().Package().getOrCreate(concatSubArray(namesParameterized, index)).getReference()
				: this.jdtTreeBuilder.getFactory().Package().topLevel();
		inner.setPackage(packageReference);
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
	private <T> CtTypeReference<T> getTypeReference(String name) {
		CtTypeReference<T> main = null;
		if (name.matches(".*(<.+>)")) {
			Pattern pattern = Pattern.compile("([^<]+)<(.+)>");
			Matcher m = pattern.matcher(name);
			if (name.startsWith("?")) {
				main = (CtTypeReference) this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
			} else {
				main = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			}
			if (m.find()) {
				main.setSimpleName(m.group(1));
				final String[] split = m.group(2).split(",");
				for (String parameter : split) {
					((CtTypeReference) main).addActualTypeArgument(getTypeParameterReference(parameter.trim()));
				}
			}
		} else if (Character.isUpperCase(name.charAt(0))) {
			main = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			main.setSimpleName(name);
		} else if (name.startsWith("?")) {
			return (CtTypeReference) this.jdtTreeBuilder.getFactory().Type().createTypeParameterReference(name);
		}
		return main;
	}

	/**
	 * Try to build a CtTypeParameterReference from a single name with specified generic types but
	 * keep in mind that if you give wrong data in the strong, reference will be wrong.
	 */
	private CtTypeParameterReference getTypeParameterReference(String name) {
		CtTypeParameterReference param = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
		if (name.contains("extends") || name.contains("super")) {
			String[] split = name.contains("extends") ? name.split("extends") : name.split("super");
			param.setSimpleName(split[0].trim());
			param.setBoundingType(getTypeReference(split[split.length - 1].trim()));
		} else if (name.matches(".*(<.+>)")) {
			Pattern pattern = Pattern.compile("([^<]+)<(.+)>");
			Matcher m = pattern.matcher(name);
			if (m.find()) {
				param.setSimpleName(m.group(1));
				final String[] split = m.group(2).split(",");
				for (String parameter : split) {
					param.addActualTypeArgument(getTypeParameterReference(parameter.trim()));
				}
			}
		} else {
			param.setSimpleName(name);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	<T> CtTypeReference<T> getTypeReference(TypeBinding binding) {
		if (binding == null) {
			return null;
		}

		CtTypeReference<?> ref = null;

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
				ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
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

			if (((ParameterizedTypeBinding) binding).arguments != null) {
				for (TypeBinding b : ((ParameterizedTypeBinding) binding).arguments) {
					if (!this.jdtTreeBuilder.getContextBuilder().isGenericTypeExplicit) {
						isImplicit = true;
					}
					if (bindingCache.containsKey(b)) {
						ref.addActualTypeArgument(getCtCircularTypeReference(b));
					} else {
						ref.addActualTypeArgument(getTypeReference(b));
					}
					isImplicit = false;
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
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			if (binding.enclosingType() != null) {
				ref.setDeclaringType(getTypeReference(binding.enclosingType()));
			} else {
				ref.setPackage(getPackageReference(binding.getPackage()));
			}
			ref.setSimpleName(new String(binding.sourceName()));
		} else if (binding instanceof TypeVariableBinding) {
			boolean oldBounds = bounds;
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			if (binding instanceof CaptureBinding) {
				ref.setSimpleName("?");
				bounds = true;
			} else {
				ref.setSimpleName(new String(binding.sourceName()));
			}
			TypeVariableBinding b = (TypeVariableBinding) binding;
			if (bounds) {
				if (b instanceof CaptureBinding && ((CaptureBinding) b).wildcard != null) {
					bounds = oldBounds;
					return getTypeReference(((CaptureBinding) b).wildcard);
				} else if (b.superclass != null && b.firstBound == b.superclass) {
					bounds = false;
					bindingCache.put(binding, ref);
					((CtTypeParameterReference) ref).setBoundingType(getTypeReference(b.superclass));
					bounds = oldBounds;
				}
			}
			if (bounds && b.superInterfaces != null && b.superInterfaces != Binding.NO_SUPERINTERFACES) {
				bounds = false;
				bindingCache.put(binding, ref);
				Set<CtTypeReference<?>> bounds = new TreeSet<>();
				if (((CtTypeParameterReference) ref).getBoundingType() != null) {
					bounds.add(((CtTypeParameterReference) ref).getBoundingType());
				}
				for (ReferenceBinding superInterface : b.superInterfaces) {
					bounds.add(getTypeReference(superInterface));
				}
				((CtTypeParameterReference) ref).setBoundingType(this.jdtTreeBuilder.getFactory().Type().createIntersectionTypeReferenceWithBounds(bounds));
			}
			if (binding instanceof CaptureBinding) {
				bounds = false;
			}
		} else if (binding instanceof BaseTypeBinding) {
			String name = new String(binding.sourceName());
			if (!this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped) {
				ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
				ref.setImplicit(true);
				ref.setSimpleName(name);
			} else {
				ref = basestypes.get(name);
				if (ref == null) {
					ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
					ref.setSimpleName(name);
					basestypes.put(name, ref);
				} else {
					ref = ref == null ? ref : ref.clone();
				}
			}
		} else if (binding instanceof WildcardBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			ref.setSimpleName("?");
			if (((WildcardBinding) binding).boundKind == Wildcard.SUPER && ref instanceof CtTypeParameterReference) {
				((CtTypeParameterReference) ref).setUpper(false);
			}

			if (((WildcardBinding) binding).bound != null && ref instanceof CtTypeParameterReference) {
				if (bindingCache.containsKey(((WildcardBinding) binding).bound)) {
					final CtCircularTypeReference circularRef = getCtCircularTypeReference(((WildcardBinding) binding).bound);
					((CtTypeParameterReference) ref).setBoundingType(circularRef);
				} else {
					((CtTypeParameterReference) ref).setBoundingType(getTypeReference(((WildcardBinding) binding).bound));
				}
			}
		} else if (binding instanceof LocalTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			if (binding.isAnonymousType()) {
				ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName((SourceTypeBinding) binding));
				ref.setDeclaringType(getTypeReference((binding.enclosingType())));
			} else {
				ref.setSimpleName(new String(binding.sourceName()));
				if (((LocalTypeBinding) binding).enclosingMethod == null && binding.enclosingType() != null && binding.enclosingType() instanceof LocalTypeBinding) {
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				} else if (binding.enclosingMethod() != null) {
					ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName((SourceTypeBinding) binding));
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				}
			}
		} else if (binding instanceof SourceTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			if (binding.isAnonymousType()) {
				ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName((SourceTypeBinding) binding));
				ref.setDeclaringType(getTypeReference((binding.enclosingType())));
			} else {
				ref.setSimpleName(new String(binding.sourceName()));
				if (binding.enclosingType() != null) {
					ref.setDeclaringType(getTypeReference(binding.enclosingType()));
				} else {
					ref.setPackage(getPackageReference(binding.getPackage()));
				}
				// if(((SourceTypeBinding) binding).typeVariables!=null &&
				// ((SourceTypeBinding) binding).typeVariables.length>0){
				// for (TypeBinding b : ((SourceTypeBinding)
				// binding).typeVariables) {
				// ref.getActualTypeArguments().add(getTypeReference(b));
				// }
				// }
			}
		} else if (binding instanceof ArrayBinding) {
			CtArrayTypeReference<Object> arrayref;
			arrayref = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
			arrayref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			ref = arrayref;
			for (int i = 1; i < binding.dimensions(); i++) {
				CtArrayTypeReference<Object> tmp = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
				arrayref.setComponentType(tmp);
				arrayref = tmp;
			}
			arrayref.setComponentType(getTypeReference(binding.leafComponentType()));
		} else if (binding instanceof ProblemReferenceBinding || binding instanceof PolyTypeBinding) {
			// Spoon is able to analyze also without the classpath
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setImplicit(isImplicit || !this.jdtTreeBuilder.getContextBuilder().isLambdaParameterImplicitlyTyped);
			ref.setSimpleName(new String(binding.readableName()));
			final CtReference declaring = this.getDeclaringReferenceFromImports(binding.sourceName());
			this.jdtTreeBuilder.setPackageOrDeclaringType(ref, declaring);
		} else if (binding instanceof JDTTreeBuilder.SpoonReferenceBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(new String(binding.sourceName()));
			ref.setDeclaringType(getTypeReference(binding.enclosingType()));
		} else if (binding instanceof IntersectionTypeBinding18) {
			Set<CtTypeReference<?>> bounds = new TreeSet<>();
			for (ReferenceBinding superInterface : binding.getIntersectingTypes()) {
				bounds.add(getTypeReference(superInterface));
			}
			ref = this.jdtTreeBuilder.getFactory().Type().createIntersectionTypeReferenceWithBounds(bounds);
		} else {
			throw new RuntimeException("Unknown TypeBinding: " + binding.getClass() + " " + binding);
		}
		bindingCache.remove(binding);
		return (CtTypeReference<T>) ref;
	}

	private CtCircularTypeReference getCtCircularTypeReference(TypeBinding b) {
		final CtCircularTypeReference circularRef = this.jdtTreeBuilder.getFactory().Internal().createCircularTypeReference();
		final CtTypeReference originalRef = bindingCache.get(b).clone();
		circularRef.setPackage(originalRef.getPackage());
		circularRef.setSimpleName(originalRef.getSimpleName());
		circularRef.setDeclaringType(originalRef.getDeclaringType());
		circularRef.setActualTypeArguments(originalRef.getActualTypeArguments());
		circularRef.setAnnotations(originalRef.getAnnotations());
		return circularRef;
	}

	@SuppressWarnings("unchecked")
	<T> CtVariableReference<T> getVariableReference(MethodBinding methbin) {
		CtFieldReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createFieldReference();
		ref.setSimpleName(new String(methbin.selector));
		ref.setType((CtTypeReference<T>) getTypeReference(methbin.returnType));

		if (methbin.declaringClass != null) {
			ref.setDeclaringType(getTypeReference(methbin.declaringClass));
		} else {
			ref.setDeclaringType(ref.getType());
		}
		return ref;
	}

	@SuppressWarnings("unchecked")
	<T> CtFieldReference<T> getVariableReference(FieldBinding varbin) {
		CtFieldReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createFieldReference();
		if (varbin == null) {
			return ref;
		}
		ref.setSimpleName(new String(varbin.name));
		ref.setType((CtTypeReference<T>) getTypeReference(varbin.type));

		if (varbin.declaringClass != null) {
			ref.setDeclaringType(getTypeReference(varbin.declaringClass));
		} else {
			ref.setDeclaringType(ref.getType());
		}
		ref.setFinal(varbin.isFinal());
		ref.setStatic((varbin.modifiers & ClassFileConstants.AccStatic) != 0);
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
				ref.setType((CtTypeReference<T>) getTypeReference(varbin.type));
				final ReferenceContext referenceContext = localVariableBinding.declaringScope.referenceContext();
				if (referenceContext instanceof LambdaExpression) {
					ref.setDeclaringExecutable(getExecutableReference(((LambdaExpression) referenceContext).binding));
				} else {
					ref.setDeclaringExecutable(getExecutableReference(((AbstractMethodDeclaration) referenceContext).binding));
				}
				return ref;
			} else if (localVariableBinding.declaration.binding instanceof CatchParameterBinding) {
				CtCatchVariableReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createCatchVariableReference();
				ref.setSimpleName(new String(varbin.name));
				CtTypeReference<T> ref2 = getTypeReference(varbin.type);
				ref.setType(ref2);
				ref.setDeclaration((CtCatchVariable<T>) this.jdtTreeBuilder.getContextBuilder().getCatchVariableDeclaration(ref.getSimpleName()));
				return ref;
			} else {
				CtLocalVariableReference<T> ref = this.jdtTreeBuilder.getFactory().Core().createLocalVariableReference();
				ref.setSimpleName(new String(varbin.name));
				CtTypeReference<T> ref2 = getTypeReference(varbin.type);
				ref.setType(ref2);
				ref.setDeclaration((CtLocalVariable<T>) this.jdtTreeBuilder.getContextBuilder().getLocalVariableDeclaration(ref.getSimpleName()));
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
		ref.setType((CtTypeReference<T>) getTypeReference(binding.searchType));
		return ref;
	}

	List<CtTypeReference<?>> getBoundedTypesReferences(TypeBinding[] genericTypeArguments) {
		List<CtTypeReference<?>> res = new ArrayList<>(genericTypeArguments.length);
		for (TypeBinding tb : genericTypeArguments) {
			res.add(getBoundedTypeReference(tb));
		}
		return res;
	}
}
