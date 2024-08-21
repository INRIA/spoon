/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api;

import java.util.Collection;
import java.util.List;

/**
 * The type of a javadoc tag (e.g. {@code @link} or {@code @author}).
 * <p>
 * The standard tags are provided by {@link StandardJavadocTagType}, but non-standard tags are often found in real
 * code.
 */
public interface JavadocTagType {

	/**
	 * @return an immutable collection of applicable categories
	 */
	Collection<JavadocTagCategory> categories();

	/**
	 * @return true if this tag can be used as an inline tag. This is <em>not</em> exclusive with {@link #isBlock()}.
	 */
	default boolean isInline() {
		return categories().contains(JavadocTagCategory.INLINE);
	}

	/**
	 * @return true if this tag can be used as a block tag. This is <em>not</em> exclusive with {@link #isInline()}.
	 */
	default boolean isBlock() {
		return categories().contains(JavadocTagCategory.BLOCK);
	}

	/**
	 * @return the name of the tag (e.g. "return")
	 */
	String getName();

	/**
	 * Creates a new tag type with a non-standard name.
	 *
	 * @param name the name of the tag
	 * @param categories the categories it belongs to
	 * @return the crated tag type
	 */
	static JavadocTagType unknown(String name, JavadocTagCategory... categories) {
		return new JavadocTagType() {
			@Override
			public Collection<JavadocTagCategory> categories() {
				return List.of(categories);
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}
}
