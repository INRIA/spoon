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

package spoon.support.reflect.declaration;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtTypeParameter}.
 * 
 * @author Renaud Pawlak
 */
public class CtTypeParameterImpl extends CtElementImpl implements
		CtTypeParameter {
	private static final long serialVersionUID = 1L;

	List<CtTypeReference<?>> bounds = new ArrayList<CtTypeReference<?>>();

	String name;

	public CtTypeParameterImpl() {
		super();
	}

	public void accept(CtVisitor v) {
		v.visitCtTypeParameter(this);
	}

	public List<CtTypeReference<?>> getBounds() {
		return bounds;
	}

	public String getName() {
		return name;
	}

	public void setBounds(List<CtTypeReference<?>> bounds) {
		this.bounds = bounds;
	}

	public void setName(String name) {
		this.name = name;
	}

}
