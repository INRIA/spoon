package spoon.reflect.eval;

import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

public interface SymbolicEvaluatorObserver {
	void onStartPath(SymbolicEvaluator evaluator);
	
	void onEndPath(SymbolicEvaluator evaluator);

	void onEnterStep(SymbolicEvaluator evaluator, SymbolicEvaluationStep step);

	void onExitStep(VisitorSymbolicEvaluator evaluator, SymbolicEvaluationStep step);
}
