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

package spoon.reflect.reference;

import spoon.reflect.declaration.CtTypeInformation;

/**
 * This interface defines a reference to a
 * {@link spoon.reflect.declaration.CtType} or sub-type.
 */
public interface CtTypeReference<T> extends CtReference,
		CtGenericElementReference, CtTypeAnnotableReference, CtTypeInformation {

	/**
	 * The name of the null type ("&lt;nulltype&gt;").
	 */
	public static final String NULL_TYPE_NAME = "<nulltype>";

	/**
	 * Gets the Java runtime class of the referenced type.
	 * 
	 * @return the Java class or null if the class is not found (not in
	 *         classpath)
	 */
	Class<T> getActualClass();

	/**
	 * Gets the type that declares the referenced type.
	 * 
	 * @return the declaring type if this references an inner class; null in
	 *         other cases
	 */
	CtTypeReference<?> getDeclaringType();

	/**
	 * Gets the package of the referenced type.
	 * 
	 * @return the declaring package or null if this if a inner class
	 */
	CtPackageReference getPackage();

	/**
	 * Returns the corresponding non-primitive type for a primitive type (the
	 * same type otherwise).
	 */
	CtTypeReference<?> box();

	/**
	 * Returns the primitive type for a boxing type (unchanged if the type does
	 * not correspond to a boxing type).
	 */
	CtTypeReference<?> unbox();

	/**
	 * Sets the reference to the declaring type. Should be set to null if the
	 * referenced type is not a inner type.
	 */
	void setDeclaringType(CtTypeReference<?> type);

	/**
	 * Sets the reference to the declaring package.
	 */
	void setPackage(CtPackageReference pack);

	/**
	 * Returns true if the reference refers to the super implementation
	 */
	boolean isSuperReference();

	/**
	 * Says that this reference refers to the super implementation
	 */
	void setSuperReference(boolean b);

}
