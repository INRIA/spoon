package spoon.support.adaption;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class NodePrintHelper {

	private NodePrintHelper() {
		throw new AssertionError("No instantiation");
	}

	/**
	 * Encloses a string in double quotes. Does not escape inner quotation marks.
	 * Special-purpose helper for {@link DeclarationNode} and {@link GlueNode}.
	 *
	 * @param input the string to quote
	 * @return the string surrounded with quotes
	 */
	public static String quote(String input) {
		return '"' + input + '"';
	}

	/**
	 * Converts a list of values to a crudely formatted JSON-like array.
	 * <br><br>
	 * Special-purpose helper for {@link DeclarationNode} and {@link GlueNode},
	 * it <em>does not produce valid JSON in other contexts!</em>
	 *
	 * @param input the input list
	 * @return the list as a quoted array
	 */
	public static String toJsonLikeArray(List<?> input) {
		return "["
			+ input.stream()
			.map(Objects::toString)
			.map(NodePrintHelper::quote)
			.collect(Collectors.joining(", "))
			+ "]";
	}
}
