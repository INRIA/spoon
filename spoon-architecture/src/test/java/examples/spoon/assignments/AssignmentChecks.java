package examples.spoon.assignments;

import java.util.function.Predicate;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;

public class AssignmentChecks {

	@Architecture(modelNames = "assignments")
	public void noTypeCastsInAssignments(CtModel model) {
		Precondition<CtAssignment<?, ?>> pre = Precondition.of(DefaultElementFilter.ASSIGNMENTS.getFilter());
		Constraint<CtAssignment<?, ?>> con = Constraint.of(new ExceptionError<>("Found a type cast in an assignment. "),
		v -> v.getAssignment().getTypeCasts().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	@Architecture(modelNames = "assignments")
	public void noSelfAssignment(CtModel model) {
		Precondition<CtAssignment<?, ?>> pre = Precondition.of(DefaultElementFilter.ASSIGNMENTS.getFilter());
		Constraint<CtAssignment<?, ?>> con = Constraint.of(new ExceptionError<>("Found an self assignment."),
		checkForSelfAssignment());
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	private Predicate<CtAssignment<?, ?>> checkForSelfAssignment() {
		return v -> !v.getAssigned().equals(v.getAssignment());
	}
}
