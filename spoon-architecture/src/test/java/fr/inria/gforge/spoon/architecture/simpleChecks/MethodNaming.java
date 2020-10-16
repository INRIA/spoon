package fr.inria.gforge.spoon.architecture.simpleChecks;

import fr.inria.gforge.spoon.architecture.ArchitectureTest;
import fr.inria.gforge.spoon.architecture.Constraint;
import fr.inria.gforge.spoon.architecture.DefaultElementFilter;
import fr.inria.gforge.spoon.architecture.Precondition;
import fr.inria.gforge.spoon.architecture.preconditions.Naming;
import fr.inria.gforge.spoon.architecture.preconditions.Visibility;
import fr.inria.gforge.spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;

public class MethodNaming {

	@Architecture
	public void methodNameStartsWithTest(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), Visibility.PUBLIC);
		Constraint<CtNamedElement> con = Constraint.of((element) -> System.out.println(element), Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}
}
