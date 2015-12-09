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
package spoon.reflect.internal;

import spoon.reflect.reference.CtTypeReference;

/**
 * This interface defines a reference to a {@link spoon.reflect.declaration.CtType} or sub-type
 * but when this type is implicit like given in the diamond operator or parameter of a lambda.
 *
 * <pre>
 * {@code
 *     // The type in the diamond operator of ArrayList is a CtImplicitTypeReference with a String.
 *     List<String> list = new ArrayList<>();
 *
 *     (e) -> {}
 * }
 * </pre>
 *
 * @param <T>
 * 		Implicit type.
 */
public interface CtImplicitTypeReference<T> extends CtTypeReference<T> {
}
