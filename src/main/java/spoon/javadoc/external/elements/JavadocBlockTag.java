package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.Collections;
import java.util.List;

public class JavadocBlockTag implements JavadocElement {
	private final JavadocTagType tagType;
	private final List<JavadocElement> elements;

	public JavadocBlockTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.tagType = tagType;
		this.elements = elements;
	}

	public JavadocTagType getTagType() {
		return tagType;
	}

	public List<JavadocElement> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitBlockTag(this);
	}

	@Override
	public String toString() {
		return "JavadocBlockTag{" +
			"tagType=" + tagType.getName() +
			", elements=" + elements +
			'}';
	}
}
