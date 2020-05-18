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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        List<SmPLRule> ruleAlternatives = new ArrayList<>();
        List<String> inputAlternatives = separateDisjunctions(rewrite(smpl));

        for (String alternative : inputAlternatives) {
            List<String> separated = separateAdditionsDeletions(alternative);

            CtClass<?> dels = Launcher.parseClass(separated.get(0));
            CtClass<?> adds = Launcher.parseClass(separated.get(1));

            // TODO: we probably want to keep legitimate this-accesses, only remove those associated with SmPLGeneralIdentifier
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
            ruleAlternatives.add(compile(dels, commonLines, anchoredOperations));
        }

        if (ruleAlternatives.size() == 1) {
            return ruleAlternatives.get(0);
        } else {
            List<Formula> formulaAlternatives = new ArrayList<>();
            ruleAlternatives.forEach((rule) -> formulaAlternatives.add(rule.getFormula()));
            return new SmPLRuleImpl(FormulaCompiler.joinAlternatives(formulaAlternatives), ruleAlternatives.get(0).getMetavariableConstraints());
        }
    }

    /**
     * Compile a given AST in the SmPL Java DSL.
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
                (result, match) -> { result.out.append("void ").append(SmPLJavaDSL.getMetavarsMethodName()).append("() {\n"); }));

        init.add(new RewriteRule("atat_rulename", "(?s)^@\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*@",
                (ctx) -> { ctx.pop(); ctx.push(metavars); },
                (result, match) -> {
                    result.out.append("String ").append(SmPLJavaDSL.getRuleNameFieldName())
                            .append(" = \"").append(match.group(1)).append("\";\n");
                    result.out.append("void ").append(SmPLJavaDSL.getMetavarsMethodName()).append("() {\n");
                }));

        // TODO: replace hardcoded names with calls to SmPLJavaDSL.getWhatever
        // Metavars context
        metavars.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));

        metavars.add(new RewriteRule("atat", "(?s)^@@([^\\S\n]*\n)?",
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

        metavars.add(new RewriteRule("explicit_type", "(?s)^([A-Za-z_][A-Za-z0-9_]*)\\s+([^;]+);",
        (ctx) -> {},
        (result, match) -> {
            for (String id : match.group(2).split("\\s*,\\s*")) {
                result.out.append(match.group(1)).append(" ").append(id).append(";\n");
            }
        }));

        // TODO: call this method header context instead?
        // Code context
        /*code.add(new RewriteRule("whitespace", "(?s)^\\s+",
                (ctx) -> {},
                (result, match) -> {}));*/

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
                    result.out.append(SmPLJavaDSL.createUnspecifiedMethodHeaderString()).append(" {\n");
                    result.out.append(SmPLJavaDSL.getDotsElementName()).append("(");
                    result.hasMethodHeader = true;
                }));

        code.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); ctx.push(body); },
                (result, match) -> {
                    result.out.append(SmPLJavaDSL.createUnspecifiedMethodHeaderString()).append(" {\n");
                    result.out.append(match.group());
                    result.hasMethodHeader = true;
                }));

        // Method body context
        body.add(new RewriteRule("dots", "(?s)^\\.\\.\\.",
                (ctx) -> { ctx.push(dots); },
                (result, match) -> { result.out.append(SmPLJavaDSL.getDotsElementName()).append("("); }));

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

                    result.out.append(SmPLJavaDSL.getDotsWhenNotEqualName()).append("(")
                            .append(match.group(1)).append(")");
                }));

        dots.add(new RewriteRule("when_exists", "(?s)^when\\s+exists",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append(SmPLJavaDSL.getDotsWhenExistsName()).append("()");
                }));

        dots.add(new RewriteRule("when_any", "(?s)^when\\s+any",
                (ctx) -> {},
                (result, match) -> {
                    if (result.out.charAt(result.out.length() - 1) == ')') {
                        result.out.append(",");
                    }

                    result.out.append(SmPLJavaDSL.getDotsWhenAnyName()).append("()");
                }));

        dots.add(new RewriteRule("anychar", "(?s)^.",
                (ctx) -> { ctx.pop(); },
                (result, match) -> { result.out.append(");\n").append(match.group()); }));

        Result result = new Result();

        // TODO: standardize class name in SmPLJavaDSL?
        result.out.append("class RewrittenSmPLRule {\n");

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
        return result.out.toString();
    }

    /**
     * Given a String containing SmPL-like disjunction syntax, produce every possible combination eliminating all
     * disjunctions.
     *
     * SmPL-like disjunction syntax:
     * 1) The character '(' appearing in position 0 on a line denotes the start of a disjunction.
     * 2) The character '|' appearing in position 0 on a line moves to the next clause of the enclosing disjunction.
     * 3) The character ')' appearing in position 0 on a line denotes the end of a disjunction.
     *
     * @param input Input String
     * @return List of Strings containing every possible combination obtainable by eliminating disjunctions
     */
    public static List<String> separateDisjunctions(String input) {
        class Disjunction {
            public Disjunction(int id) {
                this.id = id;
            }

            public int id;
            public int numClauses;
            public int currentClause;
            public int lineStart;
            public int lineEnd;

            //@Override
            //public String toString() { return "Disjunction(" + id + "," + numClauses + "," + lineStart + "-" + lineEnd + ")"; }
        }

        List<String> results = new ArrayList<>();

        // find and enumerate disjunctions and count their clauses
        List<Disjunction> disjunctions = new ArrayList<>();
        Stack<Integer> currentDisjunctionId = new Stack<>();
        int disjunctionId = 0;
        int lineNo = 0;

        for (String str : input.split("\n")) {
            if (str.length() > 0) {
                if (str.charAt(0) == '(') {
                    currentDisjunctionId.push(disjunctionId);
                    disjunctions.add(new Disjunction(disjunctionId++));
                    disjunctions.get(currentDisjunctionId.peek()).lineStart = lineNo;
                    disjunctions.get(currentDisjunctionId.peek()).numClauses += 1;

                } else if (str.charAt(0) == '|') {
                    disjunctions.get(currentDisjunctionId.peek()).numClauses += 1;
                } else if (str.charAt(0) == ')') {
                    disjunctions.get(currentDisjunctionId.peek()).lineEnd = lineNo;
                    currentDisjunctionId.pop();
                }
            }

            ++lineNo;
        }

        // create combinations generator
        CombinationsGenerator<Integer> combo = new CombinationsGenerator<>();

        for (Disjunction disj : disjunctions) {
            combo.addWheel(IntStream.range(0, disj.numClauses).boxed().collect(Collectors.toList()));
        }

        // produce every combination
        while (combo.next()) {
            List<Integer> currentCombo = combo.current();
            StringBuilder sb = new StringBuilder();

            currentDisjunctionId = new Stack<>();
            disjunctionId = 0;
            lineNo = 0;

            for (String str : input.split("\n")) {
                // keep track of which disjunction and which clause we are in
                if (str.length() > 0) {
                    if (str.charAt(0) == '(') {
                        currentDisjunctionId.push(disjunctionId++);
                        disjunctions.get(currentDisjunctionId.peek()).currentClause = 0;
                        sb.append("\n");
                    } else if (str.charAt(0) == '|') {
                        disjunctions.get(currentDisjunctionId.peek()).currentClause += 1;
                        sb.append("\n");
                    } else if (str.charAt(0) == ')') {
                        currentDisjunctionId.pop();
                        sb.append("\n");
                    } else {
                        // handle actual content
                        if (currentDisjunctionId.empty() || disjunctions.get(currentDisjunctionId.peek()).currentClause == currentCombo.get(currentDisjunctionId.peek())) {
                            // this content either appears outside any disjunction or in a clause included by the current combination
                            sb.append(str).append("\n");
                        } else {
                            // current combination does not include this clause
                            sb.append("\n");
                        }
                    }
                } else {
                    sb.append("\n");
                }

                ++lineNo;
            }

            results.add(sb.toString());
        }

        return results;
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
                    if (str.contains(SmPLJavaDSL.getDotsElementName() + "();")) {
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
        return anchorAdditions(ruleMethod.getBody(), commonLines, 0, null);
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
                    if (!SmPLJavaDSL.isStatementLevelDots(stmt)) {
                        isAfterDots = false;
                        elementAnchor = stmtLine;

                        // The InsertIntoBlockOperation.Anchor is irrelevant here
                        for (Pair<InsertIntoBlockOperation.Anchor, CtElement> element : unanchored) {
                            result.addKeyIfNotExists(elementAnchor);
                            result.get(elementAnchor).add(new PrependOperation(element.getRight()));
                        }
                    } else {
                        unanchoredCommitted.addAll(unanchored);
                        isAfterDots = true;
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
                    case "methodHeader":
                        result.get(blockAnchor)
                              .add(new InsertIntoBlockOperation(InsertIntoBlockOperation.BlockType.METHODBODY,
                                                                InsertIntoBlockOperation.Anchor.TOP,
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
