package spoon.architecture.simpleChecks;

import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.constraints.InvocationMatcher;
import spoon.architecture.preconditions.VisibilityFilter;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;

public class MethodInvocation {

	@Architecture
	public void methodInvocationLookUp(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), VisibilityFilter.isPrivate());
		InvocationMatcher matcher = new InvocationMatcher(srcModel);
		Constraint<CtMethod<?>> con = Constraint.of((element) -> System.out.println("element has no invocation: " + element), (element) -> matcher.test(element));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}
}
