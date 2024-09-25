/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

import java.util.Objects;

/**
 * Normal text appearing in a javadoc comment.
 */
public class JavadocText implements JavadocElement {

	private final String text;

	/**
	 * @param text the represented text
	 */
	public JavadocText(String text) {
		this.text = text;
	}

	/**
	 * @return the represented text
	 */
	public String getText() {
		return text;
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitText(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavadocText that = (JavadocText) o;
		return Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text);
	}

	@Override
	public String toString() {
		return "JavadocText{"
		       + "text='" + text + '\''
		       + '}';
	}
}
