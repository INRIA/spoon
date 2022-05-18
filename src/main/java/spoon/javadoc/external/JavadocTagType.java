package spoon.javadoc.external;

import java.util.Collection;
import java.util.List;

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

	static JavadocTagType unknown(String name, JavadocTagCategory category) {
		return new JavadocTagType() {
			@Override
			public Collection<JavadocTagCategory> categories() {
				return List.of(category);
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}
}
