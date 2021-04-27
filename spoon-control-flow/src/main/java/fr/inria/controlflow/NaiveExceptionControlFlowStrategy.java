/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.inria.controlflow;

import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A naive over-approximating model of exception control flow with limited support for finalizers.
 *
 * The model uses the following assumptions:
 *
 *   1) Any statement can potentially throw any exception.
 *   2) All exceptions thrown inside a try block are caught by the catchers immediately associated with the block.
 *
 * Support for finalizers is limited by the lack of modeling for the semantics of return statements in regards to
 * executing finalizers before actually returning. Because of this limitation, by default the implementation will
 * refuse to model the flow of a try-(catch-)finally construct that contains return statements. An option is
 * available to allow the model to produce a partially incorrect graph where return statements jump directly to the
 * exit without executing finalizers.
 */
public class NaiveExceptionControlFlowStrategy implements ExceptionControlFlowStrategy {
	/**
	 * Per-instance option flags for NaiveExceptionControlFlowStrategy.
	 */
	public enum Options {
		/**
		 * Add paths between the end of an empty try {} block and its catchers.
		 *
		 * Default: disabled.
		 *
		 * This option exists because expressions of the form "try { } catch(Exception e) { foo(); }" (i.e empty try
		 * blocks) are legal in Java, despite the statement "foo()" trivially being unreachable. In some use cases,
		 * excluding such unreachable statements from the control flow graph may be desirable, while in other cases the
		 * information loss may be undesirable. The default choice of not adding these paths was chosen due to how the
		 * produced graph more accurately models the actual control flow of an execution. Enabling the option produces
		 * a graph that can be said to show what the Java compiler considers to be reachable code.
		 */
		AddPathsForEmptyTryBlocks,

		/**
		 * Model (incorrectly) return statements as jumping directly to the exit node without executing any "in-scope"
		 * finalizers.
		 *
		 * Default: disabled.
		 *
		 * This option exists to provide a limited form of support for return statements in try-(catch-)finally
		 * constructs despite the lack of complete modeling for the semantics of return statements when finalizers are
		 * present. Depending on the use case, the incorrect aspects of the produced graph may be an acceptable
		 * tradeoff versus having return statements be completely unsupported when finalizers are used.
		 */
		ReturnWithoutFinalizers
	}

	/**
	 * Create a new NaiveExceptionControlFlowStrategy using the default set of options.
	 */
	public NaiveExceptionControlFlowStrategy() {
		this(EnumSet.noneOf(Options.class));
	}

	/**
	 * Create a new NaiveExceptionControlFlowStrategy using the given set of options.
	 *
	 * @param options Options to use
	 */
	public NaiveExceptionControlFlowStrategy(EnumSet<Options> options) {
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
		if (!instanceOptions.contains(Options.ReturnWithoutFinalizers) && tryBlock.getFinalizer() != null) {
			for (CtElement element : tryBlock.asIterable()) {
				if (element instanceof CtReturn) {
					throw new IllegalArgumentException("return statements in try-(catch-)finally constructs are not supported");
				}
			}
		}

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
		}

		graph.addEdge(lastNode, tryNode);
		builder.setLastNode(tryNode);

		catchNodeStack.push(catchNodes);

		tryBlock.getBody().accept(builder);

		catchNodeStack.pop();

		graph.addEdge(builder.getLastNode(), finallyNode != null ? finallyNode : convergeNode);

		for (ControlFlowNode catchNode : catchNodes) {
			if (tryBlock.getBody().getStatements().size() == 0 && instanceOptions.contains(Options.AddPathsForEmptyTryBlocks)) {
				graph.addEdge(tryNode.next().get(0).next().get(0), catchNode);
			}

			builder.setLastNode(catchNode);
			((CtCatch) catchNode.getStatement().getParent()).getBody().accept(builder);
			lastNode = builder.getLastNode();
			graph.addEdge(lastNode, finallyNode != null ? finallyNode : convergeNode);
		}

		builder.setLastNode(finallyNode != null ? finallyNode : convergeNode);

		if (finallyNode != null) {
			tryBlock.getFinalizer().accept(builder);
			graph.addEdge(builder.getLastNode(), convergeNode);
			builder.setLastNode(convergeNode);
		}
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
		removeUnreachableFinalizerNodeBlockEndPredecessors(graph);
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

	private void removeUnreachableFinalizerNodeBlockEndPredecessors(ControlFlowGraph graph) {
		graph.findNodesOfKind(BranchKind.FINALLY).forEach(node -> {
			node.prev().stream().filter(prevNode -> prevNode.prev().size() == 0).forEach(graph::removeVertex);
		});
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
