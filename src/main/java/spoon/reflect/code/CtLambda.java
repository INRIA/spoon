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
package spoon.reflect.code;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.UnsettableProperty;

import java.util.Set;

import static spoon.reflect.path.CtRole.EXPRESSION;

/**
 * This code element represents the creation of a lambda. A lambda
 * can have two sorts of body : an simple expression or a block of
 * statements. The usage of this concept in this class is:
 *
 * <pre>
 *     java.util.List l = new java.util.ArrayList();
 *     l.stream().map(
 *       x -&gt; { return x.toString(); } // a lambda
 *     );
 * </pre>
 *
 * <ul>
 * <li>
 * If your lambda has an expression, getBody method will
 * return null and getExpression method will return a
 * CtExpression.
 * </li>
 * <li>
 * If your lambda has a block of statement, getExpression
 * method will return null and getBody will returns a CtBlock
 * with all statements.
 * </li>
 * </ul>
 *
 * So keep this in mind when you would like the body of a CtLambda.
 *
 * @param <T>
 * 		created type
 */
public interface CtLambda<T> extends CtExpression<T>, CtExecutable<T> {
	/**
	 * Gets the expression in the body. Null if the body is a list
	 * of statements.
	 */
	@PropertyGetter(role = EXPRESSION)
	CtExpression<T> getExpression();

	/**
	 * @return the method that this lambda expression implements.
	 * Must be defined as a non-default method in an interface, e.g. Consumer.accept().
	 */
	@DerivedProperty
	<R> CtMethod<R> getOverriddenMethod();

	/**
	 * Sets the expression in the body of the lambda. Nothing will change
	 * if the lambda already has a value in the body attribute.
	 */
	@PropertySetter(role = EXPRESSION)
	<C extends CtLambda<T>> C setExpression(CtExpression<T> expression);

	@Override
	CtLambda<T> clone();

	@Override
	@UnsettableProperty
	<T1 extends CtExecutable<T>> T1 setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);
}
