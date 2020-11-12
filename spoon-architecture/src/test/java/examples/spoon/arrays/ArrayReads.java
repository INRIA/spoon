package examples.spoon.arrays;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtArrayRead;

public class ArrayReads {

	@Architecture(modelNames = "arrayTests")
	public void noIntegerArrays(CtModel model) {
		Precondition<CtArrayRead<?>> pre = Precondition.of(DefaultElementFilter.ARRAY_READS.getFilter());
		Constraint<CtArrayRead<?>> con = Constraint.of(new ExceptionError<>("Found an arrayRead to Integer[]. Use int[]. "),
																										v -> !v.getType().getSimpleName().equals("Integer"));
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
