package spoon.reflect.eval.observer;

import java.util.ArrayList;
import java.util.Collection;

import spoon.reflect.eval.SymbolicEvaluationPath;
import spoon.reflect.eval.SymbolicEvaluationStep;
import spoon.reflect.eval.SymbolicEvaluator;
import spoon.reflect.eval.SymbolicEvaluatorObserver;
import spoon.support.reflect.eval.VisitorSymbolicEvaluator;

/**
 * This symbolic evaluator observer will create all the evaluation paths for a
 * given evaluator. Note that it is costful to memorize all the evaluation paths
 * and this observer should not be used on large programs (or programs which are
 * known to generate a great deal of evaluation paths.
 * <p>
 * Once the evaluation is done, call {@link #getPaths()} to get the paths.
 */
public class SymbolicEvaluationPathsMaker implements SymbolicEvaluatorObserver {

	Collection<SymbolicEvaluationPath> paths = new ArrayList<SymbolicEvaluationPath>();
	SymbolicEvaluationPath currentPath;

	/**
	 * The default constructor.
	 */
	public SymbolicEvaluationPathsMaker() {
	}

	public void onStartPath(SymbolicEvaluator evaluator) {
		currentPath = new SymbolicEvaluationPath();
		paths.add(currentPath);
	}

	public void onExitStep(VisitorSymbolicEvaluator evaluator,
			SymbolicEvaluationStep step) {
		currentPath.addStep(step);
	}

	public void onEndPath(SymbolicEvaluator evaluator) {
	}

	public void onEnterStep(SymbolicEvaluator evaluator,
			SymbolicEvaluationStep step) {
		currentPath.addStep(step);
	}

	/**
	 * Get the paths that where constructed during the evaluation of the
	 * observed evaluator.
	 * 
	 * @return a collection of symbolic evaluation paths (not a copy: it can be
	 *         cleared to reset the observer's state)
	 */
	public Collection<SymbolicEvaluationPath> getPaths() {
		return paths;
	}

}
