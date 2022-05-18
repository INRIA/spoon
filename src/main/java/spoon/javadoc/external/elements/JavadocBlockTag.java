package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.List;

public class JavadocBlockTag implements JavadocElement {
	private final JavadocTagType tagType;
	private final List<JavadocElement> elements;

	public JavadocBlockTag(JavadocTagType tagType, List<JavadocElement> elements) {
		this.tagType = tagType;
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "JavadocBlockTag{" +
			"tagType=" + tagType.getName() +
			", elements=" + elements +
			'}';
	}
}
