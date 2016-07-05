/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
 * Corresponds to one enum value specified in an enumeration.
 * If the enum value implicitly calls a constructor (see example below),
 * it is stored in the default expression of the field as CtConstructorCall,
 *
 * <pre>
 *     class enum {
 *         ENUM_VALUE("default expression.");
 *     }
 * </pre>
 *
 * @param <T>
 * 		the type of the enum, hence equal to the type of getParent().
 */
public interface CtEnumValue<T> extends CtField<T> {
	@Override
	CtEnumValue clone();
}
