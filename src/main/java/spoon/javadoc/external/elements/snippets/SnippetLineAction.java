package spoon.javadoc.external.elements.snippets;

import java.util.Map;

public class SnippetLineAction {
	private final SnippetRegion region;
	private final String name;
	private final Map<String, String> attributes;

	public SnippetLineAction(SnippetRegion region, String name, Map<String, String> attributes) {
		this.region = region;
		this.name = name;
		this.attributes = attributes;
	}
}
