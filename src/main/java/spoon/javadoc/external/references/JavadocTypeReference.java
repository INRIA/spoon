package spoon.javadoc.external.references;

import spoon.reflect.reference.CtTypeReference;

public class JavadocTypeReference implements JavadocReference {
	private final CtTypeReference<?> type;
	private final String raw;

	public JavadocTypeReference(CtTypeReference<?> type, String raw) {
		this.type = type;
		this.raw = raw;
	}

	public CtTypeReference<?> getType() {
		return type;
	}

	@Override
	public String getRaw() {
		return raw;
	}
}
