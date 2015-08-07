package spoon.reflect.reference;

import spoon.reflect.declaration.CtAnnotation;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This reference defines an annotation, which can be declare on all types.
 *
 * <code>
 *     public class Tacos implements @TypeAnnotation ITacos extends @TypeAnnotation AbstractTacos {
 *         public @TypeAnnotation String m() throws @TypeAnnotation Exception {
 *         		Object s = new @TypeAnnotation String();
 *				return (@TypeAnnotation String) s;
 *         }
 *     }
 * </code>
 */
public interface CtTypeAnnotableReference {

	/**
	 * Returns the type annotations that are present on this type.
	 *
	 * For sake of encapsulation, the returned list is unmodifiable.
	 */
	List<CtAnnotation<? extends Annotation>> getTypeAnnotations();

	/**
	 * Sets the type annotations for this type.
	 */
	<T extends CtTypeAnnotableReference> T setTypeAnnotations(List<CtAnnotation<? extends Annotation>> annotations);

	/**
	 * Adds an type annotation for this type.
	 *
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	<T extends CtTypeAnnotableReference> T addTypeAnnotation(CtAnnotation<? extends Annotation> annotation);

	/**
	 * Removes an type annotation for this type.
	 *
	 * @param annotation
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	boolean removeTypeAnnotation(CtAnnotation<? extends Annotation> annotation);
}
