package deprecated;

public class Foo {
	// ref in Bar.test and Bar.foo()
	@Deprecated
	public Foo(String s) {
	}

	// ref in Bar.foo()
	@Deprecated
	public void test4() {
	}

	// ref in Bar.test
	@Deprecated
	public void test5() {
	}
}
