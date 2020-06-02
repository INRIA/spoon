package fr.inria.controlflow;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A naive over-approximating model of exception control flow in which every statement is treated as being
 * able to throw any exception, generating a path to every catcher associated with the parent try block.
 */
public class NaiveExceptionControlFlowStrategy implements ExceptionControlFlowStrategy {
	public NaiveExceptionControlFlowStrategy() {
		catchNodeStack = new Stack<>();
		finalizerStack = new Stack<>();
		isFinalizing = false;
	}

	/**
	 * Handle a CtTry by generating nodes for the try, associated catchers and finalizer, then have builder
	 * process the contents of each block.
	 *
	 * @param builder  CFG builder
	 * @param tryBlock Try block
	 */
	@Override
	public void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock) {
		ControlFlowGraph graph = builder.getResult();
		ControlFlowNode lastNode = builder.getLastNode();
		List<ControlFlowNode> catchNodes = new ArrayList<>();

		ControlFlowNode tryNode = new ControlFlowNode(null, graph, BranchKind.TRY);
		ControlFlowNode convergeNode = new ControlFlowNode(null, graph, BranchKind.CONVERGE);

		for (CtCatch catchBlock : tryBlock.getCatchers()) {
			catchNodes.add(new ControlFlowNode(catchBlock.getParameter(), graph, BranchKind.CATCH));
		}

		ControlFlowNode finallyNode = null;

		if (tryBlock.getFinalizer() != null) {
			finallyNode = new ControlFlowNode(null, graph, BranchKind.FINALLY);
			finalizerStack.push(tryBlock.getFinalizer());
		}

		addEdge(builder, graph, lastNode, tryNode);
		builder.setLastNode(tryNode);

		if (catchNodes.size() > 0) {
			catchNodeStack.push(catchNodes);
		}

		tryBlock.getBody().accept(builder);

		if (catchNodes.size() > 0) {
			catchNodeStack.pop();
		}

		if (builder.getLastNode() == null) {
			return;
		}

		addEdge(builder, graph, builder.getLastNode(), finallyNode != null ? finallyNode : convergeNode);

		for (ControlFlowNode catchNode : catchNodes) {
			builder.setLastNode(catchNode);
			((CtCatch) catchNode.getStatement().getParent()).getBody().accept(builder);
			lastNode = builder.getLastNode();
			addEdge(builder, graph, lastNode, finallyNode != null ? finallyNode : convergeNode);
		}

		builder.setLastNode(finallyNode != null ? finallyNode : convergeNode);

		if (finallyNode != null) {
			finalizerStack.pop();
			tryBlock.getFinalizer().accept(builder);
			addEdge(builder, graph, builder.getLastNode(), convergeNode);
			builder.setLastNode(convergeNode);
		}
	}

	/**
	 * Handle a statement encountered by the builder, adding edges to any active catch nodes.
	 *
	 * @param builder CFG builder
	 * @param source  Statement node
	 * @return True if the builder should abort processing the node, false otherwise
	 */
	@Override
	public boolean handleStatement(ControlFlowBuilder builder, ControlFlowNode source) {
		List<ControlFlowNode> catchNodes = currentCatchNodes();

		if (catchNodes != null) {
			ControlFlowGraph graph = builder.getResult();
			catchNodes.forEach(catchNode -> {
				graph.addEdge(source, catchNode);
			});
		}

		if (source.getStatement() instanceof CtReturn) {
			if (!isFinalizing) {
				ControlFlowGraph graph = builder.getResult();
				isFinalizing = true;

				Stack<CtBlock<?>> finalizers = new Stack<>();
				finalizers.addAll(finalizerStack);

				ControlFlowNode lastNode = source;

				while (!finalizers.isEmpty()) {
					ControlFlowNode finalizerNode = new ControlFlowNode(null, graph, BranchKind.FINALLY);
					graph.addEdge(lastNode, finalizerNode);

					ControlFlowNode begin = new ControlFlowNode(null, graph, BranchKind.BLOCK_BEGIN);
					graph.addEdge(finalizerNode, begin);
					builder.setLastNode(begin);

					for (CtStatement statement : finalizers.pop().getStatements()) {
						statement.accept(builder);
					}

					ControlFlowNode end = new ControlFlowNode(null, graph, BranchKind.BLOCK_END);
					graph.addEdge(builder.getLastNode(), end);
					lastNode = end;
				}

				graph.addEdge(lastNode, builder.exitNode);
				isFinalizing = false;
			}

			return true;
		}

		return false;
	}

	/**
	 * Post-process the graph by removing all unreachable nodes.
	 *
	 * @param graph Graph to post-process
	 */
	@Override
	public void postProcess(ControlFlowGraph graph) {
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
	 * Add an edge between source and target and handle the statement in source if there is one.
	 *
	 * @param builder CFG builder
	 * @param graph   CFG
	 * @param source  Source node
	 * @param target  Target node
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
			List<ControlFlowNode> result = new ArrayList<>();

			for (List<ControlFlowNode> stackEntry : catchNodeStack) {
				for (ControlFlowNode node : stackEntry) {
					if (!result.contains(node)) {
						result.add(node);
					}
				}
			}

			return result;
		}
	}

	/**
	 * Stack of catch nodes that statements parented by a try block may jump to.
	 */
	private Stack<List<ControlFlowNode>> catchNodeStack;

	/**
	 * Stack of finalizers.
	 */
	private Stack<CtBlock<?>> finalizerStack;

	/**
	 * Flag indicating whether we are currently processing the stack of finalizers for a return statement.
	 */
	private boolean isFinalizing;
}
