import static java.lang.System.lineSeparator;
public class Test {
	public void outerMethod() {}
	public static void staticOuterMethod() {}

	public class Inner {
		void entrypoint() {
			entrypoint();
			outerMethod();
			staticOuterMethod();
			lineSeparator();
		}
	}
}
