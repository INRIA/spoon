package examples.spoon.catches;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtCatchVariableReference;

public class CatchVariableReferences {

	@Architecture(modelNames = "catchvariables")
	public void noMultiTypesInCatches(CtModel model) {
		Precondition<CtCatchVariableReference<?>> pre = Precondition.of(DefaultElementFilter.CATCH_VARIABLES_REFERENCES.getFilter());
		Constraint<CtCatchVariableReference<?>> con = Constraint.of(new ExceptionError<>("Found a catch with multitype. "),
		v -> v.getDeclaration().getMultiTypes().size() == 1);
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
