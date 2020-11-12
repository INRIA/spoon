package examples.spoon.arrays;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtArrayWrite;

public class ArrayWrites {

	@Architecture(modelNames = "arrayTests")
	public void noArrayWriteUsesMinusSignInIndex(CtModel model) {
		Precondition<CtArrayWrite<?>> pre = Precondition.of(DefaultElementFilter.ARRAY_WRITES.getFilter());
		Constraint<CtArrayWrite<?>> con = Constraint.of(new ExceptionError<>("Found an arrayWrite that uses \"-\" in index expression. "),
																										v -> v.getIndexExpression().toString().contains("-"));
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
