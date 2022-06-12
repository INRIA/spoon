package spoon.javadoc.external.elements.snippets;

import java.util.Optional;

public enum SnippetRegionType {
	HIGHLIGHT("highlight"),
	LINK("link"),
	REPLACE("replace"),
	START("start");

	private final String asString;

	SnippetRegionType(String asString) {
		this.asString = asString;
	}

	@Override
	public String toString() {
		return asString;
	}

	public static Optional<SnippetRegionType> fromString(String input) {
		for (SnippetRegionType type : values()) {
			if (type.asString.equals(input)) {
				return Optional.of(type);
			}
		}

		return Optional.empty();
	}
}
