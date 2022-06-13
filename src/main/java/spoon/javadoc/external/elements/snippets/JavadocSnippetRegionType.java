package spoon.javadoc.external.elements.snippets;

import java.util.Optional;

/**
 * The type of a javadoc snippet markup region.
 */
public enum JavadocSnippetRegionType {
	HIGHLIGHT("highlight"),
	LINK("link"),
	REPLACE("replace"),
	START("start");

	private final String asString;

	JavadocSnippetRegionType(String asString) {
		this.asString = asString;
	}

	@Override
	public String toString() {
		return asString;
	}

	/**
	 * Parses the snippet region type from a case-insensitive string.
	 *
	 * @param input the tag name
	 * @return the matching region type, if any
	 */
	public static Optional<JavadocSnippetRegionType> fromString(String input) {
		for (JavadocSnippetRegionType type : values()) {
			if (type.asString.equalsIgnoreCase(input)) {
				return Optional.of(type);
			}
		}

		return Optional.empty();
	}
}
