package spoon.javadoc.external;

import java.util.Collection;
import java.util.Set;

import static spoon.javadoc.external.JavadocTagCategory.BLOCK;
import static spoon.javadoc.external.JavadocTagCategory.INLINE;

public enum StandardJavadocTagType implements JavadocTagType {
	AUTHOR("author", BLOCK),
	CODE("code", INLINE),
	DEPRECATED("deprecated", BLOCK),
	DOC_ROOT("docRoot", INLINE),
	EXCEPTION("exception", BLOCK),
	HIDDEN("hidden", BLOCK),
	INDEX("index", INLINE),
	INHERIT_DOC("inheritDoc", INLINE),
	LINK("link", INLINE),
	LINKPLAIN("linkplain", INLINE),
	LITERAL("literal", INLINE),
	PARAM("param", BLOCK),
	PROVIDES("provides", BLOCK),
	RETURN("return", INLINE, BLOCK),
	SEE("see", BLOCK),
	SERIAL("serial", BLOCK),
	SERIAL_DATA("serialData", BLOCK),
	SERIAL_FIELD("serialField", BLOCK),
	SINCE("since", BLOCK),
	SNIPPET("snippet", INLINE),
	SUMMARY("summary", BLOCK),
	SYSTEM_PROPERTY("systemProperty", INLINE),
	THROWS("throws", BLOCK),
	USES("uses", BLOCK),
	VALUE("value", INLINE),
	VERSION("version", BLOCK);

	private final String name;
	private final Set<JavadocTagCategory> categories;

	StandardJavadocTagType(String name, JavadocTagCategory... categories) {
		this.name = name;
		this.categories = Set.of(categories);
	}

	@Override
	public Collection<JavadocTagCategory> categories() {
		return categories;
	}

	@Override
	public String getName() {
		return name;
	}
}
