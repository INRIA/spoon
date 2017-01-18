public class UnknownSuperClass extends NotInClasspath {

	public String a() {
		return "This method doesn't override anything";
	}

	@Override
	public String b() {
		return "This method overrides NotInClasspath#b";
	}

	@Override
	public String toString() {
		return "This method overrides Object#toString()";
	}
}
