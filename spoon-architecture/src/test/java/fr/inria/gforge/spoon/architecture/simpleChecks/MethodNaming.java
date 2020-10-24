package fr.inria.gforge.spoon.architecture.simpleChecks;

import org.junit.Test;
import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.preconditions.AnnotationHelper;
import fr.inria.gforge.spoon.architecture.preconditions.Naming;
import fr.inria.gforge.spoon.architecture.preconditions.VisibilityFilter;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;

public class MethodNaming {

	@Architecture
	public void methodNameStartsWithTest(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), VisibilityFilter.isPublic(), AnnotationHelper.hasAnnotationMatcher(Test.class));
		Constraint<CtMethod<?>> con = Constraint.of(System.out::println, Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}
}
