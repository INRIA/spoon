package examples.spoon.breaks;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBreak;

public class NoBreaksWithLabels {

	@Architecture(modelNames = "testmodel")
	public void noBreaksWithLabels(CtModel model) {
		Precondition<CtBreak> pre = Precondition.of(DefaultElementFilter.BREAKS.getFilter());
		Constraint<CtBreak> con = Constraint.of(new ExceptionError<>("Found a break with label. "),
		v -> v.getLabel() == null);
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
