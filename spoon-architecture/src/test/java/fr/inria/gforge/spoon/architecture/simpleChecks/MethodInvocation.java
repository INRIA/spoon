package fr.inria.gforge.spoon.architecture.simpleChecks;

import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.constraints.InvocationMatcher;
import fr.inria.gforge.spoon.architecture.preconditions.Visibility;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;

public class MethodInvocation {

	@Architecture
	public void methodInvocationLookUp(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), Visibility.PRIVATE);
		InvocationMatcher matcher = new InvocationMatcher(srcModel);
		Constraint<CtMethod<?>> con = Constraint.of((element) -> System.out.println("element has no invocation: " + element), (element) -> matcher.test(element));
		ArchitectureTest.of(pre, con).runCheck(srcModel);
	}
}
