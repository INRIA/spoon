package spoon.support.util.internal.lexer;

public final class Token {
	private final TokenType type;
	private final int start; // inclusive
	private final int end; // exclusive

	public Token(TokenType type, int start, int end) {
		this.type = type;
		this.start = start;
		this.end = end;
	}

	public TokenType type() {
		return this.type;
	}

	public int start() {
		return this.start;
	}

	public int end() {
		return this.end;
	}

	public String valueForContent(char[] content) {
		return new String(content, this.start, this.end - this.start);
	}

	public String formatted(char[] content) {
		String type = " ".repeat(10 - this.type.name().length()) + this.type.name();
		String s = String.valueOf(this.start);
		String start = " ".repeat(5 - s.length()) + s;
		String e = String.valueOf(this.end);
		String end = " ".repeat(5 - e.length()) + e;
		return "Token[type: " + type + ", start: " + start + ", end: " + end + ", content: " + valueForContent(content);
	}
}
