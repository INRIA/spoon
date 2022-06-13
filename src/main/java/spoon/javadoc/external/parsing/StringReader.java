package spoon.javadoc.external.parsing;

import java.util.function.IntPredicate;

/**
 * A string reader to help with building hand-written parsers.
 */
class StringReader {
	private final String underlying;
	private int position;

	StringReader(String underlying) {
		this(underlying, 0);
	}

	StringReader(String underlying, int position) {
		this.underlying = underlying;
		this.position = position;
	}

	public String readWhile(IntPredicate predicate) {
		int start = position;
		while (canRead() && predicate.test(peek())) {
			position++;
		}

		return underlying.substring(start, position);
	}

	public String readBalancedBraced() {
		if (peek() != '{') {
			throw new RuntimeException(":( no brace at start");
		}
		int start = position + 1;
		int depth = 0;
		do {
			if (peek() == '{') {
				depth++;
			} else if (peek() == '}') {
				depth--;
			}
			position++;

			if (depth == 0) {
				break;
			}
		} while (canRead());

		return underlying.substring(start, position - 1);
	}

	public boolean matches(String needle) {
		return peek(needle.length()).equals(needle);
	}

	public int peek() {
		return underlying.codePointAt(position);
	}

	public String peek(int chars) {
		if (!canRead(chars)) {
			return "";
		}
		return underlying.substring(position, position + chars);
	}

	public boolean peek(String needle) {
		return peek(needle.length()).equals(needle);
	}

	public String read(int amount) {
		int start = position;
		position = Math.min(underlying.length(), position + amount);
		return underlying.substring(start, position);
	}

	public boolean canRead() {
		return canRead(1);
	}

	public boolean canRead(int amount) {
		return position + amount - 1 < underlying.length();
	}

	public StringReader fork() {
		return new StringReader(underlying, position);
	}

	public void read(String needle) {
		if (!matches(needle)) {
			throw new RuntimeException("Expected: " + needle);
		}
		position += needle.length();
	}

	public int remaining() {
		return underlying.length() - position;
	}

	public String readRemaining() {
		return read(remaining());
	}

	public String readLine() {
		String text = readWhile(it -> it != '\n');
		read("\n");

		return text;
	}

	public String readPotentiallyQuoted() {
		if (peek() != '"' && peek() != '\'') {
			return readWhile(it -> !Character.isWhitespace(it));
		}

		int quoteEndChar = peek();
		read(Character.toString(quoteEndChar));
		String text = readWhile(it -> it != quoteEndChar);
		read(Character.toString(quoteEndChar));

		return text;
	}

	public String getUnderlying() {
		return underlying;
	}

	public int getPosition() {
		return position;
	}

	public int getLastLinebreakPosition() {
		for (int i = position - 1; i >= 0; i--) {
			if (underlying.charAt(i) == '\n') {
				return i;
			}
		}
		return -1;
	}
}
