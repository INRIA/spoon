package fr.inria.controlflow;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A naive over-approximating model of exception control flow without support for finalizers.
 *
 * The model uses the following assumptions:
 *
 *   1) All try-statements have at least one catcher and there are no finalizers.
 *   2) Any statement can potentially throw any exception.
 *   3) All exceptions thrown inside a try block are caught by the catchers immediately associated with the block.
 */
public class NaiveTryCatchControlFlowStrategy implements ExceptionControlFlowStrategy {
	public NaiveTryCatchControlFlowStrategy() {
		catchNodeStack = new Stack<>();
	}

	/**
	 * Handle a CtTry by generating nodes for the try, associated catchers and finalizer, then have builder
	 * process the contents of each block.
	 *
	 * @param builder  CFG builder
	 * @param tryBlock Try block
	 */
	@Override
	public void handleTryStatement(ControlFlowBuilder builder, CtTry tryBlock) {
		if (tryBlock.getCatchers().size() < 1) {
			throw new IllegalArgumentException("try without catch is not supported");
		}

		if (tryBlock.getFinalizer() != null) {
			throw new IllegalArgumentException("finalizers are not supported");
		}

		ControlFlowGraph graph = builder.getResult();
		ControlFlowNode lastNode = builder.getLastNode();
		List<ControlFlowNode> catchNodes = new ArrayList<>();

		ControlFlowNode tryNode = new ControlFlowNode(null, graph, BranchKind.TRY);
		ControlFlowNode convergeNode = new ControlFlowNode(null, graph, BranchKind.CONVERGE);

		for (CtCatch catchBlock : tryBlock.getCatchers()) {
			catchNodes.add(new ControlFlowNode(catchBlock.getParameter(), graph, BranchKind.CATCH));
		}

		graph.addEdge(lastNode, tryNode);
		builder.setLastNode(tryNode);

		catchNodeStack.push(catchNodes);

		tryBlock.getBody().accept(builder);

		catchNodeStack.pop();

		graph.addEdge(builder.getLastNode(), convergeNode);

		for (ControlFlowNode catchNode : catchNodes) {
			builder.setLastNode(catchNode);
			((CtCatch) catchNode.getStatement().getParent()).getBody().accept(builder);
			lastNode = builder.getLastNode();
			graph.addEdge(lastNode, convergeNode);
		}

		builder.setLastNode(convergeNode);
	}

	/**
	 * Handle a throw statement.
	 *
	 * @param builder The builder
	 * @param throwStatement A throw statement
	 */
	@Override
	public void handleThrowStatement(ControlFlowBuilder builder, CtThrow throwStatement) {
		// Outside of a try-catch context we just pretend not to see throw statements.
		if (catchNodeStack.isEmpty()) {
			return;
		}

		// In a try-catch context we initially treat throw statements as any other statements, allowing them to be
		// provided with two successors covering the cases of successful execution and an exception being thrown. Later
		// in postProcess() we will remove the successor for the successful execution.

		ControlFlowGraph graph = builder.getResult();
		ControlFlowNode throwNode = new ControlFlowNode(throwStatement, graph, BranchKind.STATEMENT);
		graph.addEdge(builder.getLastNode(), throwNode);
		builder.setLastNode(throwNode);
	}

	/**
	 * Handle a statement encountered by the builder, adding edges to appropriate catch nodes.
	 *
	 * @param builder CFG builder
	 * @param source  Statement node
	 */
	@Override
	public void handleStatement(ControlFlowBuilder builder, ControlFlowNode source) {
		if (catchNodeStack.isEmpty()) {
			return;
		}

		List<ControlFlowNode> catchNodes = catchNodeStack.peek();

		if (catchNodes != null) {
			ControlFlowGraph graph = builder.getResult();
			catchNodes.forEach(catchNode -> {
				graph.addEdge(source, catchNode);
			});
		}
	}

	/**
	 * Post-process the graph by removing non-catch successors from throw statements and then removing all
	 * unreachable nodes.
	 *
	 * @param graph Graph to process
	 */
	@Override
	public void postProcess(ControlFlowGraph graph) {
		removeNonCatchSuccessorsFromThrowStatements(graph);
		removeUnreachableNodes(graph);
	}

	/**
	 * Remove all non-catch successors from throw statement nodes.
	 *
	 * @param graph Graph to process
	 */
	private void removeNonCatchSuccessorsFromThrowStatements(ControlFlowGraph graph) {
		graph.findNodesOfKind(BranchKind.STATEMENT).forEach(node -> {
			if (!(node.getStatement() instanceof CtThrow)) {
				return;
			}

			node.next().stream().filter(x -> x.getKind() != BranchKind.CATCH).forEach(nextNode -> graph.removeEdge(node, nextNode));
		});
	}

	/**
	 * Remove all unreachable nodes from a graph.
	 *
	 * @param graph Graph to process
	 */
	private void removeUnreachableNodes(ControlFlowGraph graph) {
		List<ControlFlowNode> nodesToRemove;
		Function<ControlFlowGraph, List<ControlFlowNode>> fn;

		fn = g -> nodesWithoutPredecessors(g).stream().filter(node -> !(node.getKind() == BranchKind.BEGIN)).collect(Collectors.toList());
		nodesToRemove = fn.apply(graph);

		while (!nodesToRemove.isEmpty()) {
			nodesToRemove.forEach(node -> node.getParent().removeVertex(node));
			nodesToRemove = fn.apply(graph);
		}
	}

	/**
	 * Find all nodes that have an empty set of predecessors.
	 *
	 * @param graph Graph to search
	 * @return Set of nodes lacking predecessors
	 */
	private List<ControlFlowNode> nodesWithoutPredecessors(ControlFlowGraph graph) {
		List<ControlFlowNode> result = new ArrayList<>();

		for (ControlFlowNode node : graph.vertexSet()) {
			if (node.prev().size() == 0) {
				result.add(node);
			}
		}

		return result;
	}

	/**
	 * Stack of catch nodes that statements parented by a try block may jump to.
	 */
	private Stack<List<ControlFlowNode>> catchNodeStack;
}
