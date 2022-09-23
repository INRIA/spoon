package spoon.support.util.internal.lexer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JavaLexerTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"\"Simple\"",
			"\"\\\\\"",
			"\"\\n\"",
			"\"\"",
			"\"\"\"\n\\\"\"\"\n\"\"\"",
			"\"\"\"\n\\\"\"\"\n \"\"\"",
	})
	void testEscapedString(String raw) {
		JavaLexer javaLexer = new JavaLexer(raw.toCharArray(), 0, raw.length());
		Token first = javaLexer.lex();
		assertThat(first.type(), equalTo(TokenType.LITERAL));
		assertThat(first.end(), equalTo(raw.length()));
		assertNull(javaLexer.lex()); // EOF
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"0",
			"00",
			"10",
			"0x0",
			"0X0",
			"0xF",
			"0xF",
			"0X.0p+0f",
			"0X1f.0eEfaP+0123d",
			"0b0L",
			"1f",
			"1D",
	})
	void testNumericLiterals(String raw) {
		JavaLexer javaLexer = new JavaLexer(raw.toCharArray(), 0, raw.length());
		Token first = javaLexer.lex();
		assertThat(first.type(), equalTo(TokenType.LITERAL));
		assertThat(first.end(), equalTo(raw.length()));
		assertNull(javaLexer.lex()); // EOF
	}
}
