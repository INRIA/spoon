package spoon.javadoc.external.elements.snippets;

import java.util.Map;

public class SnippetRegion {
	private final String name;
	private final int lineStart;
	private final int lineEnd;
	private final Map<String, String> attributes;

	public SnippetRegion(String name, int lineStart, int lineEnd, Map<String, String> attributes) {
		this.name = name;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		this.attributes = attributes;
	}
}
