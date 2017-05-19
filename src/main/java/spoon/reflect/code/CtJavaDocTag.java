/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.declaration.CtElement;

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
		AUTHOR,
		DEPRECATED,
		EXCEPTION,
		PARAM,
		RETURN,
		SEE,
		SERIAL,
		SERIAL_DATA,
		SERIAL_FIELD,
		SINCE,
		THROWS,
		VERSION,
		UNKNOWN;

		/**
		 * Return true if the tag can have a parameter
		 * @return true if the tag can have a parameter
		 */
		public boolean hasParam() {
			return this == PARAM || this == THROWS;
		}

		/**
		 * Get the tag type associated to a name
		 * @param tagName the tag name
		 * @return the tag type
		 */
		public static TagType tagFromName(String tagName) {
			for (TagType t : TagType.values()) {
				if (t.name().toLowerCase().equals(tagName.toLowerCase())) {
					return t;
				}
			}
			return UNKNOWN;
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
	TagType getType();

	/**
	 * Define the type of the tag
	 * @param type the type name
	 */
	<E extends CtJavaDocTag> E setType(String type);

	/**
	 * Define the type of the tag
	 * @param type the new type
	 */
	<E extends CtJavaDocTag> E setType(TagType type);

	/**
	 * Get the content of the atg
	 * @return the content of the tag
	 */
	String getContent();

	/**
	 * Define the content of the tag
	 * @param content the new content of the tag
	 */
	<E extends CtJavaDocTag> E setContent(String content);

	/**
	 * Get the parameter of the tag return null when none is specified (only for @param and @throws)
	 * @return the parameter
	 */
	String getParam();

	/**
	 * Define a parameter
	 * @param param the parameter
	 */
	<E extends CtJavaDocTag> E setParam(String param);

	@Override
	CtJavaDocTag clone();
}
