package spoon.reflect.eval.observer;

import spoon.processing.Environment;
import spoon.reflect.eval.SymbolicEvaluationStep;
import spoon.reflect.eval.SymbolicEvaluator;
import spoon.reflect.eval.SymbolicEvaluatorObserver;
import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

/**
 * This symbolic evaluator observer is a printer of what happens during the
 * evaluation. It does not memorize any evaluation data.
 */
public class SymbolicEvaluationPrinter implements SymbolicEvaluatorObserver {

	private int stepNumber = 0;
	private int pathNumber = 0;
	private Environment environment;

	/**
	 * The default constructor.
	 * @param environment the environment in which the printing of the evaluation messages should be done
	 */
	public SymbolicEvaluationPrinter(Environment environment) {
		this.environment=environment;
	}
	
	public void onStartPath(SymbolicEvaluator evaluator) {
		stepNumber = 0;
		pathNumber++;
		environment.reportProgressMessage("-- start dump path " + pathNumber);
	}

	public void onExitStep(VisitorSymbolicEvaluator evaluator,
			SymbolicEvaluationStep step) {
		environment.reportProgressMessage(++stepNumber + "\t" + step.getKind() + " "
				+ step.getFrame());
		step.getHeap().dump();
	}

	public void onEndPath(SymbolicEvaluator evaluator) {
		environment.reportProgressMessage("-- end dump path " + pathNumber);
	}

	public void onEnterStep(SymbolicEvaluator evaluator,
			SymbolicEvaluationStep step) {
		environment.reportProgressMessage((++stepNumber) + "\t" + step.getKind() + " "
				+ step.getFrame());
		step.getHeap().dump();
	}

}
