package spoon.smpl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * SmPLParser contains methods for rewriting SmPL text input to an SmPL Java DSL (domain-specific language)
 * and to compile this DSL into an SmPLRule instance.
 */
public class SmPLParser {
    /**
     * Parse an SmPL rule given in plain text.
     *
     * @param smpl SmPL rule in plain text
     * @return SmPLRule instance corresponding to input
     */
    public static SmPLRule parse(String smpl) {
        class DeletionAnchorRemover extends CtScanner {
            @Override
            protected void enter(CtElement e) {
                if (SmPLJavaDSL.isDeletionAnchor(e)) {
                    e.delete();
                }
            }
        }

        List<String> separated = separateAdditionsDeletions(rewrite(smpl));

        CtClass<?> dels = Launcher.parseClass(separated.get(0));
        CtClass<?> adds = Launcher.parseClass(separated.get(1));

        // TODO: we probably want to keep legitimate this-accesses, only remove those associated with SmPLUnspecified
        // part of hack to fix parsing of e.g "foo.x" with zero context for "foo"
        nullifyThisAccessTargets(dels);
        nullifyThisAccessTargets(adds);

        Set<Integer> delsLines = collectStatementLines(dels);
        Set<Integer> addsLines = collectStatementLines(adds);

        Set<Integer> commonLines = new HashSet<>(delsLines);
        commonLines.retainAll(addsLines);

        AnchoredOperationsMap anchoredOperations = anchorAdditions(adds, commonLines);

        Set<Integer> containedCommonLines = findContainedCommonLines(adds, commonLines);
        commonLines.removeAll(containedCommonLines);

        new DeletionAnchorRemover().scan(adds);
        return compile(dels, commonLines, anchoredOperations);
    }

    /**
     * Compile a given AST in the SmPL Java DSL, producing an SmPLRule containing a Formula.
     *
     * @param ast AST to compile
     * @return SmPLRule instance
     */
    public static SmPLRule compile(CtClass<?> ast, Set<Integer> commonLines, AnchoredOperationsMap additions) {
        String ruleName = null;

        if (ast.getDeclaredField(SmPLJavaDSL.getRuleNameFieldName()) != null) {
            ruleName = ((CtLiteral<?>) ast.getDeclaredField(SmPLJavaDSL.getRuleNameFieldName())
                                          .getFieldDeclaration()
                                          .getAssignment()).getValue().toString();
        }

        Map<String, MetavariableConstraint> metavars = new HashMap<>();

        if (ast.getMethodsByName(SmPLJavaDSL.getMetavarsMethodName()).size() != 0) {
            CtMethod<?> mth = ast.getMethodsByName(SmPLJavaDSL.getMetavarsMethodName()).get(0);

            for (CtElement e : mth.getBody().getStatements()) {
                if (e instanceof CtInvocation) {
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
                } else if (e instanceof CtLocalVariable) {
                    CtLocalVariable<?> ctLocalVar = (CtLocalVariable<?>) e;
                    metavars.put(ctLocalVar.getSimpleName(), new TypedIdentifierConstraint(ctLocalVar.getType().getSimpleName()));
                } else {
                    throw new IllegalArgumentException("unhandled metavariable element " + e.toString());
                }
            }
        }

        CtMethod<?> ruleMethod = SmPLJavaDSL.getRuleMethod(ast);

        if (ruleMethod == null) {
            // A completely empty rule matches nothing
            return new SmPLRuleImpl(new Not(new True()), metavars);
        }

        FormulaCompiler fc = new FormulaCompiler(new SmPLMethodCFG(ruleMethod), metavars, commonLines, additions);
        SmPLRule rule = new SmPLRuleImpl(fc.compileFormula(), metavars);
        rule.setName(ruleName);

        return rule;
    }

