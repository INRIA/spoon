package spoon.javadoc.external.elements.snippets;

import java.util.Map;

public class JavadocSnippetMarkupRegion {
	private final String name;
	private final int startLine;
	private final int endLine;
	private final Map<String, String> attributes;
	private final JavadocSnippetRegionType type;

	public JavadocSnippetMarkupRegion(
		String name,
		int startLine,
		int endLine,
		Map<String, String> attributes,
		JavadocSnippetRegionType type
	) {
		this.name = name;
		this.startLine = startLine;
		this.endLine = endLine;
		this.attributes = attributes;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public JavadocSnippetRegionType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SnippetTag{"
			+ "name='" + name + '\''
			+ ", startLine=" + startLine
			+ ", endLine=" + endLine
			+ ", attributes=" + attributes
			+ ", type=" + type
			+ '}';
	}
}
