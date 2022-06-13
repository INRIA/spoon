package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.List;

/**
 * A javadoc inline tag (e.g. {@code @literal} or {@code @link}).
 */
public class JavadocInlineTag implements JavadocElement {
	private final List<JavadocElement> elements;
	private final JavadocTagType tagType;

	/**
	 * @param elements the arguments of the tag
	 * @param tagType the type of the tag
	 */
	public JavadocInlineTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.elements = elements;
		this.tagType = tagType;
	}

	/**
	 * @return the type of the tag
	 */
	public JavadocTagType getTagType() {
		return tagType;
	}

	/**
	 * @return the arguments of the tag
	 */
	public List<JavadocElement> getElements() {
		return elements;
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitInlineTag(this);
	}

	@Override
	public String toString() {
		return "JavadocInlineTag{"
			+ "elements=" + elements
			+ ", tagType=" + tagType.getName()
			+ '}';
	}
}
