package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import spoon.reflect.code.CtIf;

import java.util.List;
import java.util.Set;

/**
 * SmPLCFGAdapter imposes constraints on and adaptations to a ControlFlowGraph for use
 * in SmPL-related contexts. The constraints are that the ControlFlowGraph must not be
 * simplified (see ControlFlowGraph::simplify).
 *
 * The adaptations are:
 * 1) The outmost BLOCK_BEGIN node (successor to the BEGIN node) is removed.
 * 2) All remaining BLOCK_BEGIN nodes are tagged with String "trueBranch" or "falseBranch".
 * 3) All BLOCK_END nodes are removed.
 * 4) All CONVERGE nodes with single successor being another CONVERGE node are removed.
 */
public class SmPLCFGAdapter {
    public SmPLCFGAdapter(ControlFlowGraph cfg) {
        this.cfg = cfg;

        if (cfg.findNodesOfKind(BranchKind.BLOCK_BEGIN).size() == 0) {
            throw new IllegalArgumentException("The CFG must NOT be simplified (see ControlFlowGraph::simplify)");
        }

        removeOutermostBlockBeginNode(cfg);
        removeBlockEndNodes(cfg);
        removeRedundantConvergenceNodes(cfg);

        // Annotate branches
        for (ControlFlowNode node : cfg.vertexSet()) {
            if (node.getKind() != BranchKind.BRANCH) {
                // We're only looking for BRANCH nodes
                continue;
            }

            if (node.next().size() != 2) {
                throw new IllegalStateException("branch node with invalid number of successors");
            }

            // If is the only supported branch statement at this time
            CtIf ifStm = (CtIf) node.getStatement().getParent();

            ControlFlowNode n1 = node.next().get(0);
            ControlFlowNode n2 = node.next().get(1);

            // If only one successor is a BLOCK_BEGIN the branch is else-less.
            if (n1.getKind() == BranchKind.BLOCK_BEGIN && n2.getKind() == BranchKind.CONVERGE) {
                n1.setTag("trueBranch");
            } else if (n2.getKind() == BranchKind.BLOCK_BEGIN && n1.getKind() == BranchKind.CONVERGE) {
                n2.setTag("trueBranch");
            } else {
                // Both successors are blocks

                ControlFlowNode n1next = n1.next().get(0);
                ControlFlowNode n2next = n2.next().get(0);

                if (n1next.getKind() == BranchKind.CONVERGE && n2next.getKind() == BranchKind.CONVERGE) {
                    // Both blocks are empty, we can choose labels arbitrarily
                    n1.setTag("trueBranch");
                    n2.setTag("falseBranch");
                } else {
                    // One or both blocks contains statements, must assign correct label

                    boolean n1HasStatements = n1next.getStatement() != null;
                    boolean n1IsTrueBranch;

                    if (n1HasStatements) {
                        n1IsTrueBranch = n1next.getStatement().getParent() == ifStm.getThenStatement();
                    } else {
                        n1IsTrueBranch = n2next.getStatement().getParent() == ifStm.getElseStatement();
                    }

                    if (n1IsTrueBranch) {
                        n1.setTag("trueBranch");
                        n2.setTag("falseBranch");
                    } else {
                        n1.setTag("falseBranch");
                        n2.setTag("trueBranch");
                    }
                }
            }
        }
    }

    /**
     * @see ControlFlowGraph::vertexSet
     */
    public Set<ControlFlowNode> vertexSet() {
        return cfg.vertexSet();
    }

    /**
     * @see ControlFlowGraph::incomingEdgesOf
     */
    public Set<ControlFlowEdge> incomingEdgesOf(ControlFlowNode vertex) {
        return cfg.incomingEdgesOf(vertex);
    }

    /**
     * @see ControlFlowGraph::outgoingEdgesOf
     */
    public Set<ControlFlowEdge> outgoingEdgesOf(ControlFlowNode vertex) {
        return cfg.outgoingEdgesOf(vertex);
    }

    /**
     * @see ControlFlowGraph::findNodeById
     */
    public ControlFlowNode findNodeById(int id) {
        return cfg.findNodeById(id);
    }

    /**
     * @see ControlFlowGraph::findNodesOfKind
     */
    public List<ControlFlowNode> findNodesOfKind(BranchKind kind) {
        return cfg.findNodesOfKind(kind);
    }

    /**
     * @see ControlFlowGraph::getExitNode
     */
    public ControlFlowNode getExitNode() {
        return cfg.getExitNode();
    }

    /**
     * Remove a node from a CFG, adding edges to preserve paths.
     * @param cfg CFG to operate on
     * @param node Node to remove
     */
    private static void removeNode(ControlFlowGraph cfg, ControlFlowNode node) {
        Set<ControlFlowEdge> incoming = cfg.incomingEdgesOf(node);
        Set<ControlFlowEdge> outgoing = cfg.outgoingEdgesOf(node);

        for (ControlFlowEdge in : incoming) {
            for (ControlFlowEdge out : outgoing) {
                cfg.addEdge(in.getSourceNode(), out.getTargetNode());
            }
        }

        cfg.edgesOf(node).forEach(cfg::removeEdge);
        cfg.removeVertex(node);
    }

    /**
     * Remove all nodes of kind BLOCK_END from a given CFG.
     * @param cfg CFG to operate on
     */
    private static void removeBlockEndNodes(ControlFlowGraph cfg) {
        cfg.findNodesOfKind(BranchKind.BLOCK_END).forEach((node) -> removeNode(cfg, node));
    }

    /**
     * Remove any convergence nodes having another convergence node as single successor.
     * @param cfg CFG to operate on
     */
    private static void removeRedundantConvergenceNodes(ControlFlowGraph cfg) {
        cfg.findNodesOfKind(BranchKind.CONVERGE).forEach((node) -> {
            if (node.next().size() == 1 && node.next().get(0).getKind() == BranchKind.CONVERGE) {
                removeNode(cfg, node);
            }
        });
    }

    /**
     * If the BEGIN node has a BLOCK_BEGIN as successor, remove the BLOCK_BEGIN. This BLOCK_BEGIN
     * generally represents the block enclosing the full method body.
     * @param cfg CFG to operate on
     */
    private static void removeOutermostBlockBeginNode(ControlFlowGraph cfg) {
        cfg.findNodesOfKind(BranchKind.BEGIN).forEach((node) -> {
            if (node.next().get(0).getKind() == BranchKind.BLOCK_BEGIN) {
                removeNode(cfg, node.next().get(0));
            }
        });
    }

    /**
     * The adapted CFG.
     */
    private final ControlFlowGraph cfg;
}
