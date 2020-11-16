package examples.spoon.switches;

import java.util.List;
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
import spoon.reflect.visitor.filter.TypeFilter;

public class SwitchChecks {

	@Architecture(modelNames = "switches")
	public void everySwitchHasDefault(CtModel model) {
		Precondition<CtSwitch<?>> pre = Precondition.of(DefaultElementFilter.SWITCHES.getFilter());
		List<CtCase<?>> adsda = model.getElements(new TypeFilter(CtCase.class));
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
}
