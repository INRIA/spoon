package examples.spoon.arrays;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtArrayTypeReference;

public class ArrayTypeReferences {

	@Architecture(modelNames = "arrayTests")
	public void noArrayHasMoreThen2Dimensions(CtModel model) {
		Precondition<CtArrayTypeReference<?>> pre = Precondition.of(DefaultElementFilter.ARRAY_TYPE_REFERENCES.getFilter());
		Constraint<CtArrayTypeReference<?>> con = Constraint.of(new ExceptionError<>("Found an array with more then 2 dimensions. "),
																										v -> v.getDimensionCount() <= 2);
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
