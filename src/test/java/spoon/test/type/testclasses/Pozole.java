/*
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

package spoon.test.type.testclasses;

import spoon.test.annotation.testclasses.TypeAnnotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pozole<A extends Annotation> {
	@Spice(klass = Pozole.class)
	public void make() {
		List<A> list = new ArrayList<@TypeAnnotation(clazz = Float.class, classes = {Integer.class}) A>();
		addDeliciousIngredient((Class<? extends A>) Annotation.class);
	}

	void addDeliciousIngredient(java.lang.Class<? extends A> ingredient) {
	}

	public void eat() {
		Object a = null;
		if (a instanceof String) {
		}
		if (a instanceof Collection<?>) {
		}
	}

	public void season() {
		Object a = null;
		if (a instanceof @TypeAnnotation(integer = 1) Object[]) {
		}
		if (a instanceof java.lang.Object[]) {
		}
	}

	public void prepare() {
		class Test<T extends Runnable & Serializable> {
		}
		final Runnable runnable = (Runnable & Serializable) () -> System.err.println("");
	}

	public void finish() {
		class Test<T extends Runnable> {
		}
		final Runnable runnable = (Runnable) () -> System.err.println("");
	}
}
