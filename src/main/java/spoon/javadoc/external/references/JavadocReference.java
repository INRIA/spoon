package spoon.javadoc.external.references;

import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocVisitor;
import spoon.reflect.reference.CtReference;

public class JavadocReference implements JavadocElement {

	private final String raw;
	private final CtReference reference;

	public JavadocReference(String raw, CtReference reference) {
		this.raw = raw;
		this.reference = reference;
	}

	public CtReference getReference() {
		return reference;
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitReference(this);
	}

	public String getRaw() {
		return raw;
	}

	@Override
	public String toString() {
		return "JavadocReference{" +
			"raw='" + raw + '\'' +
			", reference=" + reference +
			'}';
	}
}
