package fr.inria.gforge.spoon.architecture;

import java.util.function.Predicate;
import fr.inria.gforge.spoon.architecture.errorhandling.IError;
import spoon.reflect.declaration.CtElement;

/** 
 * Constraint defines the checkable part of a architecture rule. A constraint is a conjunction of predicates.
 * A constraint is violated if, the element does not eval all predicates to true. The predicates evaluation is circuited. 
 * If A is not true then B will not be evaluated. The rule will be negated for the check. It's checked that no element doesn't hold the condition.
 * <p>
 * This class defines multiple static methods for creation of constraints see {@link #of(IError,Predicate...)}, 
 * {@link #of(IError,Iterable)} and {@link #of(IError,Predicate)}. 
 * The {@link IError} is called if a element evaluates to false. Use this to report violations. 
 * 
 * 
 */
public class Constraint<T extends CtElement> implements Checkable<T> {
	private Predicate<T> condition;
	private IError<T> errorHandler;

	private Constraint(IError<T> errorHandler, Predicate<T> condition) {
		this.errorHandler = errorHandler;
		this.condition = condition;
	}

/**
 * Creates a constraint of a varargs of predicates.
 * @param <T> type of ast model element checked
 * @param errorHandler the handler for errors. Called if an element violates the conditions.
 * @param conditions the conditions that the element is checked against.
 * @return a constraint a conjunction of your predicate as check.
 */
	public static <T extends CtElement> Constraint<T> of(IError<T> errorHandler,
			Predicate<T>... conditions) {
		Predicate<T> startValue = (value) -> true;
		for (Predicate<T> condition : conditions) {
			startValue = startValue.and(condition);
		}
		return new Constraint<T>(errorHandler, startValue);
	}
/**
 * Creates a constraint of an iterable of predicates.
 * @param <T> type of ast model element checked
 * @param errorHandler the handler for errors. Called if an element violates the conditions.
 * @param conditions the conditions that the element is checked against.
 * @return a constraint a conjunction of your predicate as check.
 */
	public static <T extends CtElement> Constraint<T> of(IError<T> errorHandler,
			Iterable<Predicate<T>> conditions) {
		Predicate<T> startValue = (value) -> true;
		for (Predicate<T> condition : conditions) {
			startValue = startValue.and(condition);
		}
		return new Constraint<T>(errorHandler, startValue);
	}
/**
 * Creates a constraint of a single predicate.
 * @param <T> type of ast model element checked
 * @param errorHandler the handler for errors. Called if an element violates the conditions.
 * @param conditions the conditions that the element is checked against.
 * @return a constraint with your predicate as check.
 */
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
