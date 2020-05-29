package fr.inria.controlflow;

import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtTry;

import java.util.*;

/**
 * A naive over-approximating model of exception control flow in which every statement is treated as being
 * able to throw any exception, generating a path to every catcher associated with the parent try block.
 */
public class NaiveExceptionControlFlowStrategy implements ExceptionControlFlowStrategy {
    public NaiveExceptionControlFlowStrategy() {
        catchNodeStack = new Stack<>();
    }

    /**
     * Handle a CtTry by generating nodes for the try, associated catchers and finalizer, then have builder
     * process the contents of each block.
     *
     * @param builder CFG builder
     * @param tryBlock Try block
     */
    @Override
    public void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock) {
        ControlFlowGraph graph = builder.getResult();
        ControlFlowNode lastNode = builder.getLastNode();
        List<ControlFlowNode> catchNodes = new ArrayList<>();

        ControlFlowNode tryNode = new ControlFlowNode(null, graph, BranchKind.TRY);

        for (CtCatch catchBlock : tryBlock.getCatchers()) {
            catchNodes.add(new ControlFlowNode(catchBlock.getParameter(), graph, BranchKind.CATCH));
        }

        ControlFlowNode finallyNode = tryBlock.getFinalizer() == null ? null : new ControlFlowNode(null, graph, BranchKind.FINALLY);
        ControlFlowNode convergeNode = new ControlFlowNode(null, graph, BranchKind.CONVERGE);

        addEdge(builder, graph, lastNode, tryNode);
        builder.setLastNode(tryNode);

        catchNodeStack.push(catchNodes);
        tryBlock.getBody().accept(builder);
        catchNodeStack.pop();
        addEdge(builder, graph, builder.getLastNode(), finallyNode != null ? finallyNode : convergeNode);

        for (ControlFlowNode catchNode : catchNodes) {
            builder.setLastNode(catchNode);
            ((CtCatch) catchNode.getStatement().getParent()).getBody().accept(builder);
            lastNode = builder.getLastNode();
            addEdge(builder, graph, lastNode, finallyNode != null ? finallyNode : convergeNode);
        }

        builder.setLastNode(finallyNode != null ? finallyNode : convergeNode);

        if (finallyNode != null) {
            tryBlock.getFinalizer().accept(builder);
            addEdge(builder, graph, builder.getLastNode(), convergeNode);
            builder.setLastNode(convergeNode);
        }
    }

    /**
     * Handle a statement encountered by the builder, adding edges to any active catch nodes.
     *
     * @param builder CFG builder
     * @param source Statement node
     */
    @Override
    public void handleStatement(ControlFlowBuilder builder, ControlFlowNode source) {
        List<ControlFlowNode> catchNodes = currentCatchNodes();

        if (catchNodes != null) {
            ControlFlowGraph graph = builder.getResult();
            catchNodes.forEach(catchNode -> {
                graph.addEdge(source, catchNode);
            });
        }
    }

    /**
     * Add an edge between source and target and handle the statement in source if there is one.
     *
     * @param builder CFG builder
     * @param graph CFG
     * @param source Source node
     * @param target Target node
     */
    private void addEdge(ControlFlowBuilder builder, ControlFlowGraph graph, ControlFlowNode source, ControlFlowNode target) {
        graph.addEdge(source, target);

        if (source.getKind() != BranchKind.CATCH && source.getStatement() != null) {
            handleStatement(builder, source);
        }
    }

    /**
     * Try peeking at the top of the catch node stack, returning null if the stack is empty.
     *
     * @return List of catch nodes, or null if the stack is empty
     */
    private List<ControlFlowNode> currentCatchNodes() {
        if (catchNodeStack.isEmpty()) {
            return null;
        } else {
            return catchNodeStack.peek();
        }
    }

    /**
     * Stack of catch nodes that statements parented by a try block may jump to.
     */
    private Stack<List<ControlFlowNode>> catchNodeStack;
}
