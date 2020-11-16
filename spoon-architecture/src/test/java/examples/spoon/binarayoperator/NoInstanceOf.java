package examples.spoon.binarayoperator;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

public class NoInstanceOf {

	@Architecture(modelNames = "binaryoperators")
	public void noBreaksWithLabels(CtModel model) {
		Precondition<CtBinaryOperator<?>> pre = Precondition.of(DefaultElementFilter.BINARY_OPERATORS.getFilter(),
		v -> v.getKind().equals(BinaryOperatorKind.INSTANCEOF));
		Constraint<CtBinaryOperator<?>> con = Constraint.of(new ExceptionError<>("Found an instanceof usage "), new Exists<>());
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
