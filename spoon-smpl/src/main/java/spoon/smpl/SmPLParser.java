package spoon.smpl;

import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class SmPLParser {
    public static Formula parse(String smpl) {
        Launcher.parseClass(rewrite(smpl));
        return null;
    }

    public static String prettify(String text) {
        return prettify(text, '{', '}', 4, false);
    }
    public static String prettify(String text, char open, char close, int indentSize, boolean addNewlines) {
        StringBuilder result = new StringBuilder();

        int indent = 0;
        boolean doIndent = false;

        for (char c : text.toCharArray()) {
            if (c == close) {
                indent -= 1;

                if (addNewlines) {
                    result.append('\n');
                    doIndent = true;
                }
            }

            if (doIndent) {
                doIndent = false;

                for (int i = 0; i < indent; ++i) {
                    for (int j = 0; j < indentSize; ++j) {
                        result.append(" ");
                    }
                }
            }

            result.append(c);

            if (c == '\n') {
                doIndent = true;
            }

            if (c == open) {
                indent += 1;

                if (addNewlines) {
                    result.append('\n');
                    doIndent = true;
                }
            }
        }

        return result.toString();
    }

    public static String rewrite(String text) {
        class Result {
            public Result() {
                out = new StringBuilder();
                hasMethodHeader = false;
            }

            public StringBuilder out;
            public boolean hasMethodHeader;
        }

        class RewriteRule {
            public RewriteRule(String name, String regex, Consumer<Stack<List<RewriteRule>>> contextOp, BiConsumer<Result, Matcher> outputOp) {
                this.name = name;
                this.pattern = Pattern.compile(regex);
                this.contextOp = contextOp;
                this.outputOp = outputOp;
            }

            public final String name;
            public final Pattern pattern;
            public final Consumer<Stack<List<RewriteRule>>> contextOp;
            public final BiConsumer<Result, Matcher> outputOp;
        }

        if (text.length() < 1) {
            throw new RuntimeException("Empty input");
        }

        List<RewriteRule> init = new ArrayList<>();
        List<RewriteRule> metavars = new ArrayList<>();
        List<RewriteRule> code = new ArrayList<>();
        List<RewriteRule> body = new ArrayList<>();
        List<RewriteRule> dots = new ArrayList<>();

        // TODO: rule name
        // TODO: escape character
        // TODO: strings

        // Initial context
        init.add(new RewriteRule("atat", "(?s)^@@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> { result.out.append("void __SmPLMetavars__() {\n"); }));

        // Metavars context
        metavars.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));

        metavars.add(new RewriteRule("atat", "(?s)^@@",
                (ctx) -> { ctx.pop(); ctx.push(code); },
                (result, match) -> { result.out.append("}\n"); }));

        metavars.add(new RewriteRule("identifier", "(?s)^identifier\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("identifier(").append(id).append(");\n");
                    }
                }));

        metavars.add(new RewriteRule("type", "(?s)^type\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("type(").append(id).append(");\n");
                    }
                }));

        metavars.add(new RewriteRule("constant", "(?s)^constant\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("constant(").append(id).append(");\n");
                    }
                }));

        // Code context
        code.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));

        code.add(new RewriteRule("method_decl", "(?s)^((public|protected|private|static)\\s+)*(<[A-Za-z,\\s]+>)?\\s*[A-Za-z<>,\\s]+\\s*[A-Za-z]+\\s*\\([^\\{]+\\{",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append(match.group());
                    result.hasMethodHeader = true;
                }));

        code.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> { ctx.pop(); ctx.push(body); ctx.push(dots); },
                (result, match) -> {
                    result.out.append("__SmPLUndeclared__ method() {\n");
                    result.out.append("__SmPLDots__(");
                    result.hasMethodHeader = true;
                }));

        code.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append("__SmPLUndeclared__ method() {\n");
                    result.out.append(match.group());
                    result.hasMethodHeader = true;
                }));

        // Method body context
        body.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> { ctx.push(dots); },
                (result, match) -> { result.out.append("__SmPLDots__("); }));

        body.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> {},
                (result, match) -> { result.out.append(match.group()); }));

        // Dots context
        dots.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));

        dots.add(new RewriteRule("when_neq", "(?s)^when\\s*!=\\s*([a-z]+)",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append("whenNotEqual(").append(match.group(1)).append(")");
                }));

        dots.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); },
                (result, match) -> { result.out.append(");\n").append(match.group()); }));

        Result result = new Result();
        result.out.append("class SmPLRule {\n");

        Stack<List<RewriteRule>> context = new Stack<>();
        context.push(init);
        
        int pos = 0;
        
        while (pos < text.length()) {
            List<String> expected = new ArrayList<>();
            boolean foundSomething = false;
            String texthere = text.substring(pos);

            List<RewriteRule> rules = context.peek();

            for (RewriteRule rule : rules) {
                expected.add(rule.name);
                Matcher matcher = rule.pattern.matcher(texthere);

                if (matcher.find()) {
                    rule.contextOp.accept(context);
                    rule.outputOp.accept(result, matcher);

                    pos += matcher.end();
                    foundSomething = true;
                    break;
                }
            }

            if (!foundSomething) {
                throw new RuntimeException("Parse error at offset " + Integer.toString(pos) + ", expected one of " + expected.toString());
            }
        }

        if (result.hasMethodHeader) {
            result.out.append("}\n");
        }

        result.out.append("}\n");

        return result.out.toString();
    }
}
