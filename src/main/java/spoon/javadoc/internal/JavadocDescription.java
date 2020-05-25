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
	import java.util.LinkedList;
	import java.util.List;

	/**
	* A javadoc text, potentially containing inline tags.
	*/
	public class JavadocDescription implements Serializable {

	private List<JavadocDescriptionElement> elements;

	public JavadocDescription() {
		elements = new LinkedList<>();
	}

	public JavadocDescription(List<JavadocDescriptionElement> elements) {
		this();

		this.elements.addAll(elements);
	}

	public boolean addElement(JavadocDescriptionElement element) {
		return this.elements.add(element);
	}

	public List<JavadocDescriptionElement> getElements() {
		return this.elements;
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		elements.forEach(e -> sb.append(e.toText()));
		return sb.toString();
	}

	public boolean isEmpty() {
		return toText().isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		JavadocDescription that = (JavadocDescription) o;

		return elements.equals(that.elements);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public String toString() {
		return "JavadocDescription{" + "elements=" + elements + '}';
	}
	}
