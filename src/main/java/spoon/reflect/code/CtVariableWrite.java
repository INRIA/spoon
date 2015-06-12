package spoon.reflect.code;

/**
 * This code element defines a write to a variable.
 *
 * In Java, it is a usage of a variable inside an assignment. For example,
 * <code>variable = "new value";</code>
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtVariableWrite<T> extends CtVariableAccess<T> {
}
