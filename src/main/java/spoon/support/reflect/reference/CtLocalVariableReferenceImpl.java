/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support.reflect.reference;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.visitor.CtVisitor;

public class CtLocalVariableReferenceImpl<T> extends CtVariableReferenceImpl<T>
		implements CtLocalVariableReference<T> {
	private static final long serialVersionUID = 1L;

	private CtLocalVariable<T> declaration;

	public CtLocalVariableReferenceImpl() {
		super();
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtLocalVariableReference(this);
	}

	public CtLocalVariable<T> getDeclaration() {
		return declaration;
	}

	public void setDeclaration(CtLocalVariable<T> declaration) {
		this.declaration = declaration;
	}

}
