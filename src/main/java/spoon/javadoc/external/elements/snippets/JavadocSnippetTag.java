package spoon.javadoc.external.elements.snippets;

import java.util.Map;

public class JavadocSnippetTag {
	private final String name;
	private final int startLine;
	private final int endLine;
	private final Map<String, String> attributes;
	private final SnippetRegionType type;

	public JavadocSnippetTag(
		String name,
		int startLine,
		int endLine,
		Map<String, String> attributes,
		SnippetRegionType type
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

	public SnippetRegionType getType() {
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