    /**
     * Rewrite an SmPL rule given in plain text into an SmPL Java DSL.
     *
     * @param text SmPL rule in plain text
     * @return Plain text Java code in SmPL Java DSL
     */
    public static String rewrite(String text) {
        class Result {
            public Result() {
                out = new StringBuilder();
                hasUnspecifiedMethodHeader = false;
                hasDotsArguments = false;
            }

            public StringBuilder out;
            public boolean hasUnspecifiedMethodHeader;
            public boolean hasDotsArguments;
        }

        class RewriteRule {
            public RewriteRule(String name, String regex, Consumer<Stack<List<RewriteRule>>> contextOp, BiFunction<Result, Matcher, Integer> outputOp) {
                this.name = name;
                this.pattern = Pattern.compile(regex);
                this.contextOp = contextOp;
                this.outputOp = outputOp;
            }

            public final String name;
            public final Pattern pattern;
            public final Consumer<Stack<List<RewriteRule>>> contextOp;
            public final BiFunction<Result, Matcher, Integer> outputOp;
        }

        if (text.length() < 1) {
            throw new RuntimeException("Empty input");
        }

        List<RewriteRule> init = new ArrayList<>();
        List<RewriteRule> metavars = new ArrayList<>();
        List<RewriteRule> code = new ArrayList<>();
        List<RewriteRule> header_modifiers = new ArrayList<>();
        List<RewriteRule> header_type = new ArrayList<>();
        List<RewriteRule> header_name = new ArrayList<>();
        List<RewriteRule> header_params = new ArrayList<>();
        List<RewriteRule> body = new ArrayList<>();
        List<RewriteRule> argumentList = new ArrayList<>();
        List<RewriteRule> statementDots = new ArrayList<>();
        List<RewriteRule> statementDotsParams = new ArrayList<>();
        List<RewriteRule> optionalMatchDots = new ArrayList<>();
        List<RewriteRule> disjunction = new ArrayList<>();

        RewriteRule eatWhitespace = new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> { return match.end(); });

        RewriteRule anycharCopy = new RewriteRule("anychar", "(?s)^.",
                (ctx) -> {},
                (result, match) -> {
                    result.out.append(match.group());
                    return match.end();
                });

        RewriteRule newlinePopContext = new RewriteRule("newline", "(?s)^\n",
                (ctx) -> { ctx.pop(); },
                (result, match) -> { return 0; });

