package spoon.javadoc.external.elements.snippets;

import spoon.javadoc.external.parsing.SnippetFileParser;

import java.util.List;
import java.util.stream.Collectors;

public class JavadocSnippetBody {

	private final List<JavadocSnippetTag> tags;
	private final List<String> lines;

	public JavadocSnippetBody(List<String> lines, List<JavadocSnippetTag> tags) {
		this.tags = tags;
		this.lines = lines;
	}

	public List<JavadocSnippetTag> getActiveTagsAtLine(int line) {
		return tags.stream()
			.filter(it -> line >= it.getStartLine() && line <= it.getEndLine())
			.collect(Collectors.toList());
	}

	public List<String> getLines() {
		return lines;
	}

	public List<JavadocSnippetTag> getTags() {
		return tags;
	}

	public static JavadocSnippetBody fromString(String text) {
		List<String> lines = text.lines().collect(Collectors.toList());
		List<JavadocSnippetTag> tags = new SnippetFileParser(lines).parse();

		return new JavadocSnippetBody(lines, tags);
	}
}
