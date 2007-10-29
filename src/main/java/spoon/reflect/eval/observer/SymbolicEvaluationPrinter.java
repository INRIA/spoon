package spoon.reflect.eval.observer;

import spoon.reflect.eval.SymbolicEvaluatorObserver;
import spoon.reflect.eval.SymbolicEvaluationStep;
import spoon.reflect.eval.SymbolicEvaluator;
import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

public class SymbolicEvaluationPrinter implements SymbolicEvaluatorObserver {
	
	private int stepNumber = 0;
	private int pathNumber = 0;
	
	public void onStartPath(SymbolicEvaluator evaluator) {
		stepNumber = 0;
		pathNumber++;
		System.out.println("-- start dump path " + pathNumber);
	}
	
	public void onExitStep(VisitorSymbolicEvaluator evaluator, SymbolicEvaluationStep step) {
		System.out.println(++stepNumber + "\t" + step.getKind() + " "
				+ step.getFrame());
		step.getHeap().dump();
	}

	public void onEndPath(SymbolicEvaluator evaluator) {
		System.out.println("-- end dump path " + pathNumber);

	}

	public void onEnterStep(SymbolicEvaluator evaluator, SymbolicEvaluationStep step) {
		System.out.println((++stepNumber) + "\t" + step.getKind() + " "
				+ step.getFrame());
		step.getHeap().dump();
	}

}
