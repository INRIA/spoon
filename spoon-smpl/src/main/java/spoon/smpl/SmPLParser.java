package spoon.smpl;

import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * SmPLParser contains methods for rewriting SmPL text input to an SmPL Java DSL (domain-specific language)
 * and to compile this DSL into an SmPLRule instance.
 */
public class SmPLParser {
    /**
     * Parse an SmPL rule given in plain text.
     * @param smpl SmPL rule in plain text
     * @return SmPLRule instance corresponding to input
     */
    public static SmPLRule parse(String smpl) {
        return compile(Launcher.parseClass(rewrite(smpl)));
    }

    /**
     * Compile a given AST in the SmPL Java DSL.
     * @param ast AST to compile
     * @return SmPLRule instance
     */
    public static SmPLRule compile(CtClass<?> ast) {
        String ruleName = null;

        if (ast.getDeclaredField("__SmPLRuleName__") != null) {
            ruleName = ((CtLiteral<?>) ast.getDeclaredField("__SmPLRuleName__")
                                          .getFieldDeclaration()
                                          .getAssignment()).getValue().toString();
        }

        Map<String, MetavariableConstraint> metavars = new HashMap<>();

        if (ast.getMethodsByName("__SmPLMetavars__").size() != 0) {
            CtMethod<?> mth = ast.getMethodsByName("__SmPLMetavars__").get(0);

            for (CtElement e : mth.getBody().getStatements()) {
                CtInvocation<?> invocation = (CtInvocation<?>) e;
                CtElement arg = invocation.getArguments().get(0);
                String varname = null;

                if (arg instanceof CtFieldRead<?>) {
                    varname = ((CtFieldRead<?>) arg).getVariable().getSimpleName();
                } else if (arg instanceof CtTypeAccess<?>) {
                    varname = ((CtTypeAccess<?>) arg).getAccessedType().getSimpleName();
                } else {
                    throw new IllegalArgumentException("Unable to extract metavariable name at <position>");
                }

                switch (invocation.getExecutable().getSimpleName()) {
                    case "type":
                        metavars.put(varname, new TypeConstraint());
                        break;

                    case "identifier":
                        metavars.put(varname, new IdentifierConstraint());
                        break;

                    case "constant":
                        metavars.put(varname, new ConstantConstraint());
                        break;

                    case "expression":
                        metavars.put(varname, new ExpressionConstraint());
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown metavariable type " + invocation.getExecutable().getSimpleName());
                }
            }
        }

        CtMethod<?> ruleMethod = null;

        for (CtMethod<?> mth : ast.getMethods()) {
            if (!mth.getSimpleName().equals("__SmPLMetavars__")) {
                ruleMethod = mth;
            }
        }

        if (ruleMethod == null) {
            // A completely empty rule matches nothing
            return new SmPLRuleImpl(new Not(new True()), metavars);
        }

        FormulaCompiler fc = new FormulaCompiler(new SmPLMethodCFG(ruleMethod), metavars);
        SmPLRule rule = new SmPLRuleImpl(fc.compileFormula(), metavars);
        rule.setName(ruleName);

        return rule;
    }

    /**
     * Format text for pretty-printing.
     * @param text Text to format
     * @return Formatted text
     */
    public static String prettify(String text) {
        return prettify(text, '{', '}', 4, false);
    }

    /**
     * Format text for pretty-printing.
     * @param text Text to format
     * @param open Indentation-increasing character
     * @param close Indentation-decreasing character
     * @param indentSize Indentation size
     * @param addNewlines Add newlines after indentation-altering characters?
     * @return Formatted text
     */
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

    /**
     * Rewrite an SmPL rule given in plain text into an SmPL Java DSL.
     * @param text SmPL rule in plain text
     * @return Plain text Java code in SmPL Java DSL
     */
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

        // TODO: escape character
        // TODO: strings

        // Initial context
        init.add(new RewriteRule("atat", "(?s)^@@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> { result.out.append("void __SmPLMetavars__() {\n"); }));

        init.add(new RewriteRule("atat_rulename", "(?s)^@\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> {
                    result.out.append("String __SmPLRuleName__ = \"").append(match.group(1)).append("\";\n");
                    result.out.append("void __SmPLMetavars__() {\n");
                }));

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

        metavars.add(new RewriteRule("expression", "(?s)^expression\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("expression(").append(id).append(");\n");
                    }
                }));

        // TODO: call this method header context instead?
        // Code context
        code.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));

        // TODO: separate context for the signature
        code.add(new RewriteRule("method_decl", "(?s)^[A-Za-z]+\\s+[A-Za-z]+\\s*\\([A-Za-z,\\s]*\\)\\s*\\{",
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

        code.add(new RewriteRule("delete", "(?s)^[-]",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append("__SmPLUndeclared__ method() {\n");
                    result.out.append("__SmPLDelete__();\n");
                    result.hasMethodHeader = true;
                }));

        code.add(new RewriteRule("add", "(?s)^[+]",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append("__SmPLUndeclared__ method() {\n");
                    result.out.append("__SmPLAdd__();\n");
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
        body.add(new RewriteRule("horizontal_whitespace", "(?s)^[^\\S\\r\\n]+",
                (ctx) -> {},
                (result, match) -> { result.out.append(match.group()); }));

        body.add(new RewriteRule("newline_minus", "(?s)^(\\r?\\n)+[-]",
                (ctx) -> {},
                (result, match) -> { result.out.append("\n__SmPLDelete__();\n"); }));

        body.add(new RewriteRule("newline_plus", "(?s)^(\\r?\\n)+[+]",
                (ctx) -> {},
                (result, match) -> { result.out.append("\n__SmPLAdd__();\n"); }));

        body.add(new RewriteRule("newline_noop", "(?s)^(\\r?\\n)+(?=[^+-]|$)",
                (ctx) -> {},
                (result, match) -> { result.out.append(match.group()); }));

        body.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> { ctx.push(dots); },
                (result, match) -> { result.out.append("__SmPLDots__("); }));

        body.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> {},
                (result, match) -> { result.out.append(match.group()); }));

        // Dots context
        dots.add(new RewriteRule("horizontal_whitespace", "(?s)^[^\\S\\r\\n]+",
                (ctx) -> {},
                (result, match) -> {}));

        dots.add(new RewriteRule("newline_minus", "(?s)^(\\r?\\n)+[-]",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append(");\n");
                    result.out.append("__SmPLDelete__();\n");
                }));

        dots.add(new RewriteRule("newline_plus", "(?s)^(\\r?\\n)+[+]",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append(");\n");
                    result.out.append("__SmPLAdd__();\n");
                }));

        dots.add(new RewriteRule("newline_noop", "(?s)^(\\r?\\n)+(?=[^+-]|$)",
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
