package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.List;

// TODO: WellKnown.Inline.link(CtTypeReference, "Label");
public class JavadocInlineTag implements JavadocElement {
	private final List<JavadocElement> elements;
	private final JavadocTagType tagType;

	public JavadocInlineTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.elements = elements;
		this.tagType = tagType;
	}

	public JavadocTagType getTagType() {
		return tagType;
	}

	public List<JavadocElement> getElements() {
		return elements;
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitInlineTag(this);
	}

	@Override
	public String toString() {
		return "JavadocInlineTag{"
			+ "elements=" + elements
			+ ", tagType=" + tagType.getName()
			+ '}';
	}
}
