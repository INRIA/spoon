package spoon.javadoc.external;

import java.util.function.IntPredicate;

public class StringReader {
	private final String underlying;
	private int position;

	public StringReader(String underlying) {
		this.underlying = underlying;
	}

	private StringReader(String underlying, int position) {
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
}
