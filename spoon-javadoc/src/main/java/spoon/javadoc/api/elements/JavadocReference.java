/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

import java.util.Objects;
import spoon.reflect.reference.CtReference;

/**
 * A reference to a java element inside a javadoc comment.
 * <p>
 * This is typically a {@code {@link Element}} inline tag or a {@code {@see Element}} block tag.
 */
public class JavadocReference implements JavadocElement {

	private final CtReference reference;

	/**
	 * @param reference the underlying reference
	 */
	public JavadocReference(CtReference reference) {
		this.reference = reference;
	}

	/**
	 * @return the reference to the java element
	 */
	public CtReference getReference() {
		return reference;
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitReference(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavadocReference that = (JavadocReference) o;
		return Objects.equals(reference, that.reference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(reference);
	}

	@Override
	public String toString() {
		return "JavadocReference{"
		       + ", reference=" + reference
		       + '}';
	}
}
