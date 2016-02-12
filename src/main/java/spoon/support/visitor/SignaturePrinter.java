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
package spoon.support.visitor;

import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;

/** Responsible for computing signatures for elements where a signature exists (CtType, CtMethod and CtPackage).
 * Otherwise returns the empty string  */
public class SignaturePrinter extends  CtInheritanceScanner {

	final StringBuffer signature = new StringBuffer();

	public String getSignature() {
		return signature.toString();
	}

	@Override
	public <T> void visitCtArrayTypeReference(CtArrayTypeReference<T> reference) {
		scan(reference.getComponentType());
		write("[]");
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		scan(reference.getDeclaringType());

		write(CtExecutable.EXECUTABLE_SEPARATOR);
		if (reference.isConstructor()) {
			write(reference.getDeclaringType().getSimpleName());
		} else {
			write(reference.getSimpleName());
		}
		write("(");
		if (reference.getParameters().size() > 0) {
			for (CtTypeReference<?> param : reference.getParameters()) {
				if (param != null && !"null".equals(param.getSimpleName())) {
					scan(param);
				} else {
					write(CtExecutableReference.UNKNOWN_TYPE);
				}
				write(", ");
			}
			if (reference.getParameters().size() > 0) {
				clearLast(); // ","
				clearLast(); // space
			}
		}
		write(")");
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		write(reference.getQualifiedName());
	}

	@Override
	public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
		write(ref.getQualifiedName());
		if (ref.getBounds() != null && !ref.getBounds().isEmpty()) {
			if (ref.isUpper()) {
				write(" extends ");
			} else {
				write(" super ");
			}
			for (CtTypeReference<?> b: ref.getBounds()) {
				scan(b);
				write(",");
			}
			if (ref.getBounds().size() > 0) {
				clearLast();
			}
		}
	}

	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		write(c.getDeclaringType().getQualifiedName());
		write("(");
		for (CtParameter<?> p : c.getParameters()) {
			scan(p.getType());
			write(",");
		}
		if (c.getParameters().size() > 0) {
			clearLast();
		}
		write(")");
	}



	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		if (!m.getFormalTypeParameters().isEmpty()) {
			write("<");
			for (CtTypeReference ref: m.getFormalTypeParameters()) {
				scan(ref);
				write(",");
			}
			if (m.getFormalTypeParameters().size() > 0) {
				clearLast();
			}
			write("> ");
		}
		write(m.getType().getQualifiedName());
		write(" ");
		write(m.getSimpleName());
		write("(");
		for (CtParameter<?> p : m.getParameters()) {
			scan(p.getType());
			write(",");
		}
		if (!m.getParameters().isEmpty()) {
			clearLast();
		}
		write(")");
	}

	private SignaturePrinter clearLast() {
		signature.deleteCharAt(signature.length() - 1);
		return this;
	}


	protected SignaturePrinter write(String value) {
		signature.append(value);
		return this;
	}

}
