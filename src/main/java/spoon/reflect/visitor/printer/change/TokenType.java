package spoon.reflect.visitor.printer.change;

enum TokenType {

	SEPARATOR(false, false),
	OPERATOR(false, false),
	LITERAL(false, false),
	KEYWORD(false, false),
	IDENTIFIER(false, false),
	CODE_SNIPPET(false, false),
	COMMENT(false, false),
	NEW_LINE(true, false),
	INC_TAB(true, true),
	DEC_TAB(true, true),
	SPACE(true, false);

	private final boolean whiteSpace;
	private final boolean tab;

	TokenType(boolean whiteSpace, boolean tab) {
		this.whiteSpace = whiteSpace;
		this.tab = tab;
	}
	boolean isWhiteSpace() {
		return whiteSpace;
	}
	public boolean isTab() {
		return tab;
	}
}
