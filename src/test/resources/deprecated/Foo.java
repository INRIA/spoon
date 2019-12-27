package deprecated;

public class Foo {
	@Deprecated
	public Foo(String s) {
	}

	@Deprecated
	public void test1() {
		// test2();
	}

	@Deprecated
	public boolean test2() {
		return true;
	}

	@Deprecated
	public void test3() {
		test3();
	}

	@Deprecated
	public void test4() {

	}
}
