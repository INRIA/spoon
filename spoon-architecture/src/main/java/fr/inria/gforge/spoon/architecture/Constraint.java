package fr.inria.gforge.spoon.architecture;

import java.util.function.Predicate;
import fr.inria.gforge.spoon.architecture.errorhandling.IError;
import spoon.reflect.declaration.CtElement;

public class Constraint<T> implements IConstraint<T> {
	private Predicate<T> condition;
	private IError<T> errorHandler;

	private Constraint(IError<T> errorHandler, Predicate<T> condition) {
		this.errorHandler = errorHandler;
		this.condition = condition;
	}

	public static <T extends CtElement> Constraint<T> of(IError<T> errorHandler,
			Predicate<T>... conditions) {
		Predicate<T> startValue = (value) -> true;
		for (Predicate<T> condition : conditions) {
			startValue = startValue.and(condition);
		}
		return new Constraint<T>(errorHandler, startValue);
	}

	public static <T extends CtElement> Constraint<T> of(IError<T> errorHandler,
			Predicate<T> condition) {
		return new Constraint<T>(errorHandler, condition);
	}


	@Override
	public void checkConstraint(T element) {
		if (condition.negate().test(element)) {
			errorHandler.printError(element);
		}
	}


}
