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


package spoon.support.visitor.equals;


import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtBiScannerDefault;

/**
 * Used to check equality between an element and another one.
 *
 */
public class EqualsVisitor extends CtBiScannerDefault {
	public static boolean equals(CtElement element, CtElement other) {
		return !new EqualsVisitor().biScan(element, other);
	}

	private final EqualsChecker checker = new EqualsChecker();

	@Override
	protected void enter(CtElement e) {
		super.enter(e);
		checker.setOther(stack.peek());
		checker.scan(e);
		if (checker.isNotEqual()) {
			fail();
		}
	}

	@Override
	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		spoon.reflect.reference.CtTypeReference other = ((spoon.reflect.reference.CtTypeReference) (stack.peek()));
		enter(reference);
		biScan(reference.getPackage(), other.getPackage());
		biScan(reference.getDeclaringType(), other.getDeclaringType());

		//biScan(reference.getActualTypeArguments(), other.getActualTypeArguments()); // required to be commented

		biScan(reference.getAnnotations(), other.getAnnotations());
		biScan(reference.getComments(), other.getComments());
		exit(reference);
	}

	@Override
	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		spoon.reflect.reference.CtArrayTypeReference other = ((spoon.reflect.reference.CtArrayTypeReference) (stack.peek()));
		enter(reference);
		biScan(reference.getComments(), other.getComments());
		biScan(reference.getPackage(), other.getPackage());
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getComponentType(), other.getComponentType());

		//biScan(reference.getActualTypeArguments(), other.getActualTypeArguments()); // required to be commented

		biScan(reference.getAnnotations(), other.getAnnotations());
		exit(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		spoon.reflect.reference.CtExecutableReference other = ((spoon.reflect.reference.CtExecutableReference) (stack.peek()));
		enter(reference);
		biScan(reference.getDeclaringType(), other.getDeclaringType());
		biScan(reference.getParameters(), other.getParameters());

		//biScan(reference.getActualTypeArguments(), other.getActualTypeArguments());  // required to be commented

		biScan(reference.getAnnotations(), other.getAnnotations());
		biScan(reference.getComments(), other.getComments());
		exit(reference);
	}


}

