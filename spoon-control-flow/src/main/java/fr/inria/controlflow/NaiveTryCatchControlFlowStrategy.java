package fr.inria.controlflow;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A naive over-approximating model of exception control flow without support for finalizers.
 *
 * The model uses the following assumptions:
 *
 *   1) All try-statements have at least one catcher and there are no finalizers.
 *   2) Any statement can potentially throw any exception.
 *   3) All exceptions thrown inside a try block are caught by the catchers immediately associated with the block.
 *
 * The model offers a choice (default: disabled) of whether to add paths between empty try {} blocks and their
 * catchers. This is because expressions of the form "try { } catch(Exception e) { foo(); }" (i.e empty try blocks)
 * are legal in Java, despite the statement "foo()" trivially being unreachable. In some use cases, excluding such
 * unreachable statements from the control flow graph may be desirable, while in other cases the information loss
 * may be undesirable. The default choice of not adding these paths was chosen due to how the produced graph more
 * accurately models the actual control flow of an execution, while the other option produces a graph that can be
 * said to show what the Java compiler considers to be reachable.
 */
public class NaiveTryCatchControlFlowStrategy implements ExceptionControlFlowStrategy {
	/**
	 * Per-instance option flags for NaiveTryCatchControlFlowStrategy
	 */
	public enum Options {
		/**
		 * Add paths between the end of an empty try {} block and its catchers.
		 */
		AddPathsForEmptyTryBlocks;
	}

	/**
	 * Create a new NaiveTryCatchControlFlowStrategy using the default set of options.
	 */
	public NaiveTryCatchControlFlowStrategy() {
		this(EnumSet.noneOf(Options.class));
	}

	/**
	 * Create a new NaiveTryCatchControlFlowStrategy using the given set of options.
	 *
	 * @param options Options to use
	 */
	public NaiveTryCatchControlFlowStrategy(EnumSet<Options> options) {
		instanceOptions = options;
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
			if (tryBlock.getBody().getStatements().size() == 0 && instanceOptions.contains(Options.AddPathsForEmptyTryBlocks)) {
				graph.addEdge(tryNode.next().get(0).next().get(0), catchNode);
			}

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
	 * unreachable catch nodes.
	 *
	 * @param graph Graph to process
	 */
	@Override
	public void postProcess(ControlFlowGraph graph) {
		removeNonCatchSuccessorsFromThrowStatements(graph);
		removeUnreachableCatchNodes(graph);
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

			node.next().stream().filter(x -> x.getKind() != BranchKind.CATCH).forEach(nextNode -> {
				graph.removeEdge(node, nextNode);
				removePathWhileUnreachable(nextNode);
			});
		});
	}

	/**
	 * Remove all unreachable catch nodes from a graph.
	 *
	 * @param graph Graph to process
	 */
	private void removeUnreachableCatchNodes(ControlFlowGraph graph) {
		nodesWithoutPredecessors(graph).stream().filter(node -> node.getKind() == BranchKind.CATCH).forEach(this::removePathWhileUnreachable);
	}

	/**
	 * Given a starting node, remove it and iteratively remove any successors that were made unreachable by a
	 * removal until such removals did not cause any new nodes to become unreachable.
	 *
	 * @param start Starting node
	 */
	private void removePathWhileUnreachable(ControlFlowNode start) {
		Deque<ControlFlowNode> nodesToRemove = new LinkedList<ControlFlowNode>(Collections.singletonList(start));

		while (!nodesToRemove.isEmpty()) {
			ControlFlowNode node = nodesToRemove.removeFirst();
			node.next().stream().filter(x -> x.prev().size() == 1).forEach(nodesToRemove::addLast);
			node.getParent().removeVertex(node);
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

	/**
	 * Flag indicating whether paths should be added between an empty try {} block and its catchers.
	 */
	private EnumSet<Options> instanceOptions;
}
