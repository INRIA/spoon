/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;

/**
 * Defines the core bi-scan responsibility.
 */
public abstract class CtAbstractBiScanner extends CtAbstractVisitor {

	/** This method is called to compare `element` and `other` when traversing two trees in parallel.*/
	public abstract void biScan(CtElement element, CtElement other);

	/** This method is called to compare `element` and `other` according to the role when traversing two trees in parallel. */
	public abstract void biScan(CtRole role, CtElement element, CtElement other);

}
