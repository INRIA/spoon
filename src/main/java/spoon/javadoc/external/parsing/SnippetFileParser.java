package spoon.javadoc.external.parsing;

import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.snippets.SnippetRegionType;
import spoon.javadoc.external.elements.snippets.JavadocSnippetTag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class SnippetFileParser {
	private final List<String> lines;
	private final Deque<OpenRegion> openRegions;

	public SnippetFileParser(List<String> lines) {
		this.lines = lines;
		this.openRegions = new ArrayDeque<>();
	}

	public List<JavadocSnippetTag> parse() {
		List<JavadocSnippetTag> regions = new ArrayList<>();
		boolean closeOnNext = false;

		for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
			if (closeOnNext && !openRegions.isEmpty()) {
				endClosestRegion(lineNumber).ifPresent(regions::add);
				closeOnNext = false;
			}

			StringReader line = new StringReader(lines.get(lineNumber));
			line.readWhile(it -> it != '@');
			if (!line.peek("@")) {
				continue;
			}
			line.read("@");
			String tag = line.readWhile(it -> !Character.isWhitespace(it));

			if (tag.equalsIgnoreCase("end")) {
				endRegion(line, lineNumber).ifPresent(regions::add);
				continue;
			}

			Optional<SnippetRegionType> regionType = SnippetRegionType.fromString(tag.strip().toLowerCase(Locale.ROOT));
			if (regionType.isEmpty()) {
				continue;
			}

			Map<String, String> attributes = InlineTagParser.parseSnippetAttributes(
				new StringReader(line.readRemaining())
			);
			boolean forNextLine = line.getUnderlying().stripTrailing().endsWith(":");
			closeOnNext = forNextLine && regionType.get() != SnippetRegionType.START;

			int startLine = forNextLine ? lineNumber + 1 : lineNumber;
			openRegions.push(new OpenRegion(startLine, attributes, regionType.get()));

			// A one-line region
			if (!attributes.containsKey("region") && regionType.get() != SnippetRegionType.START && !closeOnNext) {
				endRegion(line, lineNumber).ifPresent(regions::add);
			}
		}


		for (int i = 0, end = openRegions.size(); i < end; i++) {
			endClosestRegion(lines.size()).ifPresent(regions::add);
		}

		return regions;
	}

	private Optional<JavadocSnippetTag> endRegion(StringReader reader, int line) {
		reader.readWhile(it -> Character.isWhitespace(it) && it != '\n');
		if (openRegions.isEmpty()) {
			return Optional.empty();
		}

		if (reader.peek("\n") || !reader.canRead()) {
			return endClosestRegion(line);
		}

		Map<String, String> attributes = InlineTagParser.parseSnippetAttributes(
			new StringReader(reader.readWhile(it -> it != '\n'))
		);
		String regionName = attributes.get("region");

		return openRegions.stream()
			.filter(it -> it.name.equals(regionName))
			.findFirst()
			.stream()
			.peek(openRegions::remove)
			.findFirst()
			.map(it -> it.close(line));
	}

	private Optional<JavadocSnippetTag> endClosestRegion(int endLine) {
		// end without argument
		OpenRegion openRegion = openRegions.pop();
		return Optional.of(openRegion.close(endLine));
	}

	private static class OpenRegion {
		private final int startLine;
		private final String name;
		private final Map<String, String> attributes;
		private final SnippetRegionType type;

		private OpenRegion(int startLine, Map<String, String> attributes, SnippetRegionType type) {
			this.startLine = startLine;
			this.name = attributes.getOrDefault("region", "");
			this.attributes = attributes;
			this.type = type;
		}

		public JavadocSnippetTag close(int endLine) {
			return new JavadocSnippetTag(name, startLine, endLine, attributes, type);
		}
	}
}
