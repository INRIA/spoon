package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.Collections;
import java.util.List;

/**
 * A javadoc block tag (like {@code @author} or {@code @see}.
 */
public class JavadocBlockTag implements JavadocElement {
	private final JavadocTagType tagType;
	private final List<JavadocElement> elements;

	/**
	 * @param elements the arguments and content
	 * @param tagType the type of the tag
	 */
	public JavadocBlockTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.tagType = tagType;
		this.elements = elements;
	}

	/**
	 * @return the tag type (e.g. {@code @version}
	 */
	public JavadocTagType getTagType() {
		return tagType;
	}

	/**
	 * @return the content of the tag. Potential arguments are the first element, content is the rest.
	 */
	public List<JavadocElement> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitBlockTag(this);
	}

	@Override
	public String toString() {
		return "JavadocBlockTag{"
			+ "tagType=" + tagType.getName()
			+ ", elements=" + elements
			+ '}';
	}
}
