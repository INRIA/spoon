/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api;

import static spoon.javadoc.api.JavadocTagCategory.BLOCK;
import static spoon.javadoc.api.JavadocTagCategory.INLINE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Contains all standard javadoc tag types.
 */
public enum StandardJavadocTagType implements JavadocTagType {
	AUTHOR("author", BLOCK),
	CODE("code", INLINE),
	DEPRECATED("deprecated", BLOCK),
	DOC_ROOT("docRoot", INLINE),
	EXCEPTION("exception", BLOCK),
	HIDDEN("hidden", BLOCK),
	INDEX("index", INLINE),
	INHERIT_DOC("inheritDoc", INLINE),
	LINK("link", INLINE),
	LINKPLAIN("linkplain", INLINE),
	LITERAL("literal", INLINE),
	PARAM("param", BLOCK),
	PROVIDES("provides", BLOCK),
	RETURN("return", INLINE, BLOCK),
	SEE("see", BLOCK),
	SERIAL("serial", BLOCK),
	SERIAL_DATA("serialData", BLOCK),
	SERIAL_FIELD("serialField", BLOCK),
	SINCE("since", BLOCK),
	SNIPPET("snippet", INLINE),
	SUMMARY("summary", INLINE),
	SYSTEM_PROPERTY("systemProperty", INLINE),
	THROWS("throws", BLOCK),
	USES("uses", BLOCK),
	VALUE("value", INLINE),
	VERSION("version", BLOCK);

	private final String name;
	private final Set<JavadocTagCategory> categories;

	StandardJavadocTagType(String name, JavadocTagCategory... categories) {
		this.name = name;
		this.categories = Set.of(categories);
	}

	@Override
	public Collection<JavadocTagCategory> categories() {
		return categories;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Tries to parse a tag type from its string representation (see {@link #getName()}).
	 *
	 * @param name the name to parse
	 * @return the matching standard tag, if any
	 */
	public static Optional<JavadocTagType> fromString(String name) {
		return Arrays.stream(values())
			.filter(it -> it.getName().equalsIgnoreCase(name))
			.map(it -> (JavadocTagType) it)
			.findFirst();
	}
}
