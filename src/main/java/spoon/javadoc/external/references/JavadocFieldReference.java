package spoon.javadoc.external.references;

import spoon.reflect.reference.CtFieldReference;

public class JavadocFieldReference implements JavadocReference {
	private final CtFieldReference<?> field;
	private final String raw;

	public JavadocFieldReference(CtFieldReference<?> field, String raw) {
		this.field = field;
		this.raw = raw;
	}

	public CtFieldReference<?> getField() {
		return field;
	}

	@Override
	public String getRaw() {
		return raw;
	}
}
