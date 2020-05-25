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

import spoon.reflect.code.CtJavaDocTag;

import java.io.Serializable;

/**
* A block tag.
*
* <p>Typically they are found at the end of Javadoc comments.
*
* <p>Examples: <code>@see AnotherClass</code> <code>@since v0.0.1</code> <code>@author Jim O'Java
* </code>
*/
public class JavadocBlockTag implements Serializable {

	private CtJavaDocTag.TagType type;
	private JavadocDescription content;
	private String name = "";
	private String tagName;

	public JavadocBlockTag(CtJavaDocTag.TagType type, String content) {
		this.type = type;
		this.tagName = type.getName();
		this.content = Javadoc.parseText(content);
	}

	public JavadocBlockTag(String tagName, String content) {
		this(CtJavaDocTag.TagType.tagFromName(tagName), content);
		this.tagName = tagName;
	}

	public JavadocBlockTag(String tagName, String paramName, String content) {
		this(CtJavaDocTag.TagType.tagFromName(tagName), content);
		this.tagName = tagName;
		this.name = paramName;
	}

	public CtJavaDocTag.TagType getType() {
		return type;
	}

	public JavadocDescription getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	public String getTagName() {
		return tagName;
	}

	/** pretty-prints the Javadoc tag */
	public String toText() {
		StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(tagName);
		sb.append(" ").append(name);
		if (!content.isEmpty()) {
			sb.append(" ");
			sb.append(content.toText());
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JavadocBlockTag that = (JavadocBlockTag) o;

		if (type != that.type) {
			return false;
		}
		if (!content.equals(that.content)) {
			return false;
		}
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + content.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "JavadocBlockTag{"
			+ "type="
			+ type
			+ ", content='"
			+ content
			+ '\''
			+ ", name="
			+ name
			+ '}';
	}
}
