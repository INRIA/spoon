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

import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * When we build a {@link CtTypeReference}, we can have a circular when
 * we got this kind of generic type: {@code <T extends Comparable<? super T>>}.
 * In this case, where we are at the last T, we come back at the first
 * one and we are in a circular.
 *
 * Now, the last T is a CtCircularTypeReference and we stop the circular
 * when we build the generic or when we scan an AST given.
 */
public interface CtCircularTypeReference extends CtTypeParameterReference {
}
