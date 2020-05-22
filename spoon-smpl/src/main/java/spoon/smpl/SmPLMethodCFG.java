package spoon.smpl;

import fr.inria.controlflow.*;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.*;

/**
 * An SmPLMethodCFG creates and adapts a ControlFlowGraph for a given method such as to make it
 * suitable for use in an SmPL context.
 */
public class SmPLMethodCFG {
    /**
     * UnsupportedElementSwapper takes a CtMethod and replaces unsupported AST elements e with elements e'
     * such that UnsupportedElementSwapper.isUnsupportedElementMarker(e') will hold true. The replacements
     * can be reversed by calling the method restore().
     */
    public static class UnsupportedElementSwapper extends CtScanner {
        /**
         * Replace all unsupported elements of a given method with "unsupported element markers".
         *
         * @param ctMethod Method to process
         */
        public UnsupportedElementSwapper(CtMethod<?> ctMethod) {
            this.ctMethod = ctMethod;

            isRestoring = false;
            swappedElements = new HashMap<>();
            swapIndex = 0;

            ctMethod.accept(this);
        }

        /**
         * Check whether a given element is a marker substituting an unsupported element.
         *
         * @param e Element to check
         * @return True if the element is a marker substituting an unsupported element
         */
        public static boolean isUnsupportedElementMarker(CtElement e) {
            return (e instanceof CtInvocation
                    && ((CtInvocation<?>) e).getTarget() == null
                    && ((CtInvocation<?>) e).getExecutable().getSimpleName().equals(SmPLJavaDSL.getUnsupportedElementName()));
        }

        /**
         * Restore all replacements.
         */
        public void restore() {
            isRestoring = true;
            ctMethod.accept(this);
            isRestoring = false;
        }

        @Override
        public void visitCtWhile(CtWhile whileLoop) {
            replace(whileLoop);
        }

        @Override
        public void visitCtBreak(CtBreak breakStatement) {
            replace(breakStatement);
        }

        @Override
        public <T> void visitCtInvocation(CtInvocation<T> invocation) {
            if (!isRestoring) {
                super.visitCtInvocation(invocation);
            } else {
                if (!isUnsupportedElementMarker(invocation)) {
                    return;
                }

                int index = ((CtLiteral<Integer>) invocation.getArguments().get(0)).getValue();

                invocation.replace(swappedElements.get(index));
            }
        }

        /**
         * Replace a single element.
         *
         * @param e Element to replace
         */
        private void replace(CtElement e) {
            swappedElements.put(swapIndex, e);
            e.replace(createReplacementInvocation(e.getFactory(), swapIndex));

            swapIndex += 1;
        }

        /**
         * Create a marker element for substituting a single unsupported element.
         *
         * @param factory Spoon Factory
         * @param index Replacement index
         * @return Marker element
         */
        private static CtInvocation<Void> createReplacementInvocation(Factory factory, int index) {
            CtExecutableReference<Void> exe = factory.createExecutableReference();
            exe.setType(factory.createCtTypeReference(Void.class));
            exe.setSimpleName(SmPLJavaDSL.getUnsupportedElementName());
            exe.setParameters(Arrays.asList(factory.createCtTypeReference(int.class)));

            List<CtExpression<?>> args = Arrays.asList(factory.createLiteral(index));

            return factory.createInvocation(null, exe, args);
        }

        /**
         * Target method.
         */
        private CtMethod<?> ctMethod;

        /**
         * Instance state flag signalling whether or not we are currently in "restore" mode, which if true
         * alters the behavior when scanning replacement marker elements.
         */
        private boolean isRestoring;

        /**
         * Replacement index counter.
         */
        private int swapIndex;

        /**
         * Replacement storage.
         */
        private Map<Integer, CtElement> swappedElements;
    }

    /**
     * A NodeTag is a combination of a String label and an AST element anchor.
     *
     * The idea is that certain nodes (BRANCH, BLOCK_BEGIN and CONVERGE) in an SmPL-adapted CFG should
     * be tagged with a NodeTag using labels "branch", ("trueBranch" or "falseBranch") and "after",
     * respectively, along with the appropriate AST element for anchoring prepend and/or append operations
     * to.
     */
    public static class NodeTag {
        /**
         * Create a new NodeTag.
         * @param label Label
         * @param anchor Anchor element
         */
        public NodeTag(String label, CtElement anchor) {
            this.label = label;
            this.anchor = anchor;
        }

        /**
         * Get the label string.
         * @return Label string
         */
        public String getLabel() {
            return label;
        }

        /**
         * Get the anchor element.
         * @return Anchor element
         */
        public CtElement getAnchor() {
            return anchor;
        }

        @Override
        public String toString() {
            return "NodeTag(" + label + ", " + anchor.toString() + ")";
        }

        /**
         * Label string.
         */
        private final String label;

        /**
         * Anchor element.
         */
        private final CtElement anchor;
    }

