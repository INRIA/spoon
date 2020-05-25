/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;
import java.util.Set;

import static spoon.reflect.path.CtRole.METHOD;
import static spoon.reflect.path.CtRole.FIELD;
import static spoon.reflect.path.CtRole.INTERFACE;
import static spoon.reflect.path.CtRole.NAME;
import static spoon.reflect.path.CtRole.NESTED_TYPE;
import static spoon.reflect.path.CtRole.SUPER_TYPE;
import static spoon.reflect.path.CtRole.TYPE_MEMBER;

/**
 * This abstract element defines a super-type for classes and interfaces, which
 * can declare methods.
 *
 * The type parameter T refers to the actual class that this type represents.
 */
public interface CtType<T> extends CtNamedElement, CtTypeInformation, CtTypeMember, CtFormalTypeDeclarer, CtShadowable {
	/**
	 * The string separator in a Java innertype qualified name.
	 */
	String INNERTTYPE_SEPARATOR = "$";
	/**
	 * Used in no classpath when we don't have any information to build the name of the type.
	 */
	String NAME_UNKNOWN = "<unknown>";

	/**
	 * Returns the simple (unqualified) name of this element.
	 * Following the compilation convention, if the type is a local type,
	 * the name starts with a numeric prefix (e.g. local class Foo has simple name 1Foo).
	 */
	@Override
	@PropertyGetter(role = NAME)
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
	 *
	 * NEVER USE THIS.
	 *
	 * See {@link CtTypeReference#getActualClass()}.
	 *
	 * @deprecated (since Spoon 7.0.0) this will be removed from the public API
	 */
	@DerivedProperty
	Class<T> getActualClass();

	/**
	 * Gets a field from its name.
	 *
	 * @return null if does not exit
	 */
	@PropertyGetter(role = FIELD)
	CtField<?> getField(String name);

	/**
	 * Returns the fields that are directly declared by this class or interface.
	 * Includes enum constants.
	 *
	 * Derived from {@link #getTypeMembers()}
	 */
	@DerivedProperty
	@PropertyGetter(role = FIELD)
	List<CtField<?>> getFields();

	/**
	 * Gets a nested type from its name.
	 */
	@PropertyGetter(role = NESTED_TYPE)
	<N extends CtType<?>> N getNestedType(String name);

	/**
	 * Returns the declarations of the nested classes and interfaces that are
	 * directly declared by this class or interface.
	 */
	@DerivedProperty
	@PropertyGetter(role = NESTED_TYPE)
	Set<CtType<?>> getNestedTypes();

	/**
	 * Gets the package where this type is declared.
	 */
	@DerivedProperty
	CtPackage getPackage();

	/**
	 * Gets the entire class code with package and imports.
	 */
	@DerivedProperty
	String toStringWithImports();

	/**
	 * Returns the corresponding type reference.
	 *
	 * Overrides the return type.
	 */
	@Override
	@DerivedProperty
	CtTypeReference<T> getReference();

	/**
	 * Returns true if this type is top-level (declared as the main type in a
	 * file).
	 */
	boolean isTopLevel();

	/**
	 * Adds a field at the top of the type (before static block).
	 * Note that the position of these field will be negative to be written at the top of the type.
	 *
	 * @param field
	 * @return <tt>true</tt> if the field is added.
	 */
	@PropertySetter(role = FIELD)
	<F, C extends CtType<T>> C addFieldAtTop(CtField<F> field);

	/**
	 * add a field at the end of the field list.
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = FIELD)
	<F, C extends CtType<T>> C addField(CtField<F> field);

	/**
	 * add a field at a given position.
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = FIELD)
	<F, C extends CtType<T>> C addField(int index, CtField<F> field);

	/**
	 * Sets all fields in the type.
	 */
	@PropertySetter(role = FIELD)
	<C extends CtType<T>> C setFields(List<CtField<?>> fields);

	/**
	 * remove a Field
	 *
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = FIELD)
	<F> boolean removeField(CtField<F> field);

	/**
	 * Add a nested type.
	 *
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = NESTED_TYPE)
	<N, C extends CtType<T>> C addNestedType(CtType<N> nestedType);

	/**
	 * Remove a nested type.
	 *
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = NESTED_TYPE)
	<N> boolean removeNestedType(CtType<N> nestedType);

	/**
	 * Sets all nested types.
	 */
	@PropertySetter(role = NESTED_TYPE)
	<C extends CtType<T>> C setNestedTypes(Set<CtType<?>> nestedTypes);

	/**
	 * Replace all the code snippets that are found in this type by the corresponding Spoon AST.
	 *
	 * @see CtCodeSnippet
	 * @see spoon.reflect.code.CtCodeSnippetExpression
	 * @see spoon.reflect.code.CtCodeSnippetStatement
	 */
	void compileAndReplaceSnippets();

	/**
	 * Return all the methods that can be called on an instance of this type.
	 * It recursively collects all methods (both concrete and abstract) from all super-classes and all super-interfaces.
	 * It deduplicates methods with the same signature, which are defined several times somewhere in the type hierarchy.
	 *
	 * Warning: this method can be really slow due to signature deduplication.
	 *
	 * It includes all methods: the methods of types whose source code is in the Spoon model,
	 * the methods of types from the JDK and from libraries present in the classpath,
	 * the methods of java.lang.Object (for all CtClass objects).
	 * However, in noclasspath mode, it does not include methods from unknown types.
	 * If methods are overridden somewhere in the type hierarchy, it returns only top methods (ie method definitions).
	 */
	@DerivedProperty
	Set<CtMethod<?>> getAllMethods();

	/**
	 * Gets a method from its return type, name, and parameter types.
	 *
	 * @return null if does not exit
	 */
	@PropertyGetter(role = METHOD)
	<R> CtMethod<R> getMethod(CtTypeReference<R> returnType, String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Gets a method from its name and parameter types.
	 *
	 * @return null if does not exit
	 */
	@PropertyGetter(role = METHOD)
	<R> CtMethod<R> getMethod(String name, CtTypeReference<?>... parameterTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface.
	 *
	 * Derived from {@link #getTypeMembers()}
	 *
	 */
	@DerivedProperty
	@PropertyGetter(role = METHOD)
	Set<CtMethod<?>> getMethods();

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and annotated with one of the given annotations.
	 */
	@PropertyGetter(role = METHOD)
	Set<CtMethod<?>> getMethodsAnnotatedWith(CtTypeReference<?>... annotationTypes);

	/**
	 * Returns the methods that are directly declared by this class or
	 * interface and that have the given name.
	 */
	@PropertyGetter(role = METHOD)
	List<CtMethod<?>> getMethodsByName(String name);

	/**
	 * Searches in the type for the given method.
	 * Super classes and implemented interfaces are considered.
	 * The matching criterion is that the signatures are identical.
	 * @param method The method to search for in the class.
	 * @return True: the type has the given method. False otherwise.
	 */
	boolean hasMethod(CtMethod<?> method);

	/**
	 * Sets the methods of this type.
	 */
	@PropertySetter(role = METHOD)
	<C extends CtType<T>> C setMethods(Set<CtMethod<?>> methods);

	/**
	 * Adds a method to this type.
	 */
	@PropertySetter(role = METHOD)
	<M, C extends CtType<T>> C addMethod(CtMethod<M> method);

	/**
	 * Removes a method from this type.
	 */
	@PropertySetter(role = METHOD)
	<M> boolean removeMethod(CtMethod<M> method);

	/**
	 * Sets the superclass type.
	 */
	@PropertySetter(role = SUPER_TYPE)
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);

	/**
	 * Sets the super interfaces of this type.
	 */
	@PropertySetter(role = INTERFACE)
	<C extends CtType<T>> C setSuperInterfaces(Set<CtTypeReference<?>> interfaces);

	/**
	 * @param interfac
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = INTERFACE)
	<S, C extends CtType<T>> C addSuperInterface(CtTypeReference<S> interfac);

	/**
	 * @param interfac
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = INTERFACE)
	<S> boolean removeSuperInterface(CtTypeReference<S> interfac);

	/**
	 * Gets all type members of the type like fields, methods, anonymous block, etc.
	 */
	@PropertyGetter(role = TYPE_MEMBER)
	List<CtTypeMember> getTypeMembers();

	/**
	 * Adds a type member at the end of all type member of the type.
	 */
	@PropertySetter(role = TYPE_MEMBER)
	<C extends CtType<T>> C addTypeMember(CtTypeMember member);

	/**
	 * Adds a type member at a given position. Think to use this method if the order is
	 * important for you.
	 */
	@PropertySetter(role = TYPE_MEMBER)
	<C extends CtType<T>> C addTypeMemberAt(int position, CtTypeMember member);

	/**
	 * Removes the type member.
	 */
	@PropertySetter(role = TYPE_MEMBER)
	boolean removeTypeMember(CtTypeMember member);

	/**
	 * Removes all types members with these new members.
	 */
	@PropertySetter(role = TYPE_MEMBER)
	<C extends CtType<T>> C setTypeMembers(List<CtTypeMember> members);

	@Override
	CtType<T> clone();

	/**
	 * Copy the type, where copy means cloning + porting all the references in the clone from the old type to the new type.
	 *
	 * The copied type is added to the same package (and this to the factory as well).
	 *
	 * A new unique method name is given for each copy, and this method can be called several times.
	 */
	CtType<?> copyType();
}
