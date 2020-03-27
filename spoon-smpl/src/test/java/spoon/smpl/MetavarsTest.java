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

    private static Model modelAlice() {
        // alice declares x and returns x
        return new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return x; }")));
    }

    private static Model modelBob() {
        // bob declares x and returns y
        return new CFGModel(methodCfg(parseMethod("int m() { int x = 1; return y; }")));
    }

    @Test
    public void testIdentifierConstraintsOnSingleStates() {

        // contract: "identifier" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", metavars("T", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", metavars("z", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[(4, {z=x})]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", metavars("C", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", metavars("z", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[(5, {z=x})]", checkerAlice.getResult().toString());
    }

    @Test
    public void testTypeConstraintsOnSingleStates() {

        // contract: "type" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", metavars("T", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[(4, {T=int})]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", metavars("z", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", metavars("C", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", metavars("z", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", checkerAlice.getResult().toString());
    }

    @Test
    public void testConstantConstraintsOnSingleStates() {

        // contract: "constant" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", metavars("T", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", metavars("z", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", metavars("C", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[(4, {C=1})]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", metavars("z", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", checkerAlice.getResult().toString());
    }

    @Test
    public void testExpressionConstraintsOnSingleStates() {

        // contract: "expression" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", metavars("T", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", metavars("z", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", metavars("C", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[(4, {C=1})]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", metavars("z", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[(5, {z=x})]", checkerAlice.getResult().toString());
    }

    @Test
    public void testIntersectionOfCompatibleIdenticalBindings() {

        // contract: metavariables bound to "same thing" in different nodes are joined under AND

        ModelChecker checkerAlice = new ModelChecker(modelAlice());
        Map<String, MetavariableConstraint> meta = metavars("z", new IdentifierConstraint());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerAlice);
        assertEquals("[(4, {z=x})]", checkerAlice.getResult().toString());
    }

    @Test
    public void testIntersectionOfIncompatibleBindings() {

        // contract: metavariables bound to different things are rejected under AND

        ModelChecker checkerBob = new ModelChecker(modelBob());
        Map<String, MetavariableConstraint> meta = metavars("z", new IdentifierConstraint());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerBob);
        assertEquals("[]", checkerBob.getResult().toString());
    }

    // TODO: add tests for other formula connectors such as OR

    @Test
    public void testMultipleVars() {

        // contract: a single formula element can use many metavariables

        ModelChecker checkerAlice = new ModelChecker(modelAlice());
        Map<String, MetavariableConstraint> meta = metavars("T", new TypeConstraint(),
                                                            "C", new ConstantConstraint(),
                                                            "ret", new IdentifierConstraint());

        stmt("T ret = C;", meta).accept(checkerAlice);
        assertEquals("[(4, {C=1, T=int, ret=x})]", sortedEnvs(checkerAlice.getResult().toString()));
    }
}