    /**
     * Create a new SmPL-adapted CFG from a given method element.
     * @param method Method for which to generate an SmPL-adapted CFG
     */
    public SmPLMethodCFG(CtMethod<?> method) {
        this.swapper = new UnsupportedElementSwapper(method);
        this.cfg = new ControlFlowBuilder().build(method.getBody());

        removeOutermostBlockBeginNode(cfg);
        removeBlockEndNodes(cfg);

        // Add method header node and annotate method body BLOCK_BEGIN node
        cfg.findNodesOfKind(BranchKind.BEGIN).forEach((cfgEntryNode) -> {
            if (cfgEntryNode.next().size() != 1) {
                throw new IllegalArgumentException("invalid BEGIN node in CFG");
            }

            ControlFlowNode cfgEntryNodeSuccessor = cfgEntryNode.next().get(0);

            // Remove the path from the CFG entry node to its successor
            cfg.removeEdge(cfgEntryNode, cfgEntryNodeSuccessor);

            // Create a node for the method header / signature
            ControlFlowNode methodHeaderNode = new ControlFlowNode(method, cfg, BranchKind.STATEMENT);
            methodHeaderNode.setTag(new NodeTag("methodHeader", method));

            // Connect CFG entry node -> method header node -> CFG entry successor
            cfg.addEdge(cfgEntryNode, methodHeaderNode);
            cfg.addEdge(methodHeaderNode, cfgEntryNodeSuccessor);
        });

        // Annotate markers for unsupported elements
        cfg.findNodesOfKind(BranchKind.STATEMENT).forEach((node) -> {
            if (UnsupportedElementSwapper.isUnsupportedElementMarker(node.getStatement())) {
                node.setTag(new NodeTag("unsupported", node.getStatement()));
            }
        });

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

            node.setTag(new NodeTag("branch", ifStm));

            ControlFlowNode afterNode = findConvergenceNode(node);
            afterNode.setTag(new NodeTag("after", ifStm));

            ControlFlowNode n1 = node.next().get(0);
            ControlFlowNode n2 = node.next().get(1);

            // If only one successor is a BLOCK_BEGIN the branch is else-less.
            if (n1.getKind() == BranchKind.BLOCK_BEGIN && n2.getKind() == BranchKind.CONVERGE) {
                n1.setTag(new NodeTag("trueBranch", ifStm.getThenStatement()));
            } else if (n2.getKind() == BranchKind.BLOCK_BEGIN && n1.getKind() == BranchKind.CONVERGE) {
                n2.setTag(new NodeTag("trueBranch", ifStm.getThenStatement()));
            } else {
                // Both successors are blocks

                ControlFlowNode n1next = n1.next().get(0);
                ControlFlowNode n2next = n2.next().get(0);

                if (n1next.getKind() == BranchKind.CONVERGE && n2next.getKind() == BranchKind.CONVERGE) {
                    // Both blocks are empty, we can choose labels arbitrarily
                    n1.setTag(new NodeTag("trueBranch", ifStm.getThenStatement()));
                    n2.setTag(new NodeTag("falseBranch", ifStm.getElseStatement()));
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
                        n1.setTag(new NodeTag("trueBranch", ifStm.getThenStatement()));
                        n2.setTag(new NodeTag("falseBranch", ifStm.getElseStatement()));
                    } else {
                        n1.setTag(new NodeTag("falseBranch", ifStm.getElseStatement()));
                        n2.setTag(new NodeTag("trueBranch", ifStm.getThenStatement()));
                    }
                }
            }
        }
    }

    public void restoreUnsupportedElements() {
        swapper.restore();
    }

    /**
     * Check a given node for being the special method header node.
     *
     * @param node Node to check
     * @return True if given node is the special method header node, false otherwise
     */
    public static boolean isMethodHeaderNode(ControlFlowNode node) {
        return node.getTag() instanceof NodeTag
               && ((NodeTag) node.getTag()).getLabel().equals("methodHeader");
    }

    /**
     * Check a given node for being a marker substituting an unsupported element.
     *
     * @param node Node to check
     * @return True if given node is a marker substituting an unsupported element, false otherwise
     */
    public static boolean isUnsupportedElementNode(ControlFlowNode node) {
        return node.getTag() instanceof NodeTag
               && ((NodeTag) node.getTag()).getLabel().equals("unsupported");
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

    @Override
    public String toString() {
        return cfg.toGraphVisText();
    }

    /**
     * Find the post-branch convergence node corresponding to a BRANCH node.
     *
     * @param node Branch node to find convergence node for
     * @return Convergence node corresponding to given branch node
     */
    private static ControlFlowNode findConvergenceNode(ControlFlowNode node) {
        // FIXME: this relies on implementation details of ControlFlowBuilder::visitCtIf and ControlFlowNode.count
        // A good fix would be adding this info explicitly to spoon-control-flow, e.g by having
        // ControlFlowNodes have a field .postBranchConverge that was set to the specific
        // convergence node created for the branch, and having a method ControlFlowNode::getPostBranchConvergenceNode
        return node.getParent().findNodeById(node.getId() + 1);
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

    /**
     * Swapper for unsupported elements.
     */
    private UnsupportedElementSwapper swapper;
}
