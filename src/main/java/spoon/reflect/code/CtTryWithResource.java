package spoon.reflect.code;

import java.util.List;

/**
 * This code element defines a <code>try</code> with resource statement.
 */
public interface CtTryWithResource extends CtTry {

	/**
	 * Gets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 */
	List<CtLocalVariable<?>> getResources();

	/**
	 * Sets the auto-closeable resources of this <code>try</code>. Available
	 * from Java 7 with the <i>try-with-resource</i> statement.
	 */
	<T extends CtTryWithResource> T setResources(List<CtLocalVariable<?>> resources);

	/**
	 * Adds a resource.
	 */
	<T extends CtTryWithResource> T addResource(CtLocalVariable<?> resource);

	/**
	 * Removes a resource.
	 */
	boolean removeResource(CtLocalVariable<?> resource);
}
