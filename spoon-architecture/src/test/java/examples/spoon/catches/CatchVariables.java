package examples.spoon.catches;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatchVariable;

public class CatchVariables {

	@Architecture(modelNames = "catchvariables")
	public void noMultiTypesInCatches(CtModel model) {
		Precondition<CtCatchVariable<?>> pre = Precondition.of(DefaultElementFilter.CATCH_VARIABLES.getFilter());
		Constraint<CtCatchVariable<?>> con = Constraint.of(new ExceptionError<>("Found a catch with multitype. "),
		v -> v.getMultiTypes().size() == 1);
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	@Architecture(modelNames = "catchvariables")
	public void noThrowableCatch(CtModel model) {
		Precondition<CtCatchVariable<?>> pre = Precondition.of(DefaultElementFilter.CATCH_VARIABLES.getFilter(), v -> v.getMultiTypes().size() == 1);
		Constraint<CtCatchVariable<?>> con = Constraint.of(new ExceptionError<>("Found a catch for throwable. "),
		v -> !v.getType().getTypeDeclaration().getSimpleName().equals("Throwable"));
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
