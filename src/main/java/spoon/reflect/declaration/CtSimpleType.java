/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This abstract element represents the types that can be declared in a Java
 * program.
 * 
 * @param <T>
 *            the actual runtime type
 */
public interface CtSimpleType<T> extends CtNamedElement, CtTypeInformation, CtTypeMember {

	/**
	 * Returns the types used by this type.
	 * 
	 * @param includeSamePackage
	 *            set to true if the method should return also the types located
	 *            in the same package as the current type
	 */
	Set<CtTypeReference<?>> getUsedTypes(boolean includeSamePackage);

	/**
	 * The string separator in a Java innertype qualified name.
	 */
	public static final String INNERTTYPE_SEPARATOR = "$";

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
	<N extends CtSimpleType<?>> N getNestedType(String name);

	/**
	 * Returns the declarations of the nested classes and interfaces that are
	 * directly declared by this class or interface.
	 */
	Set<CtSimpleType<?>> getNestedTypes();

	/**
	 * Gets the package where this type is declared.
	 */
	CtPackage getPackage();

	/** Returns the corresponding type reference.
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
	<F> boolean addField(CtField<F> field);

	/**
	 * remove a Field
	 * 
	 * @param field
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<F> boolean removeField(CtField<F> field);

	/**
	 * remove a nested type
	 * 
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<N> boolean addNestedType(CtSimpleType<N> nestedType);

	/**
	 * add a nested type
	 * 
	 * @param nestedType
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<N> boolean removeNestedType(CtSimpleType<N> nestedType);

	/**
	 * Compiles and replace all the code snippets that are found in this type.
	 * 
	 * @see CtCodeSnippet
	 * @see CtCodeSnippetExpression
	 * @see CtCodeSnippetStatement
	 */
	void compileAndReplaceSnippets();

}
