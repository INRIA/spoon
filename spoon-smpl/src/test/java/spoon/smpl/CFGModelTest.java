package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import org.junit.Before;
import org.junit.Test;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.smpl.formula.*;

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
        CtMethod<?> method = parseMethod("int m() { int x = 8; if (x > 0) { return 1; } else { return 0; } }");
        CFGModel model = new CFGModel(methodCfg(method));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);
        Formula phi;

        phi = new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class);
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                      new ExistsNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(), checker.getResult());

        phi = new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                        new StatementPattern(makePattern(parseStatement("return 0;"))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(8, env(), 11, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                                   new StatementPattern(makePattern(parseStatement("return 0;"))))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
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

        // contract: a CFGModel should annotate the first statement in a branch

        Model model = new CFGModel(methodCfg(parseMethod("int foo(int n) {     \n" +
                                                         "    if (n > 0) {     \n" +
                                                         "        return 1;    \n" +
                                                         "    } else {         \n" +
                                                         "        return 0;    \n" +
                                                         "    }                \n" +
                                                         "}                    \n")));

        assertTrue(model.getLabels(7).contains(new PropositionLabel("trueBranch")));
        assertTrue(model.getLabels(10).contains(new PropositionLabel("falseBranch")));
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

        assertTrue(model.toString().contains("states=[1, 4, 7, 10]"));
        assertTrue(model.toString().contains("successors={1->1, 4->7, 4->10, 7->1, 10->1}"));
    }
}
