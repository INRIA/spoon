package records;

public record NonCompactCanonicalConstructor(int i) {

	public NonCompactCanonicalConstructor(int x) {
		this.i = x;
	}

}
