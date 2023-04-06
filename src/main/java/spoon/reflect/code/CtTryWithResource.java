/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TRY_RESOURCE;

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
	 *
	 * The returned list is immutable for sake of proper encapsulation.
	 */
	@PropertyGetter(role = TRY_RESOURCE)
	List<CtResource<?>> getResources();

	/**
	 * Sets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 */
	@PropertySetter(role = TRY_RESOURCE)
	<T extends CtTryWithResource> T setResources(List<? extends CtResource<?>> resources);

	/**
	 * Adds a resource.
	 */
	@PropertySetter(role = TRY_RESOURCE)
	<T extends CtTryWithResource> T addResource(CtResource<?> resource);

	/**
	 * Removes a resource.
	 */
	@PropertySetter(role = TRY_RESOURCE)
	boolean removeResource(CtResource<?> resource);

	@Override
	CtTryWithResource clone();
}
