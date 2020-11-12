package examples.spoon.asserts;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssert;

public class NoAsserts {


	@Architecture(modelNames = "arrayTests")
	public void noJavaAssertsInTestCode(CtModel model) {
		Precondition<CtAssert<?>> pre = Precondition.of(DefaultElementFilter.ASSERTS.getFilter());
		Constraint<CtAssert<?>> con = Constraint.of(new ExceptionError<>("Found an assert in test code. Use junit methods for it instead."), new Exists<>());
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
