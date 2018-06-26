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
package spoon.support;

import spoon.reflect.declaration.CtType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells that a metamodel property is derived, ie computed from the value of another property.
 *
 * For instance {@link CtType#getFields()}  is derived from {@link CtType#getTypeMembers()}
 *
 * It can be put on getter and setters.
 *
 * Contracts:
 * - A setter with @DerivedProperty only triggers one single model intercession event,
 *   on the element primarily responsible for handling the state from which this dervied property is computed.
 * - A getter with @DerivedProperty is never called in CtScanner and derived classes (clone, replace)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DerivedProperty {
}
