package spoon.reflect.code;

/**
 * This code element defines an read access to a field.
 *
 * In Java, it is a usage of a field outside an assignment. For example,
 * <code>System.out.println(this.field);</code>
 *
 * @param <T>
 * 		type of the field
 */
public interface CtFieldRead<T> extends CtFieldAccess<T> {
}
