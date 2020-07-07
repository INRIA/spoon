package spoon.smpl;

import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.ConstantConstraint;
import spoon.smpl.metavars.ExpressionConstraint;
import spoon.smpl.metavars.IdentifierConstraint;
import spoon.smpl.metavars.RegexConstraint;
import spoon.smpl.metavars.TypeConstraint;

import java.util.*;

import static org.junit.Assert.*;
import static spoon.smpl.TestUtils.*;

/**
 * This is essentially an integration test of most of the SmPL stack, leaving out the parser.
 */
public class MetavarsTest {
    @Before
    public void before() {
        resetControlFlowNodeCounter();
    }

    private static Formula stmt(String code, Map<String, MetavariableConstraint> metavars) {
        return new Statement(parseStatement(code), metavars);
    }

    private static Formula retstmt(String code, Map<String, MetavariableConstraint> metavars) {
        return new Statement(parseReturnStatement(code), metavars);
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

        stmt("T x = 1;", makeMetavars("T", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", makeMetavars("z", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[(4, {z=x}, [])]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", makeMetavars("C", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", makeMetavars("z", new IdentifierConstraint())).accept(checkerAlice);
        assertEquals("[(5, {z=x}, [])]", checkerAlice.getResult().toString());
    }

    @Test
    public void testTypeConstraintsOnSingleStates() {

        // contract: "type" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", makeMetavars("T", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[(4, {T=int}, [])]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", makeMetavars("z", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", makeMetavars("C", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", makeMetavars("z", new TypeConstraint())).accept(checkerAlice);
        assertEquals("[]", checkerAlice.getResult().toString());
    }

    @Test
    public void testConstantConstraintsOnSingleStates() {

        // contract: "constant" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", makeMetavars("T", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", makeMetavars("z", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", makeMetavars("C", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[(4, {C=1}, [])]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", makeMetavars("z", new ConstantConstraint())).accept(checkerAlice);
        assertEquals("[]", checkerAlice.getResult().toString());
    }

    @Test
    public void testExpressionConstraintsOnSingleStates() {

        // contract: "expression" metavariables bind as expected when checking single states

        ModelChecker checkerAlice = new ModelChecker(modelAlice());

        stmt("T x = 1;", makeMetavars("T", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int z = 1;", makeMetavars("z", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[]", sortedEnvs(checkerAlice.getResult().toString()));

        stmt("int x = C;", makeMetavars("C", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[(4, {C=1}, [])]", sortedEnvs(checkerAlice.getResult().toString()));

        retstmt("return z;", makeMetavars("z", new ExpressionConstraint())).accept(checkerAlice);
        assertEquals("[(5, {z=x}, [])]", checkerAlice.getResult().toString());
    }

    @Test
    public void testIntersectionOfCompatibleIdenticalBindings() {

        // contract: metavariables bound to "same thing" in different nodes are joined under AND

        ModelChecker checkerAlice = new ModelChecker(modelAlice());
        Map<String, MetavariableConstraint> meta = makeMetavars("z", new IdentifierConstraint());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerAlice);
        assertEquals("[(4, {z=x}, [])]", checkerAlice.getResult().toString());
    }

    @Test
    public void testIntersectionOfIncompatibleBindings() {

        // contract: metavariables bound to different things are rejected under AND

        ModelChecker checkerBob = new ModelChecker(modelBob());
        Map<String, MetavariableConstraint> meta = makeMetavars("z", new IdentifierConstraint());

        new And(stmt("int z = 1;", meta),
                new AllNext(retstmt("return z;", meta))).accept(checkerBob);
        assertEquals("[]", checkerBob.getResult().toString());
    }

    // TODO: add tests for other formula connectors such as OR

    @Test
    public void testMultipleVars() {

        // contract: a single formula element can use many metavariables

        ModelChecker checkerAlice = new ModelChecker(modelAlice());
        Map<String, MetavariableConstraint> meta = makeMetavars("T", new TypeConstraint(),
                                                                "C", new ConstantConstraint(),
                                                                "ret", new IdentifierConstraint());

        stmt("T ret = C;", meta).accept(checkerAlice);
        assertEquals("[(4, {C=1, T=int, ret=x}, [])]", sortedEnvs(checkerAlice.getResult().toString()));
    }

    @Test
    public void testRegexConstraintOnIdentifier() {

        // contract: identifier metavariables can be further constrained by regex constraints

        CtElement e = parseExpression("foobar");

        assertEquals(e, new RegexConstraint("foo.*", new IdentifierConstraint()).apply(e));
        assertEquals(e, new RegexConstraint(".*bar", new IdentifierConstraint()).apply(e));
        assertEquals(e, new RegexConstraint(".*", new IdentifierConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("foo", new IdentifierConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("bar", new IdentifierConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("Frank Zappa", new IdentifierConstraint()).apply(e));
    }

    @Test
    public void testRegexConstraintOnType() {

        // contract: type metavariables can be further constrained by regex constraints

        CtElement e = ((CtVariable<?>) parseStatement("WebSettings.TextSize size = 10;")).getType();

        assertEquals(e, new RegexConstraint("\\w+Settings\\..*", new TypeConstraint()).apply(e));
        assertEquals(e, new RegexConstraint(".*TextSize", new TypeConstraint()).apply(e));
        assertEquals(e, new RegexConstraint(".*", new TypeConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("WebSe[ab]+tings\\..*", new TypeConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("TextSize", new TypeConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("Frank Zappa", new TypeConstraint()).apply(e));
    }

    @Test
    public void testRegexConstraintOnConstant() {

        // contract: constant metavariables can be further constrained by regex constraints

        CtElement e = ((CtVariable<?>) parseStatement("WebSettings.TextSize size = 10;")).getDefaultExpression();

        assertEquals(e, new RegexConstraint("(2|4|6|8|10)", new ConstantConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("(1|3|5|7|9)", new ConstantConstraint()).apply(e));
    }

    @Test
    public void testRegexConstraintOnExpression() {

        // contract: expression metavariables can be further constrained by regex constraints

        CtElement e = parseExpression("LARGE + 44");

        assertEquals(e, new RegexConstraint("[A-Z]+\\s+[+]\\s+\\d+", new ExpressionConstraint()).apply(e));
        assertEquals(null, new RegexConstraint("[a-z]+\\s+[+]\\s+\\d+", new ExpressionConstraint()).apply(e));
    }
}
