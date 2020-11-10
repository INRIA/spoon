package examples.spoon.anonymousexecutable;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.Exists;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtAnonymousExecutable;

public class AnonymousExecutableChecks {

	@Architecture(modelNames = "annotationTests")
	public void noAnonymousExecutableExist(CtModel model) {
		Precondition<CtAnonymousExecutable> pre = Precondition.of(DefaultElementFilter.ANONYMOUS_EXECUTABLES.getFilter());
		Constraint<CtAnonymousExecutable> con = Constraint.of(new ExceptionError<>("Found forbidden anonymous executable"), new Exists<>());
		ArchitectureTest.of(pre, con).runCheck(model);
	}

	@Architecture(modelNames = "annotationTests")
	public void allAnonymousExecutableAreEmpty(CtModel model) {
		Precondition<CtAnonymousExecutable> pre = Precondition.of(DefaultElementFilter.ANONYMOUS_EXECUTABLES.getFilter());
		Constraint<CtAnonymousExecutable> con = Constraint.of(new ExceptionError<>("Found forbidden anonymous executable"), v -> v.getBody().getStatements().isEmpty());
		ArchitectureTest.of(pre, con).runCheck(model);
	}
}
