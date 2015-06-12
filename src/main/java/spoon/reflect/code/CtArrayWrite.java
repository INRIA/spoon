package spoon.reflect.code;

/**
 * This code element defines an write access to an array.
 *
 * In Java, it is a usage of a array inside an assignment. For example,
 * <code>array[0] = "new value";</code>
 *
 * @param <T>
 * 		type of the array
 */
public interface CtArrayWrite<T> extends CtArrayAccess<T, CtExpression<?>> {
}
