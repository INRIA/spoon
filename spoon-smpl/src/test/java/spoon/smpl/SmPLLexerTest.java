package spoon.smpl;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SmPLLexerTest {
    @Test
    public void testTokenPosition() {

        // contract: SmPLLexer should record sensible source positions for tokens

        String smpl = "@@ identifier x; @@\n" +
                      "a();\n" +
                      "    b();\n" +
                      "\t\tc();\n" +
                      "\n";

        List<SmPLLexer.Token> tokens = SmPLLexer.lex(smpl);

        for (SmPLLexer.Token token : tokens) {
            if (token.toString().contains("x")) {
                assertEquals(1, token.getPosition().getLine());
                assertEquals(15, token.getPosition().getColumn());
            } else if (token.toString().contains("a()")) {
                assertEquals(2, token.getPosition().getLine());
                assertEquals(1, token.getPosition().getColumn());
            } else if (token.toString().contains("b()")) {
                assertEquals(3, token.getPosition().getLine());
                assertEquals(5, token.getPosition().getColumn());
            } else if (token.toString().contains("c()")) {
                assertEquals(4, token.getPosition().getLine());
                assertEquals(9, token.getPosition().getColumn());
            }
        }
    }
}
