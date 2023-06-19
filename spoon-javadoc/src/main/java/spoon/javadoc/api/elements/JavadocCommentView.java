package spoon.javadoc.api.elements;

import java.util.List;
import java.util.stream.Collectors;
import spoon.javadoc.api.JavadocTagType;

/**
 * A slightly more expressive view of a {@code List<JavadocElement>}.
 */
public class JavadocCommentView {

	private final List<JavadocElement> elements;

	public JavadocCommentView(List<JavadocElement> elements) {
		this.elements = List.copyOf(elements);
	}

	public List<JavadocElement> getElements() {
		return elements;
	}

	public List<JavadocBlockTag> getBlockTags() {
		return elements.stream()
			.filter(it -> it instanceof JavadocBlockTag)
			.map(it -> (JavadocBlockTag) it)
			.collect(Collectors.toList());
	}

	public List<JavadocBlockTag> getBlockTag(JavadocTagType type) {
		return getBlockTags().stream()
			.filter(it -> type.equals(it.getTagType()))
			.collect(Collectors.toList());
	}

	public <T extends JavadocElement> List<T> getBlockTagArguments(
		JavadocTagType type, Class<T> expectedArgumentClass
	) {
		return getBlockTag(type).stream()
			.flatMap(it -> it.getArgument(expectedArgumentClass).stream())
			.collect(Collectors.toList());
	}

	public List<JavadocElement> getBody() {
		return elements.stream()
			.filter(it -> !(it instanceof JavadocBlockTag))
			.collect(Collectors.toList());
	}
}
