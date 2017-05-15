/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

/**
 * This element defines a method declaration.
 */
public interface CtMethod<T> extends CtExecutable<T>, CtTypeMember, CtFormalTypeDeclarer, CtShadowable {
	/**
	 * @param superMethod to be checked method
	 * @return true if this method overrides `superMethod`.<br>
	 * Returns true for itself too.
	 * <pre>
	 * assertTrue(this.isOverriding(this))
	 * </pre>
	 */
	boolean isOverriding(CtMethod<?> superMethod);
	/**
	 * Checks if the method is a default method. Default method can be in interfaces from
	 * Java 8: http://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html.
	 */
	boolean isDefaultMethod();

	/**
	 * Sets the default value state of a method.
	 */
	<C extends CtMethod<T>> C setDefaultMethod(boolean defaultMethod);

	/**
	 * Replaces this element by another one.
	 */
	<R extends T> void replace(CtMethod<T> element);

	@Override
	CtMethod<T> clone();
}
