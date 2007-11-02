package spoon.reflect.eval;

import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

/**
 * This interface defines the basic events that can be received by a symbolic
 * evaluator observer (during the evaluation).
 */
public interface SymbolicEvaluatorObserver {

	/**
	 * The observed evaluator is starting an evaluation path.
	 * 
	 * @param evaluator
	 *            the observed evaluator
	 */
	void onStartPath(SymbolicEvaluator evaluator);

	/**
	 * The observed evaluator is ending an evaluation path.
	 * 
	 * @param evaluator
	 *            the observed evaluator
	 */
	void onEndPath(SymbolicEvaluator evaluator);

	/**
	 * The observed evaluator is entering an evaluation step.
	 * 
	 * @param evaluator
	 *            the observed evaluator
	 * @param step
	 *            the evaluation step which is currently entered
	 */
	void onEnterStep(SymbolicEvaluator evaluator, SymbolicEvaluationStep step);

	/**
	 * The observed evaluator is leaving an evaluation step.
	 * 
	 * @param evaluator
	 *            the observed evaluator
	 * @param step
	 *            the evaluation step which is currently entered
	 */
	void onExitStep(VisitorSymbolicEvaluator evaluator,
			SymbolicEvaluationStep step);
}
