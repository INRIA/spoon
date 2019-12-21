package deprecated;

public class Foo {
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
	public boolean test4() {
		return test5();
	}

	@Deprecated
	public boolean test5() {
		return test6();
	}

	public boolean test6() {
		return test4();
	}

}
