package examples.spoon.annotations;


public class SuppressWarningsValues {

	@SuppressWarnings(value = "all")
	public void bar() {

	}

	@Deprecated(forRemoval = true)
	public void test() {

	}
}
