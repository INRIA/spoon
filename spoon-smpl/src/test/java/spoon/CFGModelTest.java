package spoon;

import fr.inria.controlflow.ControlFlowBuilder;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import org.junit.Before;
import org.junit.Test;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.smpl.CFGModel;
import spoon.smpl.ModelChecker;
import spoon.smpl.formula.*;
import spoon.smpl.pattern.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CFGModelTest {

    public static Set<Integer> makeSet(Integer ... xs) {
        return new HashSet<Integer>(Arrays.asList(xs));
    }

    public static PatternNode makePattern(CtElement element) {
        PatternBuilder builder = new PatternBuilder();
        element.accept(builder);
        return builder.getResult();
    }

    public static CtMethod<?> parseMethod(String methodCode) {
        CtClass<?> myclass = Launcher.parseClass("class A { " + methodCode + " }");
        return (CtMethod<?>) myclass.getMethods().toArray()[0];
    }

    public static ControlFlowGraph methodCfg(CtMethod<?> method) {
        ControlFlowBuilder cfgBuilder = new ControlFlowBuilder();
        ControlFlowGraph cfg = cfgBuilder.build(method);
        cfg.simplify();

        return cfg;
    }

    public static CtElement parseStatement(String code) {
        CtClass<?> myclass = Launcher.parseClass("class A { void m() { " + code + " } }");
        return ((CtMethod<?>)myclass.getMethods().toArray()[0]).getBody().getLastStatement();
    }

    public static CtElement parseExpression(String code) {
        CtClass<?> myclass = Launcher.parseClass("class A { Object m() { return " + code + " } }");
        CtReturn<?> ctReturn = ((CtMethod<?>)myclass.getMethods().toArray()[0]).getBody().getLastStatement();
        return ctReturn.getReturnedExpression();
    }

    public static CtElement parseReturnStatement(String code) {
        CtClass<?> myclass = Launcher.parseClass("class A { Object m() { " + code + " } }");
        return ((CtMethod<?>)myclass.getMethods().toArray()[0]).getBody().getLastStatement();
    }

    @Before
    public void resetControlFlowNodeCounter() {
        try {
            Field field = ControlFlowNode.class.getDeclaredField("count");
            field.setAccessible(true);
            ControlFlowNode.count = 0;
        } catch (Exception e) {
            fail("Unable to reset ControlFlowNode id counter");
        }
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
        assertEquals(makeSet(4), checker.getResult());
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
        assertEquals(makeSet(5), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                      new ExistsNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(makeSet(5), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(makeSet(), checker.getResult());

        phi = new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                        new StatementPattern(makePattern(parseStatement("return 0;"))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(makeSet(8,11), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                                   new StatementPattern(makePattern(parseStatement("return 0;"))))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(makeSet(5), checker.getResult());
    }
}
