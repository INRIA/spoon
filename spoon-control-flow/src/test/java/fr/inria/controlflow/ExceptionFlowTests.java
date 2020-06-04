package fr.inria.controlflow;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtMethod;

import java.util.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ExceptionFlowTests {
    @Test
    public void testBasicSingle() {

        // contract: NaiveTryCatchControlFlowStrategy should result in every statement parented by a try
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
        builder.setExceptionControlFlowStrategy(new NaiveTryCatchControlFlowStrategy());
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

        // contract: NaiveTryCatchControlFlowStrategy should result in every statement parented by a try
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
        builder.setExceptionControlFlowStrategy(new NaiveTryCatchControlFlowStrategy());
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

        // contract: NaiveTryCatchControlFlowStrategy should result in every statement parented by a try
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
        builder.setExceptionControlFlowStrategy(new NaiveTryCatchControlFlowStrategy());
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
    public void testMultipleCatchers() {

        // contract: NaiveTryCatchControlFlowStrategy should result in every statement 1) from which control
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
        builder.setExceptionControlFlowStrategy(new NaiveTryCatchControlFlowStrategy());
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
    public void testThrowStatement() {
        // contract: NaiveTryCatchControlFlowStrategy should result in throw statements having paths to every
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
        builder.setExceptionControlFlowStrategy(new NaiveTryCatchControlFlowStrategy());
        builder.build(method);
        ControlFlowGraph cfg = builder.getResult();

        assertNull(findNodeByString(cfg, "unreachable()"));

        ControlFlowNode throwstmt = findNodeByString(cfg, "throw new RuntimeException()");
        ControlFlowNode boom = findNodeByString(cfg, "boom()");
        ControlFlowNode bang = findNodeByString(cfg, "bang()");

        assertTrue(canReachNode(throwstmt, boom));
        assertTrue(canReachNode(throwstmt, bang));

        assertFalse(canReachExitWithoutEnteringCatchBlock(throwstmt));
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
}
