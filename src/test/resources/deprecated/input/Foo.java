package deprecated;

public class Foo {
	// ref in Bar.test and Bar.foo()
	@Deprecated
	public Foo(String s) {
	}

	// no ref should be deleted
	@Deprecated
	public void test1() {
		test2();
	}

	// ref in Foo.test1()
	@Deprecated
	public boolean test2() {
		return true;
	}

	// should be deleted because only ref in Foo.test3()
	@Deprecated
	public void test3() {
		test3();
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
