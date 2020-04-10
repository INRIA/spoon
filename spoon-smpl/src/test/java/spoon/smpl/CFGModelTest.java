package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import org.junit.Before;
import org.junit.Test;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.smpl.formula.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static spoon.smpl.TestUtils.*;

public class CFGModelTest {
    @Before
    public void before() {
        resetControlFlowNodeCounter();
    }

    @Test
    public void testSimple() {

        // contract: CFGModel should produce a checkable model from a given CFG

        CtMethod<?> method = parseMethod("int m() { int x = 1; return x + 1; }");
        CFGModel model = new CFGModel(methodCfg(method));

        Formula phi = new And(
                new StatementPattern(makePattern(parseStatement("int x = 1;"))),
                new ExistsNext(
                        new StatementPattern(makePattern(parseReturnStatement("return x + 1;")))));

        ModelChecker checker = new ModelChecker(model);
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(4, env()), checker.getResult());
    }

    @Test
    public void testBranch() {

        // TODO: split this test into multiple tests with clear contracts

        CtMethod<?> method = parseMethod("int m() { int x = 8; if (x > 0) { return 1; } else { return 0; } }");
        CFGModel model = new CFGModel(methodCfg(method));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);
        Formula phi;

        phi = new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class);
        phi.accept(checker);
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                      new ExistsNext(new And(new Proposition("falseBranch"),
                                             new AllNext(new StatementPattern(makePattern(parseStatement("return 0;")))))));
        phi.accept(checker);
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        assertEquals(res(), checker.getResult());

        phi = new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                        new StatementPattern(makePattern(parseStatement("return 0;"))));
        phi.accept(checker);
        assertEquals(res(8, env(), 11, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new Or(new And(new Proposition("trueBranch"),
                                           new AllNext(new StatementPattern(makePattern(parseStatement("return 1;"))))),
                                   new And(new Proposition("falseBranch"),
                                           new AllNext(new StatementPattern(makePattern(parseStatement("return 0;"))))))));
        phi.accept(checker);
        assertEquals(res(5, env()), checker.getResult());
    }

    @Test
    public void testBeginNodeBug() {

        // contract: the CFGModel should not include a state for the BEGIN node

        Model model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; } ")));
        ModelChecker checker = new ModelChecker(model);

        new AllNext(new StatementPattern(makePattern(parseStatement("int x = 1;")))).accept(checker);

        assertEquals(res(), checker.getResult());

        // Before bugfix was [(2, {})] where 2 is the ID of the BEGIN node in the CFG
    }

    @Test
    public void testExitNodeHasSelfLoop() {

        // contract: the exit node should have itself as its single successor

        CFGModel model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; } ")));

        model.getCfg().findNodesOfKind(BranchKind.EXIT).forEach((node) -> {
            // TODO: this assumes state ids correspond to node ids which isnt being tested
            assertEquals(1, model.getSuccessors(node.getId()).size());
            assertTrue(node.getId() == model.getSuccessors(node.getId()).get(0));
        });
    }

    @Test
    public void testBranchAnnotations() {

        // contract: a CFGModel should annotate branches with proposition labels

        Model model = new CFGModel(methodCfg(parseMethod("int foo(int n) {     \n" +
                                                         "    if (n > 0) {     \n" +
                                                         "        return 1;    \n" +
                                                         "    } else {         \n" +
                                                         "        return 0;    \n" +
                                                         "    }                \n" +
                                                         "}                    \n")));

        for (int state : model.getStates()) {
            for (Label label : model.getLabels(state)) {
                if (label instanceof StatementLabel) {
                    StatementLabel stmLabel = (StatementLabel) label;

                    if (stmLabel.getStatement().toString().equals("return 1")) {
                        for (int otherState : model.getStates()) {
                            if (model.getSuccessors(otherState).contains(state)) {
                                assertTrue(model.getLabels(otherState).contains(new PropositionLabel("trueBranch")));
                            }
                        }
                    } else if (stmLabel.getStatement().toString().equals("return 0")) {
                        for (int otherState : model.getStates()) {
                            if (model.getSuccessors(otherState).contains(state)) {
                                assertTrue(model.getLabels(otherState).contains(new PropositionLabel("falseBranch")));
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testToString() {

        // contract: CFGModel should provide a useful string representation

        Model model = new CFGModel(methodCfg(parseMethod("int foo(int n) {     \n" +
                                                         "    if (n > 0) {     \n" +
                                                         "        return 1;    \n" +
                                                         "    } else {         \n" +
                                                         "        return 0;    \n" +
                                                         "    }                \n" +
                                                         "}                    \n")));

        Pattern regex = Pattern.compile("labels=\\{(\\d+): \\[\\], (\\d+): \\[if \\(n > 0\\)\\], (\\d+): \\[after\\], " +
                                        "(\\d+): \\[trueBranch\\], (\\d+): \\[return 1\\], (\\d+): \\[falseBranch\\], " +
                                        "(\\d+): \\[return 0\\]}");

        Matcher matcher = regex.matcher(model.toString());
        assertTrue(matcher.find());

        String exit = matcher.group(1);
        String ifstm = matcher.group(2);
        String after = matcher.group(3);
        String truebranch = matcher.group(4);
        String retone = matcher.group(5);
        String falsebranch = matcher.group(6);
        String retzero = matcher.group(7);

        assertTrue(model.toString().contains("states=[" + exit + ", " +
                                                          ifstm + ", " +
                                                          after + ", " +
                                                          truebranch + ", " +
                                                          retone + ", " +
                                                          falsebranch + ", " +
                                                          retzero + "]"));

        assertTrue(model.toString().contains("successors={" + exit + "->" + exit + ", " +
                                                              ifstm + "->" + truebranch + ", " +
                                                              ifstm + "->" + falsebranch + ", " +
                                                              after + "->" + exit + ", " +
                                                              truebranch + "->" + retone + ", " +
                                                              retone + "->" + exit + ", " +
                                                              falsebranch + "->" + retzero + ", " +
                                                              retzero + "->" + exit + "}"));
    }
}
