package spoon.architecture.simpleChecks;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.FieldReferenceMatcher;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.preconditions.Naming;
import spoon.architecture.preconditions.VisibilityFilter;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtField;

public class FieldReferenceMatcherTest {

	@Architecture(modelNames = "fieldReferenceMatcher")
	public void checkFields(CtModel srcModel) {
		FieldReferenceMatcher matcher = new FieldReferenceMatcher(srcModel);
		Precondition<CtField<?>> pre = Precondition.of(DefaultElementFilter.FIELDS.getFilter(),
		VisibilityFilter.isPrivate(),
		Naming.equals("serialVersionUID ").negate());
		Constraint<CtField<?>> con = Constraint.of(new ExceptionError<CtField<?>>(), matcher);
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}
}
