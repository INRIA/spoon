/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 *  This file originally comes from JavaParser and is distributed under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 */
package spoon.javadoc.internal;

import java.io.Serializable;

/**
* An inline tag contained in a Javadoc description.
*
* <p>For example <code>{@link String}</code>
*/
public class JavadocInlineTag implements JavadocDescriptionElement, Serializable {

	/** Return the next word of the string, in other words it stops when a space is encountered. */
	public static String nextWord(String string) {
		int index = 0;
		while (index < string.length() && !Character.isWhitespace(string.charAt(index))) {
			index++;
		}
		return string.substring(0, index);
	}

	/** parses a Javadoc tag */
	public static JavadocDescriptionElement fromText(String text) {
		if (!text.startsWith("{@")) {
			throw new IllegalArgumentException(
				String.format("Expected to start with '{@'. Text '%s'", text));
		}
		if (!text.endsWith("}")) {
			throw new IllegalArgumentException(
				String.format("Expected to end with '}'. Text '%s'", text));
		}
		text = text.substring(2, text.length() - 1);
		String tagName = nextWord(text);
		Type type = Type.fromName(tagName);
		String content = text.substring(tagName.length()).trim();
		return new JavadocInlineTag(tagName, type, content);
	}

	/**
		* The type of tag: it could either correspond to a known tag (code, docRoot, etc.) or represent
		* an unknown tag.
		*/
	public enum Type {
		CODE,
		DOC_ROOT,
		INHERIT_DOC,
		LINK,
		LINKPLAIN,
		LITERAL,
		VALUE,
		UNKNOWN;

		Type() {
			this.keyword = name();
		}

		private String keyword;

		static JavadocInlineTag.Type fromName(String tagName) {
			for (JavadocInlineTag.Type t : JavadocInlineTag.Type.values()) {
			if (t.keyword.equals(tagName.toUpperCase())) {
				return t;
			}
			}
			return UNKNOWN;
		}
	}

	private String tagName;
	private Type type;

	public void setContent(String content) {
		this.content = content;
	}

	private String content;

	public JavadocInlineTag(String tagName, Type type, String content) {
		this.tagName = tagName;
		this.type = type;
		this.content = content;
	}

	public Type getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public String getName() {
		return tagName;
	}

	@Override
	public String toText() {
		return "{@" + tagName + " " + this.content + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JavadocInlineTag that = (JavadocInlineTag) o;

		if (tagName != null ? !tagName.equals(that.tagName) : that.tagName != null) {
			return false;
		}
		if (type != that.type) {
			return false;
		}
		return content != null ? content.equals(that.content) : that.content == null;
	}

	@Override
	public int hashCode() {
		int result = tagName != null ? tagName.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "JavadocInlineTag{"
			+ "tagName='"
			+ tagName
			+ '\''
			+ ", type="
			+ type
			+ ", content='"
			+ content
			+ '\''
			+ '}';
	}
	}
