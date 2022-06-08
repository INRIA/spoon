package spoon.javadoc.external.elements;

public interface JavadocElement {

	void accept(JavadocVisitor visitor);
}
