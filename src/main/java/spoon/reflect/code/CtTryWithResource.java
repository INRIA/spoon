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
package spoon.reflect.code;

import java.util.List;

/**
 * This code element defines a <code>try</code> with resource statement.
 *
 * Example:
 * <pre>
 *    // br is the resource
 *    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("/foo"))) {
 *    	br.readLine();
 *   }
 * </pre>
 */
public interface CtTryWithResource extends CtTry {

	/**
	 * Gets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 */
	List<CtLocalVariable<?>> getResources();

	/**
	 * Sets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 */
	<T extends CtTryWithResource> T setResources(List<CtLocalVariable<?>> resources);

	/**
	 * Adds a resource.
	 */
	<T extends CtTryWithResource> T addResource(CtLocalVariable<?> resource);

	/**
	 * Removes a resource.
	 */
	boolean removeResource(CtLocalVariable<?> resource);

	@Override
	CtTryWithResource clone();
}
