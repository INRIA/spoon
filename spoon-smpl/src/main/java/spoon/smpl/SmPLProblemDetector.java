package spoon.smpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SmPLProblemDetector provides static analysis problem detection on SmPL patch code.
 */
public class SmPLProblemDetector {
    /**
     * Type/severity of problem.
     */
    public enum ProblemType {
        /**
         * Warning, likely recoverable.
         */
        Warning,

        /**
         * Error, likely unrecoverable.
         */
        Error
    }

    /**
     * A Problem notification consists of a type/severity and a message.
     */
    public static class Problem {
        /**
         * Create a new Problem notification.
         *
         * @param type Type/severity of problem
         * @param message Message describing the problem
         */
        public Problem(ProblemType type, String message) {
            this.type = type;
            this.message = message;
        }

        @Override
        public String toString() {
            return type.toString() + ": " + message;
        }

        /**
         * Type/severity of problem.
         */
        public final ProblemType type;

        /**
         * Message describing the problem.
         */
        public final String message;
    }

    /**
     * Detect problems in plain text SmPL patch code.
     *
     * @param smpl Plain text SmPL patch code
     * @return List of detected problems
     */
    public static List<Problem> detectProblems(String smpl) {
        return null;
    }

    /**
     * Detect problems in token stream produced by SmPLLexer.
     *
     * @param tokens SmPLLexer Token stream
     * @return List of detected problems
     */
    public static List<Problem> detectProblems(List<SmPLLexer.Token> tokens) {
        List<Problem> problems = new ArrayList<>();

        SmPLLexer.Token firstBodyToken = nextNonMatch(tokens, 0,
                                                      SmPLLexer.TokenType.Newline,
                                                      SmPLLexer.TokenType.Rulename,
                                                      SmPLLexer.TokenType.MetavarType,
                                                      SmPLLexer.TokenType.MetavarIdentifier);

        if (firstBodyToken != null && (firstBodyToken.getType() == SmPLLexer.TokenType.Dots || firstBodyToken.getType() == SmPLLexer.TokenType.OptDotsBegin)) {
            problems.add(new Problem(ProblemType.Error, "Superfluous dots operator at " + firstBodyToken.getPosition().toString()));
        }

        for (int i = 0; i < tokens.size() - 1; ++i) {
            SmPLLexer.Token t1 = tokens.get(i);

            if (t1.getType() == SmPLLexer.TokenType.Dots) {
                SmPLLexer.Token t2 = nextNonMatch(tokens, i + 1, SmPLLexer.TokenType.Newline,
                                                                 SmPLLexer.TokenType.WhenAny,
                                                                 SmPLLexer.TokenType.WhenExists,
                                                                 SmPLLexer.TokenType.WhenNotEqual);

                if (t2 != null && t2.getType() == SmPLLexer.TokenType.Dots) {
                    problems.add(new Problem(ProblemType.Error, "Consecutive dots operators at " + t1.getPosition().toString()));
                }
            }
        }

        return problems;
    }

    /**
     * Filter a list of tokens to include only the given types.
     *
     * @param tokens List of tokens to filter
     * @param ts Types to retain
     * @return List of tokens filtered to only retain the given types
     */
    private static List<SmPLLexer.Token> retain(List<SmPLLexer.Token> tokens, SmPLLexer.TokenType ... ts) {
        return tokens.stream().filter(t -> Set.of(ts).contains(t.getType())).collect(Collectors.toList());
    }

    /**
     * Filter a list of tokens to remove the given types.
     *
     * @param tokens List of tokens to filter
     * @param ts Types to remove
     * @return List of tokens filtered to remove the given types
     */
    private static List<SmPLLexer.Token> remove(List<SmPLLexer.Token> tokens, SmPLLexer.TokenType ... ts) {
        return tokens.stream().filter(t -> !Set.of(ts).contains(t.getType())).collect(Collectors.toList());
    }

    /**
     * Find next matching token of specific type(s).
     *
     * @param tokens List of tokens to search
     * @param startIndex List index where search should start
     * @param matchTypes Types to match
     * @return First matching token, or null if there was no token matching the given type(s)
     */
    private static SmPLLexer.Token nextMatch(List<SmPLLexer.Token> tokens, int startIndex, SmPLLexer.TokenType ... matchTypes) {
        List<SmPLLexer.Token> stuff = retain(tokens.subList(startIndex, tokens.size() - 1), matchTypes);
        return stuff.isEmpty() ? null : stuff.get(0);
    }

    /**
     * Find next token that does not match the given specific type(s).
     *
     * @param tokens List of tokens to search
     * @param startIndex List index where search should start
     * @param matchTypes Types to ignore
     * @return First token of some other type, or null if there was no such token
     */
    private static SmPLLexer.Token nextNonMatch(List<SmPLLexer.Token> tokens, int startIndex, SmPLLexer.TokenType ... matchTypes) {
        List<SmPLLexer.Token> stuff = remove(tokens.subList(startIndex, tokens.size() - 1), matchTypes);
        return stuff.isEmpty() ? null : stuff.get(0);
    }
}
