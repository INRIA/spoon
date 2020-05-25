/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.Arrays;

import static spoon.reflect.path.CtRole.COMMENT_CONTENT;
import static spoon.reflect.path.CtRole.JAVADOC_TAG_VALUE;
import static spoon.reflect.path.CtRole.DOCUMENTATION_TYPE;

/**
 * This code element defines a javadoc tag
 *
 * Example:
 * <code>
 * @since name description
 * </code>
 */
public interface CtJavaDocTag extends CtElement {

	/**
	 * The tag prefix
	 */
	String JAVADOC_TAG_PREFIX = "@";

	/**
	 * Define the possible type for a tag
	 */
	enum TagType {
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

		TagType(String name) {
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
		public static TagType tagFromName(String tagName) {
			return Arrays.stream(TagType.values())
					.filter(v -> v.name.equals(tagName))
					.findFirst().orElse(UNKNOWN);
		}

		@Override
		public String toString() {
			return JAVADOC_TAG_PREFIX + name().toLowerCase();
		}
	}

	/**
	 * The type of the tag
	 * @return the type of the tag
	 */
	@PropertyGetter(role = DOCUMENTATION_TYPE)
	TagType getType();

	/**
	 * Define the type of the tag
	 * @param type the type name
	 */
	@PropertySetter(role = DOCUMENTATION_TYPE)
	<E extends CtJavaDocTag> E setType(String type);

	/**
	 * Define the type of the tag
	 * @param type the new type
	 */
	@PropertySetter(role = DOCUMENTATION_TYPE)
	<E extends CtJavaDocTag> E setType(TagType type);

	/**
	 * Get the content of the atg
	 * @return the content of the tag
	 */
	@PropertyGetter(role = COMMENT_CONTENT)
	String getContent();

	/**
	 * Define the content of the tag
	 * @param content the new content of the tag
	 */
	@PropertySetter(role = COMMENT_CONTENT)
	<E extends CtJavaDocTag> E setContent(String content);

	/**
	 * Get the parameter of the tag return null when none is specified (only for @param and @throws)
	 * @return the parameter
	 */
	@PropertyGetter(role = JAVADOC_TAG_VALUE)
	String getParam();

	/**
	 * Define a parameter
	 * @param param the parameter
	 */
	@PropertySetter(role = JAVADOC_TAG_VALUE)
	<E extends CtJavaDocTag> E setParam(String param);

	@Override
	CtJavaDocTag clone();
}
