package fr.inria.controlflow;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtMethod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ExceptionFlowTests {
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

        ControlFlowNode x = findNodeByString(cfg, "x()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode c = findNodeByString(cfg, "c()");
        ControlFlowNode bang = findNodeByString(cfg, "bang()");

        assertFalse(canReachNode(x, bang));

        assertTrue(canReachExitWithoutEnteringCatchBlock(x));
        assertTrue(canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(canReachExitWithoutEnteringCatchBlock(b));
        assertTrue(canReachExitWithoutEnteringCatchBlock(c));

        assertTrue(canReachNode(a, bang));
        assertTrue(canReachNode(a, b));
        assertTrue(canReachNode(b, bang));
        assertTrue(canReachNode(b, c));
        assertTrue(canReachNode(c, bang));
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

        ControlFlowNode x = findNodeByString(cfg, "x()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode bang = findNodeByString(cfg, "bang()");
        ControlFlowNode boom = findNodeByString(cfg, "boom()");

        assertFalse(canReachNode(x, bang));
        assertTrue(canReachNode(x, boom));

        assertTrue(canReachExitWithoutEnteringCatchBlock(x));
        assertTrue(canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(canReachExitWithoutEnteringCatchBlock(b));

        assertTrue(canReachNode(a, bang));

        assertTrue(canReachNode(b, boom));
        assertFalse(canReachNode(b, bang));
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

        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode c = findNodeByString(cfg, "c()");
        ControlFlowNode boom = findNodeByString(cfg, "boom()");
        ControlFlowNode bang = findNodeByString(cfg, "bang()");

        assertTrue(canReachExitWithoutEnteringCatchBlock(a));
        assertTrue(canReachExitWithoutEnteringCatchBlock(b));
        assertTrue(canReachExitWithoutEnteringCatchBlock(c));
        assertTrue(canReachExitWithoutEnteringCatchBlock(boom));

        assertTrue(canReachNode(a, boom));
        assertTrue(canReachNode(a, bang));
        assertTrue(canReachNode(b, boom));
        assertTrue(canReachNode(b, bang));
        assertFalse(canReachNode(c, boom));
        assertTrue(canReachNode(c, bang));
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

        ControlFlowNode top = findNodeByString(cfg, "top()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode c = findNodeByString(cfg, "c()");

        assertFalse(canAvoidNode(top, c));
        assertTrue(canReachNode(top, b));
        assertTrue(canAvoidNode(top, b));
        assertFalse(canAvoidNode(a, c));
        assertTrue(canReachNode(a, b));
        assertTrue(canAvoidNode(a, b));
        assertFalse(canAvoidNode(b, c));
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

        ControlFlowNode top = findNodeByString(cfg, "top()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode c = findNodeByString(cfg, "c()");

        assertTrue(canReachExitWithoutEnteringCatchBlock(top));
        assertTrue(canReachExitWithoutEnteringCatchBlock(a));

        assertTrue(canReachNode(top, a));
        assertTrue(canReachNode(top, b));
        assertTrue(canReachNode(top, c));

        assertTrue(canReachNode(a, b));
        assertTrue(canReachNode(a, c));

        assertFalse(canReachNode(b, c));
        assertFalse(canReachNode(c, b));
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

        ControlFlowNode top = findNodeByString(cfg, "top()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");
        ControlFlowNode c = findNodeByString(cfg, "c()");
        ControlFlowNode breathe = findNodeByString(cfg, "breathe()");

        assertFalse(canAvoidNode(top, breathe));
        assertFalse(canAvoidNode(a, breathe));
        assertFalse(canAvoidNode(b, breathe));
        assertFalse(canAvoidNode(c, breathe));
    }

    @Test
    public void testDirectPathToCatchersWhenNested() {

        // contract: NaiveExceptionControlFlowStrategy should result in every statement parented by nested
        //           try blocks to have direct paths to every catcher that is able to catch an exception thrown
        //           at the control point of the statement

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  void m() {\n" +
                                                 "    try {\n" +
                                                 "      try {\n" +
                                                 "        try {\n" +
                                                 "          a();\n" +
                                                 "        }\n" +
                                                 "        catch (Exception e3) {\n" +
                                                 "          bang3();\n" +
                                                 "        }" +
                                                 "      } catch (Exception e2) {\n" +
                                                 "        bang2();\n" +
                                                 "      }\n" +
                                                 "    } catch (Exception e1) {\n" +
                                                 "      bang1();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode bang1 = findNodeByString(cfg, "bang1()");
        ControlFlowNode bang2 = findNodeByString(cfg, "bang2()");
        ControlFlowNode bang3 = findNodeByString(cfg, "bang3()");

        assertTrue(hasDirectStatementSuccessor(a, bang1));
        assertTrue(hasDirectStatementSuccessor(a, bang2));
        assertTrue(hasDirectStatementSuccessor(a, bang3));
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

        ControlFlowNode top = findNodeByString(cfg, "top()");
        ControlFlowNode a = findNodeByString(cfg, "a()");
        ControlFlowNode b = findNodeByString(cfg, "b()");

        assertFalse(canAvoidNode(top, a));
        assertFalse(canAvoidNode(top, b));
        assertFalse(canAvoidNode(a, b));
    }

    @Test
    public void testTryBlockReturnStatementExecutesFinalizers01() {
        // contract: NaiveExceptionControlFlowStrategy should result in every return statement in a try or
        //           catch block to execute all "in-scope" finalizers before jumping to the exit

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  int m() {\n" +
                                                 "    try {\n" +
                                                 "      return 1;\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      finalize();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> finalize = findNodesByString(cfg, "finalize()");
        ControlFlowNode ret1 = findNodeByString(cfg, "return 1");

        assertFalse(canAvoidAllNodes(ret1, finalize));
    }

    @Test
    public void testTryBlockReturnStatementExecutesFinalizers02() {
        // contract: NaiveExceptionControlFlowStrategy should result in every return statement in a try or
        //           catch block to execute all "in-scope" finalizers before jumping to the exit

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  int m() {\n" +
                                                 "    try {\n" +
                                                 "      foo();\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      return 1;\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      finalize();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> finalize = findNodesByString(cfg, "finalize()");
        ControlFlowNode foo = findNodeByString(cfg, "foo()");
        ControlFlowNode ret1 = findNodeByString(cfg, "return 1");

        assertFalse(canAvoidAllNodes(foo, finalize));
        assertFalse(canAvoidAllNodes(ret1, finalize));
    }

    @Test
    public void testTryBlockReturnStatementExecutesFinalizers03() {
        // contract: NaiveExceptionControlFlowStrategy should result in every return statement in a try or
        //           catch block to execute all "in-scope" finalizers before jumping to the exit

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  int m() {\n" +
                                                 "    try {\n" +
                                                 "      return 1;\n" +
                                                 "    }\n" +
                                                 "    catch (Exception e) {\n" +
                                                 "      return 2;\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      finalize();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> finalize = findNodesByString(cfg, "finalize()");
        ControlFlowNode ret1 = findNodeByString(cfg, "return 1");
        ControlFlowNode ret2 = findNodeByString(cfg, "return 2");

        assertFalse(canAvoidAllNodes(ret1, finalize));
        assertFalse(canAvoidAllNodes(ret2, finalize));
    }

    @Test
    public void testTryBlockReturnStatementExecutesFinalizers04() {
        // contract: NaiveExceptionControlFlowStrategy should result in every return statement in a try or
        //           catch block to execute all "in-scope" finalizers before jumping to the exit

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  int m() {\n" +
                                                 "    try {\n" +
                                                 "      try {\n" +
                                                 "        return 1;\n" +
                                                 "      }\n" +
                                                 "      finally {\n" +
                                                 "        finalizeInner();\n" +
                                                 "      }\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      finalizeOuter();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> finalizeInner = findNodesByString(cfg, "finalizeInner()");
        Set<ControlFlowNode> finalizeOuter = findNodesByString(cfg, "finalizeOuter()");
        ControlFlowNode ret1 = findNodeByString(cfg, "return 1");

        assertEquals(1, paths(ret1).size());

        assertFalse(canAvoidAllNodes(ret1, finalizeInner));
        assertFalse(canAvoidAllNodes(ret1, finalizeOuter));
    }

    @Test
    public void testTryBlockReturnStatementExecutesFinalizers05() {
        // contract: NaiveExceptionControlFlowStrategy should result in every return statement in a try or
        //           catch block to execute all "in-scope" finalizers before jumping to the exit

        CtMethod<?> method = Launcher.parseClass("class A {\n" +
                                                 "  int m() {\n" +
                                                 "    try {\n" +
                                                 "      try {\n" +
                                                 "        if (random > 0.5f) {\n" +
                                                 "          return 1;\n" +
                                                 "        }\n" +
                                                 "      }\n" +
                                                 "      finally {\n" +
                                                 "        finalizeInner();\n" +
                                                 "      }\n" +
                                                 "      \n" +
                                                 "      a();\n" +
                                                 "    }\n" +
                                                 "    finally {\n" +
                                                 "      finalizeOuter();\n" +
                                                 "    }\n" +
                                                 "  }\n" +
                                                 "}\n").getMethods().iterator().next();

        ControlFlowBuilder builder = new ControlFlowBuilder();
        builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        Set<ControlFlowNode> finalizeInner = findNodesByString(cfg, "finalizeInner()");
        Set<ControlFlowNode> finalizeOuter = findNodesByString(cfg, "finalizeOuter()");
        ControlFlowNode branch = findNodeByString(cfg, "random > 0.5F");
        ControlFlowNode ret1 = findNodeByString(cfg, "return 1");
        ControlFlowNode a = findNodeByString(cfg, "a()");

        assertFalse(canAvoidAllNodes(ret1, finalizeInner));
        assertFalse(canAvoidAllNodes(ret1, finalizeOuter));
        assertFalse(canReachNode(ret1, a));

        assertTrue(canReachNode(branch, a));
        assertFalse(canAvoidAllNodes(branch, finalizeInner));
        assertFalse(canAvoidAllNodes(branch, finalizeOuter));
    }

    /**
     * Memoization of paths.
     */
    Map<ControlFlowNode, List<List<ControlFlowNode>>> pathsMemo = new HashMap<>();

    /**
     * Get the set of possible paths to the exit node from a given starting node.
     *
     * @param node Starting node
     * @return Set of possible paths
     */
    private List<List<ControlFlowNode>> paths(ControlFlowNode node) {
        if (pathsMemo.containsKey(node)) {
            return pathsMemo.get(node);
        }

        List<List<ControlFlowNode>> result = new ArrayList<>();

        for (ControlFlowNode nextNode : node.next()) {
            result.add(new ArrayList<>(Arrays.asList(node, nextNode)));
        }

        result = paths(result);
        pathsMemo.put(node, result);
        return result;
    }

    /**
     * Get the set of possible paths to the exit node given a set of potentially incomplete paths.
     *
     * @param prior Set of potentially incomplete paths
     * @return Set of possible paths
     */
    private List<List<ControlFlowNode>> paths(List<List<ControlFlowNode>> prior) {
        List<List<ControlFlowNode>> result = new ArrayList<>();
        boolean extended = false;

        for (List<ControlFlowNode> path : prior) {
            ControlFlowNode lastNode = path.get(path.size() - 1);

            if (lastNode.getKind() == BranchKind.EXIT) {
                result.add(new ArrayList<>(path));
            } else {
                for (ControlFlowNode nextNode : lastNode.next()) {
                    extended = true;
                    List<ControlFlowNode> thisPath = new ArrayList<>(path);
                    thisPath.add(nextNode);
                    result.add(thisPath);
                }
            }
        }

        if (extended) {
            return paths(result);
        } else {
            return result;
        }
    }

    /**
     * Check whether a path contains a catch block node.
     *
     * @param nodes Path to check
     * @return True if path contains a catch block node, false otherwise
     */
    private boolean containsCatchBlockNode(List<ControlFlowNode> nodes) {
        return nodes.stream().anyMatch(node -> node.getKind() == BranchKind.CATCH);
    }

    /**
     * Check whether a node has a path to the exit node that does not enter a catch block.
     *
     * @param node Node to check
     * @return True if node has path to exit that does not enter any catch block, false otherwise
     */
    private boolean canReachExitWithoutEnteringCatchBlock(ControlFlowNode node) {
        return paths(node).stream().anyMatch(xs -> !containsCatchBlockNode(xs));
    }

    /**
     * Check whether a node has a path to another node.
     *
     * @param source Starting node
     * @param target Target node
     * @return True if there is a path from source to target, false otherwise
     */
    private boolean canReachNode(ControlFlowNode source, ControlFlowNode target) {
        return paths(source).stream().anyMatch(xs -> xs.contains(target));
    }

    /**
     * Check whether a node can reach the exit without crossing a certain node.
     *
     * @param source Starting node
     * @param target Target node
     * @return True if there exists a path between source and exit that does not include target, false otherwise
     */
    private boolean canAvoidNode(ControlFlowNode source, ControlFlowNode target) {
        return !paths(source).stream().allMatch(xs -> xs.contains(target));
    }

    /**
     * Check whether a node can reach the exit without reaching any of a set of nodes.
     *
     * @param source Starting node
     * @param targets Target nodes
     * @return True if there exists a path between source and exit that does not include any node in target, false otherwise
     */
    private boolean canAvoidAllNodes(ControlFlowNode source, Set<ControlFlowNode> targets) {
        return !paths(source).stream().allMatch(xs -> intersect(new HashSet<>(xs), targets).size() != 0);
    }

    /**
     * Intersect two sets of nodes.
     *
     * @param a First set
     * @param b Second set
     * @return Intersection of the first and seconds sets
     */
    private Set<ControlFlowNode> intersect(Set<ControlFlowNode> a, Set<ControlFlowNode> b) {
        Set<ControlFlowNode> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    /**
     * Filter a control flow path to only include statement nodes.
     *
     * @param path Path to filter
     * @return Path containing only statement nodes
     */
    private List<ControlFlowNode> statementPath(List<ControlFlowNode> path) {
        return path.stream().filter(x -> x.getKind() == BranchKind.STATEMENT).collect(Collectors.toList());
    }

    /**
     * Check if a path (a list of nodes) contains a transition from source to target.
     *
     * @param path Path to check
     * @param source Source node
     * @param target Target node
     * @return True if path contains a transition from source to target, false otherwise
     */
    private boolean hasTransition(List<ControlFlowNode> path, ControlFlowNode source, ControlFlowNode target) {
        return IntStream.range(0, path.size() - 1).anyMatch(n -> path.get(n).equals(source) && path.get(n + 1).equals(target));
    }

    /**
     * Check if a node 'source' has a direct path to a statement node 'target', meaning no other statement
     * nodes are found in between 'source' and 'target'.
     *
     * @param source Node to check
     * @param target Successor to find
     * @return True if source has a direct path to target, false otherwise
     */
    private boolean hasDirectStatementSuccessor(ControlFlowNode source, ControlFlowNode target) {
        return paths(source).stream().map(this::statementPath).anyMatch(path -> hasTransition(path, source, target));
    }

    /**
     * Find a node in a ControlFlowGraph by matching on the string representation of the statement
     * stored in the node (if any).
     *
     * @param graph Graph to search
     * @param s String to match against statement
     * @return First node found with statement matching string, or null if none was found
     */
    private ControlFlowNode findNodeByString(ControlFlowGraph graph, String s) {
        for (ControlFlowNode node : graph.vertexSet()) {
            if (node.getStatement() != null && node.getStatement().toString().equals(s)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Find a set of nodes in a ControlFlowGraph by matching on the string representation of the statement
     * stored in the node (if any).
     *
     * @param graph Graph to search
     * @param s String to match against statement
     * @return Set of nodes found with statement matching string
     */
    private Set<ControlFlowNode> findNodesByString(ControlFlowGraph graph, String s) {
        Set<ControlFlowNode> result = new HashSet<>();

        for (ControlFlowNode node : graph.vertexSet()) {
            if (node.getStatement() != null && node.getStatement().toString().equals(s)) {
                result.add(node);
            }
        }

        return result;
    }
}
