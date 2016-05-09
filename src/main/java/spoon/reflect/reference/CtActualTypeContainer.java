package spoon.reflect.reference;

import java.util.List;

/**
 * This interface defines a reference to {@link spoon.reflect.declaration.CtFormalTypeDeclarer}.
 */
public interface CtActualTypeContainer {
	/**
	 * Gets the type arguments.
	 */
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Sets the type arguments.
	 */
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<CtTypeReference<?>> actualTypeArguments);

	/**
	 * Adds a type argument.
	 */
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	/**
	 * Removes a type argument.
	 */
	boolean removeActualTypeArgument(CtTypeReference<?> actualTypeArgument);
}
