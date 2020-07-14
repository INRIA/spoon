package fr.inria.controlflow;

import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;

/**
 * Base interface for exception control flow strategies.
 */
public interface ExceptionControlFlowStrategy {
	/**
	 * Handle a try-catch-finally construct.
	 *
	 * @param builder The builder
	 * @param tryBlock A try statement
	 */
	void handleTryStatement(ControlFlowBuilder builder, CtTry tryBlock);

	/**
	 * Handle a throw statement.
	 *
	 * @param builder The builder
	 * @param throwStatement A throw statement
	 */
	void handleThrowStatement(ControlFlowBuilder builder, CtThrow throwStatement);

	/**
	 * Handle a statement node.
	 *
	 * @param builder The builder
	 * @param source Statement node
	 */
	void handleStatement(ControlFlowBuilder builder, ControlFlowNode source);

	/**
	 * Apply any post-processing to the graph.
	 *
	 * @param graph Graph to post-process
	 */
	void postProcess(ControlFlowGraph graph);
}
