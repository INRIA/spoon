/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc;

import java.util.Arrays;

import static spoon.reflect.code.CtJavaDocTag.JAVADOC_TAG_PREFIX;

public enum JavadocTagType {

	AUTHOR("author"),
	DEPRECATED("deprecated"),
	EXCEPTION("exception"),
	PARAM("param"),
	RETURN("return"),
	SEE("see"),
	SERIAL("serial"),
	SERIAL_DATA("serialData"),
	SERIAL_FIELD("serialField"),
	SINCE("since"),
	THROWS("throws"),
	VERSION("version"),
	UNKNOWN("unknown");

	JavadocTagType(String name) {
		this.name = name;
	}

	private String name;

	/**
	 * Get tag name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return true if the tag can have a parameter
	 * @return true if the tag can have a parameter
	 */
	public boolean hasParam() {
		return this == PARAM || this == THROWS || this == EXCEPTION;
	}

	/**
	 * Get the tag type associated to a name
	 * @param tagName the tag name
	 * @return the tag type
	 */
	public static JavadocTagType fromName(String tagName) {
		return Arrays.stream(JavadocTagType.values())
				.filter(v -> v.name.equals(tagName))
				.findFirst().orElse(UNKNOWN);
	}

	@Override
	public String toString() {
		return JAVADOC_TAG_PREFIX + name().toLowerCase();
	}
}
