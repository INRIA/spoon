package spoon.javadoc.external.elements;

/**
 * Normal text appearing in a javadoc comment.
 */
public class JavadocText implements JavadocElement {
	private final String text;

	/**
	 * @param text the represented text
	 */
	public JavadocText(String text) {
		this.text = text;
	}

	/**
	 * @return the represented text
	 */
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
