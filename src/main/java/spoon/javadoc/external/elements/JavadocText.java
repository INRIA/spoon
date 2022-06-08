package spoon.javadoc.external.elements;

public class JavadocText implements JavadocElement {
	private final String rawFragment;

	public JavadocText(String rawFragment) {
		this.rawFragment = rawFragment;
	}

	public String getRawFragment() {
		return rawFragment;
	}

	@Override
	public void accept(JavadocVisitor visitor) {
		visitor.visitText(this);
	}

	@Override
	public String toString() {
		return "JavadocText{" +
			"rawFragment='" + rawFragment + '\'' +
			'}';
	}
}
