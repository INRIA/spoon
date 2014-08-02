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
	 * Gets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 *
	 * @return the List of resources
	 */
	List<CtLocalVariable<? extends AutoCloseable>> getResources();

	/**
	 * Sets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 *
	 * @param resources the List of resources to set
	 */
	void setResources(List<CtLocalVariable<? extends AutoCloseable>> resources);

	/**
	 * Adds a resource.
	 *
	 * @param resource the resource to add
	 *
	 * @return true if the resource has been added
	 */
	boolean addResource(CtLocalVariable<? extends AutoCloseable> resource);

	/**
	 * Removes a resource.
	 *
	 * @param resource the resource to remove
	 *
	 * @return true if the resource has been removed
	 */
	boolean removeResource(CtLocalVariable<? extends AutoCloseable> resource);

	/**
	 * Gets the <i>catchers</i> of this <code>try</code>.
	 *
	 * @return the List of catch blocks
	 */
	List<CtCatch> getCatchers();

	/**
	 * Sets the <i>catchers</i> of this <code>try</code>.
	 *
	 * @param catchers the List of catch blocks to set
	 */
	void setCatchers(List<CtCatch> catchers);

	/**
	 * Adds a catch block.
	 *
	 * @param catcher the catch block to add
	 *
	 * @return true if the catch block as been added
	 */
	boolean addCatcher(CtCatch catcher);

	/**
	 * Removes a catch block.
	 *
	 * @param catcher the catch block to remove
	 *
	 * @return true if the catch block as been removed
	 */
	boolean removeCatcher(CtCatch catcher);

	/**
	 * Sets the tried body.
	 *
	 * @return the body
	 */
	CtBlock<?> getBody();

	/**
	 * Sets the tried body.
	 *
	 * @param body the body
	 */
	void setBody(CtBlock<?> body);

	/**
	 * Gets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 *
	 * @return the finally block
	 */
	CtBlock<?> getFinalizer();

	/**
	 * Sets the <i>finalizer</i> block of this <code>try</code> (
	 * <code>finally</code> part).
	 *
	 * @param finalizer the finally block to set
	 */
	void setFinalizer(CtBlock<?> finalizer);
}
