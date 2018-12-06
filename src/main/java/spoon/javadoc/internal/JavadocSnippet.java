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
* A piece of text inside a Javadoc description.
*
* <p>For example in <code>A class totally unrelated to {@link String}, I swear!</code> we would
* have two snippets: one before and one after the inline tag (<code>{@link String}</code>).
*/
public class JavadocSnippet implements JavadocDescriptionElement, Serializable {
	private String text;

	public JavadocSnippet(String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		if (text.startsWith("/**")) {
			text = text.substring(3);
		}
		this.text = text;
	}

	@Override
	public String toText() {
		return this.text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JavadocSnippet that = (JavadocSnippet) o;

		return text.equals(that.text);
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public String toString() {
		return "JavadocSnippet{" + "text='" + text + '\'' + '}';
	}
}
