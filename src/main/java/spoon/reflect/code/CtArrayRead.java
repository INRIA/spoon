package spoon.reflect.code;

/**
 * This code element defines an read access to an array.
 *
 * In Java, it is a usage of a array outside an assignment. For example,
 * <code>System.out.println(array[0]);</code>
 *
 * @param <T>
 * 		type of the array
 */
public interface CtArrayRead<T> extends CtArrayAccess<T, CtExpression<?>> {
}
