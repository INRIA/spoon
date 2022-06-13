package spoon.javadoc.external.elements;

public interface JavadocElement {

	<T> T accept(JavadocVisitor<T> visitor);
}
