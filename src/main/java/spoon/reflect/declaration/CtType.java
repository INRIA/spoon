/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;

import java.util.List;
import java.util.Set;

/**
 * This abstract element defines a super-type for classes and interfaces, which
 * can declare methods.
 *
 * The type parameter T refers to the actual class that this type represents.
 */
public interface CtType<T> extends CtNamedElement, CtTypeInformation, CtTypeMember, CtGenericElement {
	/**
	 * The string separator in a Java innertype qualified name.
	 */
	String INNERTTYPE_SEPARATOR = "$";

	/**
	 * Returns the simple (unqualified) name of this element.
	 * Following the compilation convention, if the type is a local type,
	 * the name starts with a numeric prefix (e.g. local class Foo has simple name 1Foo).
	 */
	@Override
	String getSimpleName();

	/**
	 * Returns the types used by this type.
	 *
	 * @param includeSamePackage
	 * 		set to true if the method should return also the types located
	 * 		in the same package as the current type
	 */
	Set<CtTypeReference<?>> getUsedTypes(boolean includeSamePackage);

	/**
	 * Returns the actual runtime class if exists.
	 *
	 * @return the runtime class, null if is not accessible or does not exist
	 */
	Class<T> getActualClass();

	/**
	 * Gets a field from its name.
	 *
	 * @return null if does not exit
	 */
	CtField<?> getField(String name);

	/**
	 * Returns the fields that are directly declared by this class or interface.
	 * Includes enum constants.
	 */
	List<CtField<?>> getFields();

	/**
	 * Gets a nested type from its name.
	 */
	<N extends CtType<?>> N getNestedType(String name);

	/**
	 * Returns the declarations of the nested classes and interfaces that are
	 * directly declared by this class or interface.
	 */
	Set<CtType<?>> getNestedTypes();

	/**
	 * Gets the package where this type is declared.
	 */
	CtPackage getPackage();

	/**
	 * Returns the corresponding type reference.
	 *
	 * Overrides the return type.
	 */
	CtTypeReference<T> getReference();

	/**
	 * Returns true if this type is top-level (declared as the main type in a
	 * file).
	 */
	boolean isTopLevel();

	/**
	 * add a Field
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<F, C extends CtType<T>> C addField(CtField<F> field);

	/**
	 * add a field at a given position.
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<F, C extends CtType<T>> C addField(int index, CtField<F> field);

	/**
	 * remove a Field
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<F> boolean removeField(CtField<F> field);

	/**
	 * Add a nested type.
	 *
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<N, C extends CtType<T>> C addNestedType(CtType<N> nestedType);

	/**
	 * Remove a nested type.
	 *
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<N> boolean removeNestedType(CtType<N> nestedType);

	/**
	 * Compiles and replace all the code snippets that are found in this type.
	 *
	 * @see CtCodeSnippet
	 * @see spoon.reflect.code.CtCodeSnippetExpression
	 * @see spoon.reflect.code.CtCodeSnippetStatement
	 */
	void compileAndReplaceSnippets();

	/**
	 * Return all the accessible methods (concrete and abstract) for this type.
	 *
	 * The recursion stops when the super-type/super-interface is not in the model.
	 */
	Set<CtMethod<?>> getAllMethods();

	/**
	 * Gets a method from its return type, name, and parameter types.
	 *
	 * @return null if does not exit
	 */
	<R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Gets a method from its name and parameter types.
	 *
	 * @return null if does not exit
	 */
	<R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface.
	 */
	Set<CtMethod<?>> getMethods();

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and annotated with one of the given annotations.
	 */
	Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and that have the given name.
	 */
	List<CtMethod<?>> getMethodsByName(String name);

	/**
	 * Sets the methods of this type.
	 */
	<C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods);

	/**
	 * Adds a method to this type.
	 */
	<M, C extends CtType<T>> C addMethod(CtMethod<M> method);

	/**
	 * Removes a method from this type.
	 */
	<M> boolean removeMethod(CtMethod<M> method);

	/**
	 * Sets the super interfaces of this type.
	 */
	<C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	/**
	 * @param interfac
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<S, C extends CtType<T>> C addSuperInterface(CtTypeReference<S> interfac);

	/**
	 * @param interfac
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<S> boolean removeSuperInterface(CtTypeReference<S> interfac);

}
