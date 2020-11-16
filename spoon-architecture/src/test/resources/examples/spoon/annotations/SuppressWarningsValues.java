package examples.spoon.annotations;


public class SuppressWarningsValues {

	@SuppressWarnings(value = "all")
	public void bar() {

	}
	@SuppressWarnings
	@Deprecated
	public void test() {

	}
}
