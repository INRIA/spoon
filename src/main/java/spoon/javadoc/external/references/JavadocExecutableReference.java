package spoon.javadoc.external.references;

import spoon.reflect.reference.CtExecutableReference;

public class JavadocExecutableReference implements JavadocReference {
	private final CtExecutableReference<?> executable;
	private final String raw;

	public JavadocExecutableReference(CtExecutableReference<?> executable, String raw) {
		this.executable = executable;
		this.raw = raw;
	}

	public CtExecutableReference<?> getExecutable() {
		return executable;
	}

	@Override
	public String getRaw() {
		return raw;
	}
}