        RewriteRule anycharPopContext = new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); },
                (result, match) -> { return 0; });

        // TODO: escape character
        // TODO: strings

        // Initial context
        init.add(eatWhitespace);

        init.add(new RewriteRule("atat", "(?s)^@@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> {
                    result.out.append("void ").append(SmPLJavaDSL.getMetavarsMethodName()).append("() {\n");
                    return match.end();
                }));

        init.add(new RewriteRule("atat_rulename", "(?s)^@\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> {
                    result.out.append("String ").append(SmPLJavaDSL.getRuleNameFieldName())
                              .append(" = \"").append(match.group(1)).append("\";\n")
                              .append("void ").append(SmPLJavaDSL.getMetavarsMethodName()).append("() {\n");
                    return match.end();
                }));

        // TODO: replace hardcoded names with calls to SmPLJavaDSL.getWhatever
        // Metavars context
        metavars.add(eatWhitespace);

        metavars.add(new RewriteRule("atat", "(?s)^@@",
                (ctx) -> { ctx.pop(); ctx.push(code); },
                (result, match) -> {
                    result.out.append("}\n");
                    return match.end();
                }));

        metavars.add(new RewriteRule("identifier", "(?s)^identifier\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("identifier(").append(id).append(");\n");
                    }
                    return match.end();
                }));

        metavars.add(new RewriteRule("type", "(?s)^type\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("type(").append(id).append(");\n");
                    }
                    return match.end();
                }));

        metavars.add(new RewriteRule("constant", "(?s)^constant\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("constant(").append(id).append(");\n");
                    }
                    return match.end();
                }));

        metavars.add(new RewriteRule("expression", "(?s)^expression\\s+([^;]+);",
                (ctx) -> {},
                (result, match) -> {
                    for (String id : match.group(1).split("\\s*,\\s*")) {
                        result.out.append("expression(").append(id).append(");\n");
                    }
                    return match.end();
                }));

        metavars.add(new RewriteRule("explicit_type", "(?s)^([A-Za-z_][A-Za-z0-9_]*)\\s+([^;]+);",
        (ctx) -> {},
        (result, match) -> {
            for (String id : match.group(2).split("\\s*,\\s*")) {
                result.out.append(match.group(1)).append(" ").append(id).append(";\n");
            }
            return match.end();
        }));

        // Code context, meaning we're done with metavars and now expect either a method header or just a bunch of statements
        code.add(new RewriteRule("method_header", "(?s)^\\s*(public\\s+|private\\s+|protected\\s+|static\\s+)*[A-Za-z_][A-Za-z0-9_-]*\\s+[A-Za-z_][A-Za-z0-9_-]*\\s*\\(",
                (ctx) -> { ctx.pop(); ctx.push(header_modifiers); },
                (result, match) -> {
                    result.hasUnspecifiedMethodHeader = false;
                    return 0;
                }));

        // any char, but requires there to be SOME non-whitespace content eventually
        code.add(new RewriteRule("anychar", "(?s)^.(?=.*[^\\s])",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.hasUnspecifiedMethodHeader = true;
                    result.out.append(SmPLJavaDSL.createUnspecifiedMethodHeaderString())
                              .append(" {\n")
                              .append("if (").append(SmPLJavaDSL.getDotsWithOptionalMatchName()).append(") {")
                              .append("\n");
                    return 0;
                }));

        // any char, consuming all remaining input. this is here to support the "empty patch": "@@@@"
        code.add(new RewriteRule("anychar", "(?s)^.*",
                (ctx) -> { },
                (result, match) -> { return match.end(); }));

        // Method header modifiers context
        header_modifiers.add(eatWhitespace);

        header_modifiers.add(new RewriteRule("modifiers", "(?s)^(public|private|protected|static)",
                (ctx) -> {},
                (result, match) -> {
                    result.out.append(match.group(1)).append(" ");
                    return match.end();
                }));

        header_modifiers.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); ctx.push(header_type); },
                (result, match) -> {
                    return 0;
                }));

        // Method header type context
        header_type.add(eatWhitespace);

        // TODO: support type parameters
        header_type.add(new RewriteRule("method_type", "(?s)^([A-Za-z_][A-Za-z0-9_]*)",
                (ctx) -> { ctx.pop(); ctx.push(header_name); },
                (result, match) -> {
                    result.out.append(match.group(1)).append(" ");
                    return match.end();
                }));

        // Method header name context
        header_name.add(eatWhitespace);

        header_name.add(new RewriteRule("method_name", "(?s)^([A-Za-z_][A-Za-z0-9_]*)",
                (ctx) -> { ctx.pop(); ctx.push(header_params); },
                (result, match) -> {
                    result.out.append(match.group(1));
                    return match.end();
                }));

        // Method header params context
        header_params.add(new RewriteRule("opening_brace", "(?s)^\\{",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append("{");
                    return match.end();
                }));

        header_params.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> {},
                (result, match) -> {
            // TODO: give each dots parameter a fresh identifier?
                    result.out.append(SmPLJavaDSL.createDotsParameterString());
                    return match.end();
                }));

        header_params.add(anycharCopy);

        // Method body context
        body.add(new RewriteRule("open_paren", "(?s)^\\(",
                 (ctx) -> { ctx.push(argumentList); },
                 (result, match) -> {
                    result.out.append("(");
                    return match.end();
                 }));

        body.add(new RewriteRule("newline", "(?s)^\n",
            (ctx) -> {
                ctx.push(statementDots);
                ctx.push(optionalMatchDots);
                ctx.push(disjunction);
            },

            (result, match) -> {
                result.out.append("\n");
                return match.end();
            }));

        body.add(anycharCopy);

        // Context for statement dots
        statementDots.add(new RewriteRule("dots", "(?s)^[^\\S\n]*\\.\\.\\.",
                (ctx) -> { ctx.pop(); ctx.push(statementDotsParams); },
                (result, match) -> {
                    result.out.append(SmPLJavaDSL.getDotsStatementElementName()).append("(");
                    return match.end();
                }));

        statementDots.add(anycharPopContext);

        // Context for statement dots parameters (constraints)
        statementDotsParams.add(eatWhitespace);

        statementDotsParams.add(new RewriteRule("when_neq", "(?s)^when\\s*!=\\s*([a-z]+)",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append(SmPLJavaDSL.getDotsWhenNotEqualName()).append("(")
                              .append(match.group(1)).append(")");
                    return match.end();
                }));

        statementDotsParams.add(new RewriteRule("when_exists", "(?s)^when\\s+exists",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append(SmPLJavaDSL.getDotsWhenExistsName()).append("()");
                    return match.end();
                }));

        statementDotsParams.add(new RewriteRule("when_any", "(?s)^when\\s+any",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append(SmPLJavaDSL.getDotsWhenAnyName()).append("()");
                    return match.end();
                }));

        statementDotsParams.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append(");\n");
                    return 0;
                }));

        // Context for dots with optional match <... P ...> microsyntax
        optionalMatchDots.add(new RewriteRule("optdots_begin", "(?s)^<\\.\\.\\.", // TODO: could add something like result.required += "optdots_end" for error detection
                (ctx) -> { },
                (result, match) -> {
                    result.out.append("if (").append(SmPLJavaDSL.getDotsWithOptionalMatchName()).append(") {");
                    return match.end();
                }));

        optionalMatchDots.add(new RewriteRule("optdots_end", "(?s)^\\.\\.\\.>",
                (ctx) -> { },
                (result, match) -> {
                    result.out.append("}");
                    return match.end();
                }));

        optionalMatchDots.add(anycharPopContext);

        // Context for pattern disjunction microsyntax
        disjunction.add(new RewriteRule("disjunction_begin", "(?s)^\\(",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append("if (").append(SmPLJavaDSL.getBeginDisjunctionName()).append(") {");
                    return match.end();
                }));

        disjunction.add(new RewriteRule("disjunction_continue", "(?s)^\\|",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append("} else if (").append(SmPLJavaDSL.getContinueDisjunctionName()).append(") {");
                    return match.end();
                }));

        disjunction.add(new RewriteRule("disjunction_end", "(?s)^\\)",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append("}");
                    return match.end();
                }));

        disjunction.add(anycharPopContext);

        // Context for method call argument lists
        argumentList.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> { },
                (result, match) -> {
                    result.hasDotsArguments = true;
                    result.out.append(SmPLJavaDSL.getDotsParameterOrArgumentElementName());
                    return match.end();
                }));

        argumentList.add(new RewriteRule("open_paren", "(?s)^\\(",
                (ctx) -> { ctx.push(argumentList); },
                (result, match) -> {
                    result.out.append("(");
                    return match.end();
                }));

        argumentList.add(new RewriteRule("close_paren", "(?s)^\\)",
                (ctx) -> { ctx.pop(); },
                (result, match) -> {
                    result.out.append(")");
                    return match.end();
                }));

        argumentList.add(anycharCopy);

        Result result = new Result();

        // TODO: standardize class name in SmPLJavaDSL?
        result.out.append("class RewrittenSmPLRule {\n");

        if (result.hasDotsArguments) {
            result.out.append("Object ")
                      .append(SmPLJavaDSL.getDotsParameterOrArgumentElementName())
                      .append(" = null;\n");
        }

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
                    pos += rule.outputOp.apply(result, matcher);

                    foundSomething = true;
                    break;
                }
            }

            if (!foundSomething) {
                throw new RuntimeException("Parse error at offset " + Integer.toString(pos) + ", expected one of " + expected.toString());
            }
        }

        if (result.hasUnspecifiedMethodHeader) {
            // close implicit dots-with-optional-match
            result.out.append("}\n");

            // close method
            result.out.append("}\n");
        }

        // hack to fix parsing of e.g "foo.x" with zero context for "foo"
        List<String> addedMembers = new ArrayList<>();
        Matcher m = Pattern.compile("(?s)([$A-Za-z_][$A-Za-z0-9_]*)(\\s*\\.\\s*([$A-Za-z_][$A-Za-z0-9_]*))+").matcher(result.out.toString());

        while (m.find()) {
            String memberName = m.group(1);

            if (addedMembers.contains(memberName)) {
                continue;
            }

            result.out.append(SmPLJavaDSL.getUnspecifiedElementOrTypeName()).append(" ").append(memberName).append(";\n");
            addedMembers.add(memberName);
        }

        result.out.append("}\n");
        return removeEmptyLines(result.out.toString()) + "\n";
    }

    /**
     * Remove empty lines from a String.
     *
     * @param s String to process
     * @return String with empty lines removed
     */
    private static String removeEmptyLines(String s) {
        return String.join("\n", Arrays.stream(s.split("\n")).filter(ss -> !ss.isEmpty()).collect(Collectors.toList()));
    }

    /**
     * Separate an SmPL patch given in plain text into two versions where one removes all
     * added lines retaining only deletions and context lines, and the other replaces all
     * deleted lines with a dummy placeholder for anchoring.
     *
     * @param input SmPL patch in plain text to separate
     * @return List of two Strings containing the two separated versions
     */
    private static List<String> separateAdditionsDeletions(String input) {
        StringBuilder dels = new StringBuilder();
        StringBuilder adds = new StringBuilder();

        for (String str : input.split("\n")) {
            if (str.length() > 0) {
                if (str.charAt(0) == '-') {
                    dels.append(' ').append(str.substring(1)).append("\n");
                    if (str.contains(SmPLJavaDSL.getDotsStatementElementName() + "();")) {
                        adds.append("\n");
                    } else {
                        adds.append(SmPLJavaDSL.getDeletionAnchorName()).append("();\n");
                    }
                } else if (str.charAt(0) == '+') {
                    dels.append("\n");
                    adds.append(' ').append(str.substring(1)).append("\n");
                } else {
                    dels.append(str).append("\n");
                    adds.append(str).append("\n");
                }
            } else {
                dels.append(str).append("\n");
                adds.append(str).append("\n");
            }
        }

        return Arrays.asList(dels.toString(), adds.toString());
    }

    /**
     * Find appropriate anchors for all addition operations.
     *
     * @param e SmPL rule class in the SmPL Java DSL
     * @param commonLines Set of context lines common to both the deletions and the additions ASTs
     * @return Map of anchors to lists of operations
     */
    private static AnchoredOperationsMap anchorAdditions(CtClass<?> e, Set<Integer> commonLines) {
        CtMethod<?> ruleMethod = SmPLJavaDSL.getRuleMethod(e);
        return anchorAdditions(ruleMethod.getBody(), commonLines, AnchoredOperationsMap.methodBodyAnchor, "methodBody");
    }

    /**
     * Recursive helper function for anchorAdditions.
     *
     * @param e Element to scan
     * @param commonLines Set of context lines common to both the deletions and the additions ASTs
     * @param blockAnchor Line number of statement seen as current block-insert anchor.
     * @param context Anchoring context, one of null, "methodHeader", "trueBranch" or "falseBranch"
     * @return Map of anchors to lists of operations
     */
    private static AnchoredOperationsMap anchorAdditions(CtElement e, Set<Integer> commonLines, int blockAnchor, String context) {
        AnchoredOperationsMap result = new AnchoredOperationsMap();

        // Temporary storage for operations until an anchor is found
        List<Pair<InsertIntoBlockOperation.Anchor, CtElement>> unanchored = new ArrayList<>();

        // Less temporary storage for operations encountered without an anchor that cannot be anchored
        // to the next encountered anchorable statement, to be dealt with later
        List<Pair<InsertIntoBlockOperation.Anchor, CtElement>> unanchoredCommitted = new ArrayList<>();

        int elementAnchor = 0;
        boolean isAfterDots = false;

        if (e instanceof CtBlock<?>) {
            for (CtStatement stmt : ((CtBlock<?>) e).getStatements()) {
                int stmtLine = stmt.getPosition().getLine();

                if (SmPLJavaDSL.isDeletionAnchor(stmt) || commonLines.contains(stmtLine)) {
                    // This is a deletion or a context statement

                    if (SmPLJavaDSL.isStatementLevelDots(stmt)) {
                        // TODO: this is pretty awful, find a cleaner way
                        // Arriving at dots carrying an unanchored statement that was itself preceded by dots
                        //   yields an impossible situation
                        if (unanchored.size() > 0) {
                            for (Pair<InsertIntoBlockOperation.Anchor, CtElement> pair : unanchored) {
                                if (pair.getLeft().equals(InsertIntoBlockOperation.Anchor.BOTTOM)) {
                                    throw new IllegalArgumentException("unanchorable statement");
                                }
                            }
                        }

                        unanchoredCommitted.addAll(unanchored);
                        isAfterDots = true;
                        elementAnchor = 0;
                    } else {
                        isAfterDots = false;
                        // TODO: if we used line+offset maybe we could support multiple anchorable statements per line in a patch
                        elementAnchor = stmtLine;

                        // The InsertIntoBlockOperation.Anchor is irrelevant here
                        for (Pair<InsertIntoBlockOperation.Anchor, CtElement> element : unanchored) {
                            result.addKeyIfNotExists(elementAnchor);
                            result.get(elementAnchor).add(new PrependOperation(element.getRight()));
                        }
                    }

                    unanchored.clear();

                    // Process branches of if-then-else statements
                    if (stmt instanceof CtIf) {
                        CtIf ctIf = (CtIf) stmt;
                        result.join(anchorAdditions(ctIf.getThenStatement(), commonLines, stmtLine, "trueBranch"));

                        if (ctIf.getElseStatement() != null) {
                            result.join(anchorAdditions(((CtIf) stmt).getElseStatement(), commonLines, stmtLine, "falseBranch"));
                        }
                    }
                } else {
                    // This is an addition

                    if (elementAnchor != 0) {
                        result.addKeyIfNotExists(elementAnchor);
                        result.get(elementAnchor).add(new AppendOperation(stmt));
                    } else {
                        unanchored.add(new ImmutablePair<>(isAfterDots ? InsertIntoBlockOperation.Anchor.BOTTOM
                                                                       : InsertIntoBlockOperation.Anchor.TOP, stmt));
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("cannot handle " + e.getClass().toString());
        }

        unanchored.addAll(unanchoredCommitted);

        // Process unanchored elements
        if (unanchored.size() > 0) {
            result.addKeyIfNotExists(blockAnchor);

            for (Pair<InsertIntoBlockOperation.Anchor, CtElement> element : unanchored) {
                switch (context) {
                    case "methodBody":
                        result.get(blockAnchor)
                              .add(new InsertIntoBlockOperation(InsertIntoBlockOperation.BlockType.METHODBODY,
                                                                element.getLeft(),
                                                                (CtStatement) element.getRight()));
                        break;
                    case "trueBranch":
                        result.get(blockAnchor)
                              .add(new InsertIntoBlockOperation(InsertIntoBlockOperation.BlockType.TRUEBRANCH,
                                                                element.getLeft(),
                                                                (CtStatement) element.getRight()));
                        break;
                    case "falseBranch":
                        result.get(blockAnchor)
                              .add(new InsertIntoBlockOperation(InsertIntoBlockOperation.BlockType.FALSEBRANCH,
                                                                element.getLeft(),
                                                                (CtStatement) element.getRight()));
                        break;
                    default:
                        throw new IllegalStateException("unknown context " + context);
                }
            }
        }

        return result;
    }

    /**
     * Scan the rule method of a given class in the SmPL Java DSL and collect the line
     * numbers associated with statements in the method body.
     *
     * @param ctClass Class in SmPL Java DSL
     * @return Set of line numbers at which statements occur in the rule method
     */
    private static Set<Integer> collectStatementLines(CtClass<?> ctClass) {
        class LineCollectingScanner extends CtScanner {
            public Set<Integer> result = new HashSet<>();

            @Override
            protected void enter(CtElement e) {
                if (!SmPLJavaDSL.isDeletionAnchor(e) && e instanceof CtStatement && !(e instanceof CtBlock)) {
                    result.add(e.getPosition().getLine());
                }
            }
        }

        LineCollectingScanner lines = new LineCollectingScanner();
        lines.scan(SmPLJavaDSL.getRuleMethod(ctClass).getBody().getStatements());
        return lines.result;
    }

    /**
     * Scan the rule method of a given class in the SmPL Java DSL and find the set of
     * statement-associated line numbers that are included in a given set of 'common' line
     * numbers, but for which the parent element is a block belonging to a statement that
     * does not occur on a line belonging to the set of 'common' line numbers.
     *
     * i.e the set of context lines enclosed in non-context lines.
     *
     * @param ctClass Class in SmPL Java DSL
     * @param commonLines Set of 'common' line numbers
     * @return Set of line numbers enclosed by statements that are not associated with common lines
     */
    private static Set<Integer> findContainedCommonLines(CtClass<?> ctClass, Set<Integer> commonLines) {
        class ContainedCommonLineScanner extends CtScanner {
            public ContainedCommonLineScanner(int rootParent, Set<Integer> commonLines) {
                this.rootParent = rootParent;
                this.commonLines = commonLines;
            }

            private int rootParent;
            private Set<Integer> commonLines;
            public Set<Integer> result = new HashSet<>();

            @Override
            protected void enter(CtElement e) {
                if (e instanceof CtStatement && !(e instanceof CtBlock)) {
                    int elementPos = e.getPosition().getLine();

                    if (!commonLines.contains(elementPos)) {
                        return;
                    }

                    int parentStmtPos = e.getParent().getParent().getPosition().getLine();

                    if (parentStmtPos != rootParent && !commonLines.contains(parentStmtPos)) {
                        result.add(elementPos);
                    }
                }
            }
        }

        ContainedCommonLineScanner contained = new ContainedCommonLineScanner(SmPLJavaDSL.getRuleMethod(ctClass).getPosition().getLine(), commonLines);
        contained.scan(SmPLJavaDSL.getRuleMethod(ctClass).getBody().getStatements());
        return contained.result;
    }

    /**
     * Given a CtElement AST, Replace with null all targets of CtTargetedExpressions that are
     * CtThisAccesses
     * @param e AST to operate on
     */
    private static void nullifyThisAccessTargets(CtElement e) {
        // part of hack to fix parsing of e.g "foo.x" with zero context for "foo"
        CtScanner scanner = new CtScanner() {
            @Override
            protected void enter(CtElement e) {
                if (e instanceof CtTargetedExpression) {
                    CtTargetedExpression<?,?> ctTargeted = (CtTargetedExpression<?,?>) e;

                    if (ctTargeted.getTarget() instanceof CtThisAccess) {
                        ctTargeted.setTarget(null);
                    }
                }
            }
        };

        scanner.scan(e);
    }
}
