package spoon.reflect.code;

/**
 * This code element defines a write to a field.
 *
 * In Java, it is a usage of a field inside an assignment. For example,
 * <code>this.field = "new value";</code>
 *
 * @param <T>
 * 		type of the field
 */
public interface CtFieldWrite<T> extends CtFieldAccess<T> {
}
