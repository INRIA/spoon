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
package spoon.test.prettyprinter.testclasses;

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
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
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
import spoon.support.reflect.CtExtendedModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchPackage;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchType;
import static spoon.support.compiler.jdt.JDTTreeBuilderQuery.searchTypeBinding;

// super complex test class
public class ComplexClass {

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
				ref.setPackage(getPackageReference(binding.getPackage()));
			}
			ref.setSimpleName(new String(binding.sourceName()));
		} else if (binding instanceof TypeVariableBinding) {
			boolean oldBounds = bounds;

			if (binding instanceof CaptureBinding) {
				ref = this.jdtTreeBuilder.getFactory().Core().createWildcardReference();
				bounds = true;
			} else {
				TypeVariableBinding typeParamBinding = (TypeVariableBinding) binding;
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
						refSuperClass = this.getTypeReference(superClass);
					}

				// if the type parameter has a super interface, then we'll get it too, as a superclass
				// type parameter can only extends an interface or a class, so we don't make the distinction
				// in Spoon. Moreover we can only have one extends in a type parameter.
				} else if (superInterfaces != null && superInterfaces.length == 1) {
					refSuperClass = this.getTypeReference(superInterfaces[0]);
				}

				ref = this.jdtTreeBuilder.getFactory().Core().createTypeParameterReference();
				ref.setSimpleName(new String(binding.sourceName()));

				if (refSuperClass != null) {
					((CtTypeParameterReference) ref).addBound(refSuperClass);
				}
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
				List<CtTypeReference<?>> bounds = new ArrayList<>();
				CtTypeParameterReference typeParameterReference = (CtTypeParameterReference) ref;
				if (!(typeParameterReference.isDefaultBoundingType())) { // if it's object we can ignore it
					bounds.add(typeParameterReference.getBoundingType());
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
			//always create new TypeReference, because clonning from a cache clones invalid SourcePosition
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(name);
		} else if (binding instanceof WildcardBinding) {
			WildcardBinding wildcardBinding = (WildcardBinding) binding;
			ref = this.jdtTreeBuilder.getFactory().Core().createWildcardReference();

			if (wildcardBinding.boundKind == Wildcard.SUPER && ref instanceof CtTypeParameterReference) {
				((CtTypeParameterReference) ref).setUpper(false);
			}

			if (wildcardBinding.bound != null && ref instanceof CtTypeParameterReference) {
				if (bindingCache.containsKey(wildcardBinding.bound)) {
					((CtTypeParameterReference) ref).setBoundingType(getCtCircularTypeReference(wildcardBinding.bound));
				} else {


					((CtTypeParameterReference) ref).setBoundingType(getTypeReference(((WildcardBinding) binding).bound));
				}
			}
		} else if (binding instanceof LocalTypeBinding) {
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			if (binding.isAnonymousType()) {
				ref.setSimpleName(JDTTreeBuilderHelper.computeAnonymousName(((SourceTypeBinding) binding).constantPoolName()));
				ref.setDeclaringType(getTypeReference((binding.enclosingType())));
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
			ref = arrayref;
			for (int i = 1; i < binding.dimensions(); i++) {
				CtArrayTypeReference<Object> tmp = this.jdtTreeBuilder.getFactory().Core().createArrayTypeReference();
				arrayref.setComponentType(tmp);
				arrayref = tmp;
			}
			arrayref.setComponentType(getTypeReference(binding.leafComponentType()));
		} else if (binding instanceof PolyTypeBinding) {
			// JDT can't resolve the type of this binding and we only have a string.
			// In this case, we return a type Object because we can't know more about it.
			ref = this.jdtTreeBuilder.getFactory().Type().objectType();
		} else if (binding instanceof ProblemReferenceBinding) {
			// Spoon is able to analyze also without the classpath
			ref = this.jdtTreeBuilder.getFactory().Core().createTypeReference();
			ref.setSimpleName(new String(binding.readableName()));
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
}
