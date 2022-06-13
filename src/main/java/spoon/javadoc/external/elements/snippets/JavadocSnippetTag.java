package spoon.javadoc.external.elements.snippets;

import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.elements.JavadocVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An {@code @snippet} inline tag.
 * <p>
 * This class also contains the attributes of the tag and the body as its first element.
 * The body can be parsed using the {@link spoon.javadoc.external.parsing.SnippetFileParser}.
 */
public class JavadocSnippetTag extends JavadocInlineTag {

	private final Map<String, String> attributes;

	/**
	 * Creates a new snippet inline tag with the given body and attributes.
	 *
	 * @param body the body of the snippet
	 * @param attributes the attributes of the snippet
	 */
	public JavadocSnippetTag(JavadocText body, Map<String, String> attributes) {
		super(List.of(body), StandardJavadocTagType.SNIPPET);

		this.attributes = attributes;
	}

	/**
	 * @return the snippet attributes
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitSnippet(this);
	}
}
