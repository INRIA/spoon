package records;

public record MultipleConstructors(int i) {

	public MultipleConstructors(String s) {
		this(Integer.parseInt(s));
	}
}
