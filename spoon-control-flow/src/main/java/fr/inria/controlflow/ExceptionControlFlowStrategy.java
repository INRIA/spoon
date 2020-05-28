package fr.inria.controlflow;

import spoon.reflect.code.CtTry;

import java.util.Map;

public interface ExceptionControlFlowStrategy {
    void handleTryBlock(ControlFlowBuilder builder, CtTry tryBlock);
    void handleStatement(ControlFlowBuilder builder, ControlFlowNode source);
}
