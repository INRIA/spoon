package fr.inria.controlflow;

import spoon.reflect.code.CtTry;

public interface ExceptionControlFlowStrategy {
	/**
	 * Handle a try-catch-finally construct.
	 *
	 * @param builder The builder
	 * @param tryBlock A try statement
	 */
	void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock);

	/**
	 * Handle a statement node.
	 *
	 * @param builder The builder
	 * @param source Statement node
	 * @return True if the builder should abort processing the node, false otherwise
	 */
	boolean handleStatement(ControlFlowBuilder builder, ControlFlowNode source);

	/**
	 * Apply any post-processing to the graph.
	 *
	 * @param graph Graph to post-process
	 */
	void postProcess(ControlFlowGraph graph);
}
