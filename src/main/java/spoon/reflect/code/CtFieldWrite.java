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
package spoon.reflect.code;

/**
 * This code element defines a write to a field.
 *
 * In Java, it is a usage of a field inside an assignment.
 *
 * For example:
 * <pre>
 *     this.field = "new value";
 *     this.field += "";
 * </pre>
 *
 * If you process this element, keep in mind that you won't process field++.
 *
 * @param <T>
 * 		type of the field
 */
public interface CtFieldWrite<T> extends CtFieldAccess<T> {
}
