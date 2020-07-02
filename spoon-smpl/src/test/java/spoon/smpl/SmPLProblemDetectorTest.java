package spoon.smpl;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

import spoon.smpl.SmPLProblemDetector.Problem;

public class SmPLProblemDetectorTest {
    @Test
    public void testDetectLeadingSuperfluousStatementDotsOperator() {

        // contract: a superfluous statement dots operator at the top of a patch using implicit dots (not matching on method header) should be reported as an error

        String smpl = "@@ @@\n" +
                      "...\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Superfluous dots operator"));
    }

    @Test
    public void testDetectLeadingSuperfluousOptDotsOperator() {

        // contract: a superfluous optdots operator at the top of a patch using implicit dots (not matching on method header) should be reported as an error

        String smpl = "@@ @@\n" +
                      "<...\n" +
                      "...>\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Superfluous dots operator"));
    }

    @Test
    public void testDetectConsecutiveStatementDotsOperatorsPlain() {

        // contract: consecutive statement dots operators should be reported as an error

        String smpl = "@@ @@\n" +
                      "void m() {\n" +
                      "...\n" +
                      "...\n" +
                      "}\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
    }

    @Test
    public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsUpper() {

        // contract: consecutive statement dots operators should be reported as an error

        String smpl = "@@ @@\n" +
                      "void m() {\n" +
                      "... when any\n" +
                      "...\n" +
                      "}\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
    }

    @Test
    public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsLower() {

        // contract: consecutive statement dots operators should be reported as an error

        String smpl = "@@ @@\n" +
                      "void m() {\n" +
                      "...\n" +
                      "... when exists\n" +
                      "}\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
    }

    @Test
    public void testDetectConsecutiveStatementDotsOperatorsWithConstraintsBoth() {

        // contract: consecutive statement dots operators should be reported as an error

        String smpl = "@@ @@\n" +
                      "void m() {\n" +
                      "... when any\n" +
                      "... when exists\n" +
                      "}\n" +
                      "\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Consecutive dots operators"));
    }

    @Test
    public void testDetectStatementDotsInDisjunction() {

        // contract: statement dots operators inside a pattern disjunction should be reported as an error

        String smpl = "@@ @@\n" +
                      "(\n" +
                      "foo();\n" +
                      "|\n" +
                      "...\n" +
                      ")\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Dots operator in pattern disjunction"));
    }

    @Test
    public void testDetectOptDotsInDisjunction() {

        // contract: optdots blocks inside a pattern disjunction should be reported as an error

        String smpl = "@@ @@\n" +
                      "(\n" +
                      "foo();\n" +
                      "|\n" +
                      "<...\n" +
                      "bar();\n" +
                      "...>\n" +
                      ")\n";

        List<Problem> problems = SmPLProblemDetector.detectProblems(SmPLLexer.lex(smpl));
        assertTrue(problems.toString().contains("Error: Dots operator in pattern disjunction"));
    }
}
