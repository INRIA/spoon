/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.refactoring.Refactoring;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.Collection;

import static spoon.reflect.path.CtRole.IS_DEFAULT;


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
	@PropertyGetter(role = IS_DEFAULT)
	boolean isDefaultMethod();

	/**
	 * Sets the default value state of a method.
	 */
	@PropertySetter(role = IS_DEFAULT)
	<C extends CtMethod<T>> C setDefaultMethod(boolean defaultMethod);

	@Override
	CtMethod<T> clone();

	/**
	 * Returns the top-most methods in the hierarchy defining this method
	 * (in super class and super interfaces).
	 * Returns the empty collection if defined here for the first time.
	 */
	Collection<CtMethod<?>> getTopDefinitions();

	/**
	 * Copy the method, where copy means cloning + porting all the references of the old method to the new method (important for recursive methods).
	 * The copied method is added to the type, with a suffix "Copy".
	 *
	 * A new unique method name is given for each copy, and this method can be called several times.
	 *
	 * If you want to rename the new method, use {@link Refactoring#changeMethodName(CtMethod, String)} (and not {@link #setSimpleName(String)}, which does not update the references)
	 */
	CtMethod<?> copyMethod();

}
