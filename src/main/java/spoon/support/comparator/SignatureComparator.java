/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.comparator;

import spoon.reflect.declaration.CtElement;
import spoon.support.visitor.SignaturePrinter;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares executables (method, executable-references) based on a signature.
 */
public class SignatureComparator implements Comparator<CtElement>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public int compare(CtElement o1, CtElement o2) {
		SignaturePrinter signaturePrinter1 = new SignaturePrinter();
		SignaturePrinter signaturePrinter2 = new SignaturePrinter();
		signaturePrinter1.scan(o1);
		signaturePrinter2.scan(o2);
		return signaturePrinter1.getSignature().compareTo(signaturePrinter2.getSignature());
	}

}
