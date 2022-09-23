package spoon.support.util.internal.lexer;

public final class Token {
	private static final int COLUMN_WIDTH = 8;
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

	public int length() {
		return end - start;
	}

	public String valueForContent(char[] content) {
		return new String(content, this.start, this.end - this.start);
	}

	public String formatted(char[] content) {
		String type = " ".repeat(10 - this.type.name().length()) + this.type.name();
		String s = String.valueOf(this.start);
		String start = padFailsafe(COLUMN_WIDTH, s);
		String e = String.valueOf(this.end);
		String end = padFailsafe(COLUMN_WIDTH, e);
		return "Token[type: " + type + ", start: " + start + ", end: " + end + ", content: " + valueForContent(content);
	}


	private static String padFailsafe(int width, String content) {
		int count = width - content.length();
		return " ".repeat(Math.max(0, count)) + content;
	}
}
