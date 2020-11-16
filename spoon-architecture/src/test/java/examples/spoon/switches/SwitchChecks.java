package examples.spoon.switches;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtSwitch;

public class SwitchChecks {

	@Architecture(modelNames = "switches")
	public void everySwitchHasDefault(CtModel model) {
		Precondition<CtSwitch<?>> pre = Precondition.of(DefaultElementFilter.SWITCHES.getFilter());
		Constraint<CtSwitch<?>> con = Constraint.of(new ExceptionError<>("Found a switch without default  "),
		v -> v.getCases().stream().anyMatch(caseExpression -> hasAnyDefaultExpression(caseExpression)));
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	private boolean hasAnyDefaultExpression(CtCase<?> ctCase) {
		if (ctCase.getCaseExpressions().isEmpty()) {
			return isDefaultCase(ctCase.getCaseExpression());
		}
		return ctCase.getCaseExpressions().stream().anyMatch(caseStatement -> isDefaultCase(caseStatement));
	}

	private boolean isDefaultCase(CtExpression<?> caseStatement) {
		// default cases have a null caseStatement
		return caseStatement == null;
	}

	@Architecture(modelNames = "switches")
	public void noMultipleCaseStatements(CtModel model) {
		Precondition<CtCase<?>> pre = Precondition.of(DefaultElementFilter.CASES.getFilter());
		Constraint<CtCase<?>> con = Constraint.of(new ExceptionError<>("Found a case with multiple case expressions "),
		v -> v.getCaseExpressions().size() < 2);
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
