package spoon.architecture.simpleChecks;

import org.junit.Test;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.preconditions.Annotations;
import spoon.architecture.preconditions.Naming;
import spoon.architecture.preconditions.VisibilityFilter;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;

public class MethodNaming {

	@Architecture
	public void methodNameStartsWithTest(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), VisibilityFilter.isPublic(), Annotations.hasAnnotation(Test.class));
		Constraint<CtMethod<?>> con = Constraint.of(System.out::println, Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}
}
