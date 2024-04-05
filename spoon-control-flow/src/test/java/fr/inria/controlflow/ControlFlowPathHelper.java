package fr.inria.controlflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for analyzing paths in the control flow graph
 */
public class ControlFlowPathHelper {
    /**
     * Memoization of paths.
     */
    private final Map<ControlFlowNode, List<List<ControlFlowNode>>> pathsMemo = new HashMap<>();

    /**
     * Get a list of possible paths to the exit node from a given starting node.
     *
     * @param node Starting node
     * @return List of possible paths
     */
    List<List<ControlFlowNode>> paths(ControlFlowNode node) {
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
     * Get a list of possible paths to the exit node given a set of potentially incomplete paths.
     *
     * @param prior Set of potentially incomplete paths
     * @return List of possible paths
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
    boolean canReachExitWithoutEnteringCatchBlock(ControlFlowNode node) {
        return paths(node).stream().anyMatch(xs -> !containsCatchBlockNode(xs));
    }

    /**
     * Check whether a node has a path to another node.
     *
     * @param source Starting node
     * @param target Target node
     * @return True if there is a path from source to target, false otherwise
     */
    boolean canReachNode(ControlFlowNode source, ControlFlowNode target) {
        return paths(source).stream().anyMatch(xs -> xs.contains(target));
    }

    /**
     * Check whether a node can reach the exit without crossing a certain node.
     *
     * @param source Starting node
     * @param avoid Avoid node
     * @return True if there exists a path between source and exit that does not include avoid, false otherwise
     */
    boolean canAvoidNode(ControlFlowNode source, ControlFlowNode avoid) {
        return !paths(source).stream().allMatch(xs -> xs.contains(avoid));
    }

    /**
     * Find a node in a ControlFlowGraph by matching on the string representation of the statement
     * stored in the node (if any).
     *
     * @param graph Graph to search
     * @param s String to match against statement
     * @return First node found with statement matching string, or null if none was found
     */
    ControlFlowNode findNodeByString(ControlFlowGraph graph, String s) {
        for (ControlFlowNode node : graph.vertexSet()) {
            if (node.getStatement() != null && node.getStatement().toString().equals(s)) {
                return node;
            }
        }

        return null;
    }
}
