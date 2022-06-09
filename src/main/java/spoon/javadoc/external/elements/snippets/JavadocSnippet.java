package spoon.javadoc.external.elements.snippets;

import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.elements.JavadocVisitor;

import java.util.List;
import java.util.Map;

public class JavadocSnippet extends JavadocInlineTag {

	private final Map<String, String> attributes;

	public JavadocSnippet(JavadocText body, Map<String, String> attributes) {
		super(List.of(body), StandardJavadocTagType.SNIPPET);

		this.attributes = attributes;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitSnippet(this);
	}
}
