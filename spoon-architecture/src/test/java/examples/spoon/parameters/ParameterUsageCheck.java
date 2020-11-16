package examples.spoon.parameters;

import java.util.Collection;
import java.util.HashSet;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.DefaultElementFilter;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class ParameterUsageCheck {

	@Architecture(modelNames = "parameters")
	public void checkParameterUsageMethods(CtModel userInput) {
		Precondition<CtMethod<?>> pre = Precondition.of(DefaultElementFilter.METHODS.getFilter());
		Constraint<CtMethod<?>> con = Constraint.of(new ExceptionError<>("Methode mit ungenutzen Parametern gefunden "), this::allParametersAreUsed);
		ArchitectureTest.of(pre, con).runCheck(userInput);
	}

	@Architecture(modelNames = "parameters")
	public void checkParameterUsageConstructor(CtModel userInput) {
		Precondition<CtConstructor<?>> pre = Precondition.of(DefaultElementFilter.CONSTRUCTORS.getFilter());
		Constraint<CtConstructor<?>> con = Constraint.of(new ExceptionError<>("Konstruktor mit ungenutzen Parametern gefunden "), this::allParametersAreUsed);
		ArchitectureTest.of(pre, con).runCheck(userInput);
	}

	private boolean allParametersAreUsed(CtExecutable<?> v) {
		Collection<CtParameterReference<?>> references = new HashSet<>(v.getElements(new TypeFilter<>(CtParameterReference.class)));
		for (CtParameter<?> parameter : v.getParameters()) {
			if (!references.contains(parameter.getReference())) {
				return false;
			}
		}
		return true;
	}
}
