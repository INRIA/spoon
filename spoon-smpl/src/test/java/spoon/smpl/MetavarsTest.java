package spoon.smpl;

import fr.inria.controlflow.ControlFlowNode;
import org.junit.Before;
import org.junit.Test;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.ConstantConstraint;
import spoon.smpl.metavars.ExpressionConstraint;
import spoon.smpl.metavars.IdentifierConstraint;
import spoon.smpl.metavars.TypeConstraint;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spoon.smpl.TestUtils.*;
import static spoon.smpl.TestUtils.parseReturnStatement;

/**
 * This is essentially an integration test of most of the SmPL stack, leaving out the parser.
 */
public class MetavarsTest {
    @Before
    public void resetControlFlowNodeCounter() {
        // needed for consistent IDs in CFGModels
        try {
            Field field = ControlFlowNode.class.getDeclaredField("count");
            field.setAccessible(true);
            ControlFlowNode.count = 0;
        } catch (Exception e) {
            fail("Unable to reset ControlFlowNode id counter");
        }
    }

    private static Formula stmt(String code, Map<String, MetavariableConstraint> metavars) {
        return new StatementPattern(makePattern(parseStatement(code), new ArrayList<>(metavars.keySet())), metavars);
    }

    private static Formula retstmt(String code, Map<String, MetavariableConstraint> metavars) {
        return new StatementPattern(makePattern(parseReturnStatement(code), new ArrayList<>(metavars.keySet())), metavars);
    }

    @Test
    public void testCompatibleBindings() {

        // contract: metavariables bound to "same thing" in different nodes are joined under AND
        // TODO: add tests for other formula connectors such as OR

        Model modelA = new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return x; }")));
        ModelChecker checkerA = new ModelChecker(modelA);
        //System.out.println(((CFGModel) modelA).getCfg().toGraphVisText());

        Map<String, MetavariableConstraint> meta = metavars("z", new IdentifierConstraint());

        stmt("int z = 1;", meta).accept(checkerA);
        assertEquals("[(4, {z=x})]", checkerA.getResult().toString());

        retstmt("return z;", meta).accept(checkerA);
        assertEquals("[(5, {z=x})]", checkerA.getResult().toString());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerA);
        assertEquals("[(4, {z=x})]", checkerA.getResult().toString());
    }

    @Test
    public void testIncompatibleBindings() {

        // contract: metavariables bound to different things are rejected under AND

        Model modelA = new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return y; }")));
        ModelChecker checkerA = new ModelChecker(modelA);
        //System.out.println(((CFGModel) modelA).getCfg().toGraphVisText());

        Map<String, MetavariableConstraint> meta = metavars("z", new IdentifierConstraint());

        stmt("int z = 1;", meta).accept(checkerA);
        assertEquals("[(4, {z=x})]", checkerA.getResult().toString());

        retstmt("return z;", meta).accept(checkerA);
        assertEquals("[(5, {z=y})]", checkerA.getResult().toString());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerA);
        assertEquals("[]", checkerA.getResult().toString()); // incompatible environments -> no result
    }

    @Test
    public void testMultipleVars() {

        // contract: a single formula element can use many metavariables

        Model modelA = new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return x; }")));
        ModelChecker checkerA = new ModelChecker(modelA);
        //System.out.println(((CFGModel) modelA).getCfg().toGraphVisText());

        Map<String, MetavariableConstraint> meta = metavars("T", new TypeConstraint(),
                                                                  "C", new ConstantConstraint(),
                                                                  "ret", new IdentifierConstraint());

        stmt("T ret = C;", meta).accept(checkerA);
        assertEquals("[(4, {C=1, T=int, ret=x})]", sortedEnvs(checkerA.getResult().toString()));

        retstmt("return ret;", meta).accept(checkerA);
        assertEquals("[(5, {ret=x})]", checkerA.getResult().toString());

        new And(stmt("T ret = C;", meta),
                new AllNext(retstmt("return ret;", meta))).accept(checkerA);
        assertEquals("[(4, {C=1, T=int, ret=x})]", sortedEnvs(checkerA.getResult().toString()));
    }

    // TODO: add test for every type of constraint

    @Test
    public void testExpressionConstraint() {

        // contract: expression metavariables bind to any expression

        Model modelA = new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return x; }")));
        ModelChecker checkerA = new ModelChecker(modelA);
        //System.out.println(((CFGModel) modelA).getCfg().toGraphVisText());

        resetControlFlowNodeCounter();

        Model modelB = new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return 0; }")));
        ModelChecker checkerB = new ModelChecker(modelB);
        //System.out.println(((CFGModel) modelB).getCfg().toGraphVisText());

        // when E is an expression it matches in both models
        stmt("return E;", metavars("E", new ExpressionConstraint())).accept(checkerA);
        assertEquals("[(5, {E=x})]", checkerA.getResult().toString());

        stmt("return E;", metavars("E", new ExpressionConstraint())).accept(checkerB);
        assertEquals("[(5, {E=0})]", checkerB.getResult().toString());

        // when E is an identifier it still matches in model A
        stmt("return E;", metavars("E", new IdentifierConstraint())).accept(checkerA);
        assertEquals("[(5, {E=x})]", checkerA.getResult().toString());

        // but when E is an identifier it doesnt match in model B
        stmt("return E;", metavars("E", new IdentifierConstraint())).accept(checkerB);
        assertEquals("[]", checkerB.getResult().toString());
    }
}
