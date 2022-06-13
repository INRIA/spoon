package spoon.javadoc.external;

/**
 * The category (block or inline) a javadoc tag belongs to. A tag might be able to be used as
 * <em>both</em> (e.g. {@code @return}.
 */
public enum JavadocTagCategory {
	INLINE,
	BLOCK,
}
