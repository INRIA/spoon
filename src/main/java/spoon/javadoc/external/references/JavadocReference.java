package spoon.javadoc.external.references;

import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocVisitor;
import spoon.reflect.reference.CtReference;

public class JavadocReference implements JavadocElement {

	private final CtReference reference;

	public JavadocReference(CtReference reference) {
		this.reference = reference;
	}

	public CtReference getReference() {
		return reference;
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitReference(this);
	}

	@Override
	public String toString() {
		return "JavadocReference{"
			+ ", reference=" + reference
			+ '}';
	}
}
