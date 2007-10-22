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

package spoon.reflect.code;

import java.util.List;

import spoon.template.TemplateParameter;

/**
 * This code element defines a <code>try</code> statement.
 */
public interface CtTry extends CtStatement, TemplateParameter<Void> {

	/**
	 * Gets the <i>catchers</i> of this <code>try</code>.
	 */
	List<CtCatch> getCatchers();

	/**
	 * Sets the <i>catchers</i> of this <code>try</code>.
	 */
	void setCatchers(List<CtCatch> catchers);

	/**
	 * Sets the tried body.
	 */
	CtBlock<?> getBody();

	/**
	 * Sets the tried body.
	 */
	void setBody(CtBlock<?> body);

	/**
	 * Gets the <i>finalizer</i> block of this <code>try</code> (<code>finally</code>
	 * part).
	 */
	CtBlock<?> getFinalizer();

	/**
	 * Sets the <i>finalizer</i> block of this <code>try</code> (<code>finally</code>
	 * part).
	 */
	void setFinalizer(CtBlock<?> finalizer);
}
