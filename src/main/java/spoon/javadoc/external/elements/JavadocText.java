package spoon.javadoc.external.elements;

public class JavadocText implements JavadocElement {
	private final String text;

	public JavadocText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitText(this);
	}

	@Override
	public String toString() {
		return "JavadocText{"
			+ "text='" + text + '\''
			+ '}';
	}
}
