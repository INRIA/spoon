package fr.inria.controlflow;

import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.code.CtTry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A naive over-approximating model of exception control flow in which every statement is treated as being
 * able to throw any exception, generating a path to every catcher associated with the parent try block.
 */
public class NaiveExceptionControlFlowStrategy implements ExceptionControlFlowStrategy {
    /**
     * Handle a CtTry by generating nodes for the try, associated catchers and finalizer, then have builder
     * process the contents of each block.
     *
     * @param builder CFG builder
     * @param tryBlock Try block
     */
    @Override
    public void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock) {
        if (tryBlock.getCatchers().size() != 1 || tryBlock.getFinalizer() != null) {
            throw new NotImplementedException("not implemented");
        }

        ControlFlowGraph graph = builder.getResult();
        ControlFlowNode lastNode = builder.getLastNode();

        ControlFlowNode tryNode = new ControlFlowNode(null, graph, BranchKind.TRY);
        ControlFlowNode catchNode = new ControlFlowNode(tryBlock.getCatchers().get(0).getParameter(), graph, BranchKind.CATCH);
        ControlFlowNode convergeNode = new ControlFlowNode(null, graph, BranchKind.CONVERGE);

        addEdge(builder, graph, lastNode, tryNode);
        builder.setLastNode(tryNode);

        builder.pushCatchNodes(new HashSet<>(Collections.singletonList(catchNode)));
        tryBlock.getBody().accept(builder);
        builder.popCatchNodeStack();
        addEdge(builder, graph, builder.getLastNode(), convergeNode);

        builder.setLastNode(catchNode);
        tryBlock.getCatchers().get(0).getBody().accept(builder);
        lastNode = builder.getLastNode();

        addEdge(builder, graph, lastNode, convergeNode);
        builder.setLastNode(convergeNode);
    }

    /**
     * Handle a statement encountered by the builder, adding edges to any active catch nodes.
     *
     * @param builder CFG builder
     * @param source Statement node
     */
    @Override
    public void handleStatement(ControlFlowBuilder builder, ControlFlowNode source) {
        Set<ControlFlowNode> catchNodes = builder.getCurrentCatchNodes();

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
}
