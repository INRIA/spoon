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

package spoon.test.visibility.testclasses;

public class A2 {
	public class B {
		public static final int i = 0;
		public boolean m(Object o) {
			return i == 0;
		}
	}

	public class C<T> {
	}

	public boolean instanceOf(Object o) {
		return o instanceof A2.B;
	}

	public C<String> returnType() {
		return new C<String>();
	}

	public Foo<String>.Bar<String> returnType2(String s) {
		return null;
	}

	public void aMethod() {
		class D {
		}
		new D();
	}
}
