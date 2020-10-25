package spoon.architecture.errorhandling;

/**
 * This interface defines an error handler for constraint violates. If an element does not satisfy a constraint {@code #printError(Object)} gets called.
 * Printing an error mustn't change the element and/or the ast.
 * <P>
 * If you want to use this for automatic code fixes, you should use the printError method for collecting the errors or fixes and apply them after the analysis.
 */
@FunctionalInterface
public interface IError<T> {

	void printError(T element);
}
