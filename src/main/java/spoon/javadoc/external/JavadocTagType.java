package spoon.javadoc.external;

import java.util.Collection;

public interface JavadocTagType {

	/**
	 * @return an immutable collection of applicable categories
	 */
	Collection<JavadocTagCategory> categories();

	default boolean isInline() {
		return categories().contains(JavadocTagCategory.INLINE);
	}

	default boolean isBlock() {
		return categories().contains(JavadocTagCategory.BLOCK);
	}

	String getName();
}
