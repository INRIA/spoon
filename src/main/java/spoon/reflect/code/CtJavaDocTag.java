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

	String JAVADOC_TAG_PREFIX = "@";

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

		TagType() {
			this.keyword = name().toLowerCase();
		}

		private String keyword;

		public boolean hasParam() {
			return this == PARAM || this == THROWS;
		}

		public String getKeyword() {
			return keyword;
		}

		public static TagType fromName(String tagName) {
			for (TagType t : TagType.values()) {
				if (t.keyword.equals(tagName)) {
					return t;
				}
			}
			return UNKNOWN;
		}
	}


	TagType getName();

	<E extends CtJavaDocTag> E setName(String name);

	<E extends CtJavaDocTag> E setName(TagType name);

	String getContent();

	<E extends CtJavaDocTag> E setContent(String content);

	String getParam();

	<E extends CtJavaDocTag> E setParam(String param);

	@Override
	CtJavaDocTag clone();
}
