package spoon.support.util.internal.lexer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertThat(first.type()).isEqualTo(TokenType.LITERAL);
		assertThat(first.end()).isEqualTo(raw.length());
		assertThat(javaLexer.lex()).isNull(); // EOF
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
		assertThat(first.type()).isEqualTo(TokenType.LITERAL);
		assertThat(first.end()).isEqualTo(raw.length());
		assertThat(javaLexer.lex()).isNull(); // EOF
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
			non,1
			non-,2
			non-se,3
			non-sea,3
			non-seal,3
			non-seale,3
			non-sealef,3
			non-sealed2,3
			""")
	void testNonSealedInvalid(String raw, int tokens) {
		JavaLexer javaLexer = new JavaLexer(raw.toCharArray(), 0, raw.length());
		Token first = javaLexer.lex();
		assertThat(first.type()).isEqualTo(TokenType.IDENTIFIER);
		Token second = javaLexer.lex();
		if (tokens == 1) {
			assertThat(second).isNull();
			return;
		}
		assertThat(second.type()).isEqualTo(TokenType.OPERATOR);
		Token third = javaLexer.lex();
		if (tokens == 2) {
			assertThat(third).isNull();
			return;
		}
		assertThat(third.type()).isEqualTo(TokenType.IDENTIFIER);
		assertThat(javaLexer.lex()).isNull();
	}

	@Test
	void testNonSealedSeparatedByWhitespace() {
		String raw = "non - sealed";
		JavaLexer javaLexer = new JavaLexer(raw.toCharArray(), 0, raw.length());
		Token first = javaLexer.lex();
		assertThat(first.type()).isEqualTo(TokenType.IDENTIFIER);
		Token second = javaLexer.lex();
		assertThat(second).isNotNull();
		assertThat(second.type()).isEqualTo(TokenType.OPERATOR);
		Token third = javaLexer.lex();
		assertThat(third).isNotNull();
		// sealed is a keyword in this case
		assertThat(third.type()).isEqualTo(TokenType.KEYWORD);
		assertThat(javaLexer.lex()).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"non-sealed",
			"non-sealed ?",
			"non-sealed?",
			"non-sealed-",
	})
	void testNonSealed(String raw) {
		JavaLexer javaLexer = new JavaLexer(raw.toCharArray(), 0, raw.length());
		Token first = javaLexer.lex();
		assertThat(first.type()).isEqualTo(TokenType.KEYWORD);
	}
}
