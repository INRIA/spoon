import Enclosing;

public class Caller {
	public Caller() {
		Enclosing<Object, Object> e = new Enclosing<>();
		Enclosing$Nested<Object> n = new Enclosing<>(System.out);
	}
}