package fr.inria.controlflow;

import spoon.reflect.code.CtTry;

public interface ExceptionControlFlowStrategy {
	void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock);

	void handleStatement(ControlFlowBuilder builder, ControlFlowNode source);
}
