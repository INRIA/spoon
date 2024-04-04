package fr.inria.controlflow;

import java.util.*;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtMethod;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExceptionFlowTests {

    ControlFlowPathHelper pathHelper = new ControlFlowPathHelper();

    @Test
    public void testBasicSingle() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement parented by a try
        //           block having a path to the corresponding catch block.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "      b();\n" +
                                                 "      c();\n" +
                                                 "    } catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "    x();\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode x = pathHelper.findNodeByString(cfg, "x()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode c = pathHelper.findNodeByString(cfg, "c()");
        ControlFlowNode bang = pathHelper.findNodeByString(cfg, "bang()");

        assertFalse(pathHelper.canReachNode(x, bang));

        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(x));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(b));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(c));

        assertTrue(pathHelper.canReachNode(a, bang));
        assertTrue(pathHelper.canReachNode(a, b));
        assertTrue(pathHelper.canReachNode(b, bang));
        assertTrue(pathHelper.canReachNode(b, c));
        assertTrue(pathHelper.canReachNode(c, bang));
    }

    @Test
    public void testBasicDouble() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement parented by a try
        //           block having a path to the corresponding catch block.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    } catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "    x();\n" +
                                                 "    try {\n" +
                                                 "      b();\n" +
                                                 "    } catch (Exception e) {\n" +
                                                 "      boom();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode x = pathHelper.findNodeByString(cfg, "x()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode bang = pathHelper.findNodeByString(cfg, "bang()");
        ControlFlowNode boom = pathHelper.findNodeByString(cfg, "boom()");

        assertFalse(pathHelper.canReachNode(x, bang));
        assertTrue(pathHelper.canReachNode(x, boom));

        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(x));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(b));

        assertTrue(pathHelper.canReachNode(a, bang));

        assertTrue(pathHelper.canReachNode(b, boom));
        assertFalse(pathHelper.canReachNode(b, bang));
    }

    @Test
    public void testBasicNested() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement parented by a try
        //           block having a path to the corresponding catch block.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "      try {\n" +
                                                 "        b();\n" +
                                                 "      } catch (Exception e2) {\n" +
                                                 "        boom();\n" +
                                                 "      }\n" +
                                                 "      c();\n" +
                                                 "    } catch (Exception e1) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode c = pathHelper.findNodeByString(cfg, "c()");
        ControlFlowNode boom = pathHelper.findNodeByString(cfg, "boom()");
        ControlFlowNode bang = pathHelper.findNodeByString(cfg, "bang()");

        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(b));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(c));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(boom));

        assertTrue(pathHelper.canReachNode(a, boom));
        assertTrue(pathHelper.canReachNode(a, bang));
        assertTrue(pathHelper.canReachNode(b, boom));
        assertTrue(pathHelper.canReachNode(b, bang));
        assertFalse(pathHelper.canReachNode(c, boom));
        assertTrue(pathHelper.canReachNode(c, bang));
    }

    @Test
    public void testMultipleCatchers() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement 1) from which control
        //           flow is guaranteed to enter a try block equipped with multiple catchers, or 2) parented
        //           by a try block equipped with multiple catchers, to have a path to every catcher.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    catch (IOException e) {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "    catch (RuntimeException e) {\n" +
                                                 "      c();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode top = pathHelper.findNodeByString(cfg, "top()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode c = pathHelper.findNodeByString(cfg, "c()");

        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(top));
        assertTrue(pathHelper.canReachExitWithoutEnteringCatchBlock(a));

        assertTrue(pathHelper.canReachNode(top, a));
        assertTrue(pathHelper.canReachNode(top, b));
        assertTrue(pathHelper.canReachNode(top, c));

        assertTrue(pathHelper.canReachNode(a, b));
        assertTrue(pathHelper.canReachNode(a, c));

        assertFalse(pathHelper.canReachNode(b, c));
        assertFalse(pathHelper.canReachNode(c, b));
    }

    @Test
    public void testThrowStatement() {
        // contract: NaiveExceptionControlFlowStrategy should result in throw statements having paths to every
        //           catcher while having no other paths, meaning unreachable post-throw statements should never
        //           make it into the graph.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      throw new RuntimeException();\n" +
                                                 "      unreachable();\n" +
                                                 "    }\n" +
                                                 "    catch (RuntimeException e) {\n" +
                                                 "      boom();\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        assertNull(pathHelper.findNodeByString(cfg, "unreachable()"));

        ControlFlowNode throwstmt = pathHelper.findNodeByString(cfg, "throw new RuntimeException()");
        ControlFlowNode boom = pathHelper.findNodeByString(cfg, "boom()");
        ControlFlowNode bang = pathHelper.findNodeByString(cfg, "bang()");

        assertTrue(pathHelper.canReachNode(throwstmt, boom));
        assertTrue(pathHelper.canReachNode(throwstmt, bang));

        assertFalse(pathHelper.canReachExitWithoutEnteringCatchBlock(throwstmt));
    }

    @Test
    public void testAddPathsForEmptyTryBlocksDisabled() {
        // contract: NaiveExceptionControlFlowStrategy should by default not add paths from an empty try block to
        //           any of its catchers, preventing unreachable statements in catch blocks from appearing in
        //           the CFG.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        assertNull(pathHelper.findNodeByString(cfg, "bang()"));
    }

    @Test
    public void testAddPathsForEmptyTryBlocksEnabled() {
        // contract: NaiveExceptionControlFlowStrategy should, when configured with the option to do, add paths from
        //           an empty try block to each of its catchers, causing unreachable statements in catch blocks to
        //           be included in the CFG.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      bang();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        EnumSet<NaiveExceptionControlFlowStrategy.Options> options = EnumSet.of(NaiveExceptionControlFlowStrategy.Options.AddPathsForEmptyTryBlocks);
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy(options));
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        assertNotNull(pathHelper.findNodeByString(cfg, "bang()"));
    }

    @Test
    public void testFinalizerReturnStatementInTryBlockRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
        // contract: NaiveExceptionControlFlowStrategy should reject a try-catch construct if it is equipped with a
        //           finalizer and return statements are used anywhere in the construct

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      return;\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      c();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        });
    } 

    @Test
    public void testFinalizerReturnStatementInCatchBlockRejected() {
        assertThrows(IllegalArgumentException.class, () -> {
        // contract: NaiveExceptionControlFlowStrategy should reject a try-catch construct if it is equipped with a
        //           finalizer and return statements are used anywhere in the construct

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      if (random > 0.5f) {\n" +
                                                 "        return;\n" +
                                                 "      }\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      c();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        });
    } 

    @Test
    public void testFinalizerReturnStatementInFinalizerBlockRejected() {
        // contract: NaiveExceptionControlFlowStrategy should reject a try-catch construct if it is equipped with a
        //           finalizer and return statements are used anywhere in the construct

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      try {\n" +
                                                 "        c();" +
                                                 "      }\n" +
                                                 "      catch (Exception e) {\n" +
                                                 "        return;" +
                                                 "      }\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build(method);
        });
    } 

    @Test
    public void testFinalizer() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement 1) from which control
        //           flow is guaranteed to enter a try block equipped with a finalizer, or 2) parented by a try
        //           block equipped with a finalizer, to unavoidably reach the finalizer block when no return
        //           statements are used.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      c();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode top = pathHelper.findNodeByString(cfg, "top()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode c = pathHelper.findNodeByString(cfg, "c()");

        assertFalse(pathHelper.canAvoidNode(top, c));
        assertTrue(pathHelper.canReachNode(top, b));
        assertTrue(pathHelper.canAvoidNode(top, b));
        assertFalse(pathHelper.canAvoidNode(a, c));
        assertTrue(pathHelper.canReachNode(a, b));
        assertTrue(pathHelper.canAvoidNode(a, b));
        assertFalse(pathHelper.canAvoidNode(b, c));
    }

    @Test
    public void testCatchlessTry() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement 1) from which control
        //           flow is guaranteed to enter a try block equipped with a finalizer, or 2) parented by a try
        //           block equipped with a finalizer, to unavoidably reach the finalizer block when no return
        //           statements are used.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode top = pathHelper.findNodeByString(cfg, "top()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");

        assertFalse(pathHelper.canAvoidNode(top, a));
        assertFalse(pathHelper.canAvoidNode(top, b));
        assertFalse(pathHelper.canAvoidNode(a, b));
    }

    @Test
    public void testMultipleCatchersWithFinalizer() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement 1) from which control
        //           flow is guaranteed to enter a try block equipped with multiple catchers and a finalizer,
        //           or 2) parented by a try block equipped with multiple catchers and a finalizer, to unavoidably
        //           reach the finalizer when no return statements are used.

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    top();\n" +
                                                 "    try {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    catch (IOException e) {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "    catch (RuntimeException e) {\n" +
                                                 "      c();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      breathe();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode top = pathHelper.findNodeByString(cfg, "top()");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");
        ControlFlowNode c = pathHelper.findNodeByString(cfg, "c()");
        ControlFlowNode breathe = pathHelper.findNodeByString(cfg, "breathe()");

        assertFalse(pathHelper.canAvoidNode(top, breathe));
        assertFalse(pathHelper.canAvoidNode(a, breathe));
        assertFalse(pathHelper.canAvoidNode(b, breathe));
        assertFalse(pathHelper.canAvoidNode(c, breathe));
    }

    @Test
    public void testFinalizerReturnStatementWithSimplifyingOption() {

        // contract: NaiveExceptionControlFlowStrategy should reject a try-catch construct if it is equipped with a
        //           finalizer and return statements are used anywhere in the construct

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      return;\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      b();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();

        EnumSet<NaiveExceptionControlFlowStrategy.Options> options;
        options = EnumSet.of(NaiveExceptionControlFlowStrategy.Options.ReturnWithoutFinalizers);

        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy(options));
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();
        System.out.println(cfg.toGraphVisText());

        ControlFlowNode ret = pathHelper.findNodeByString(cfg, "return");
        ControlFlowNode a = pathHelper.findNodeByString(cfg, "a()");
        ControlFlowNode b = pathHelper.findNodeByString(cfg, "b()");

        assertTrue(pathHelper.canAvoidNode(ret, a));
        assertTrue(pathHelper.canAvoidNode(ret, b));
    }
}
