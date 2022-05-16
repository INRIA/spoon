package spoon.javadoc.external.elements.snippets;

import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;

import java.util.List;
import java.util.Map;

public class JavadocSnippet extends JavadocInlineTag {

	private final JavadocText body;
	private final Map<String, String> attributes;

	public JavadocSnippet(JavadocText body, Map<String, String> attributes) {
		super(List.of(), StandardJavadocTagType.SNIPPET);

		this.body = body;
		this.attributes = attributes;
	}
}
