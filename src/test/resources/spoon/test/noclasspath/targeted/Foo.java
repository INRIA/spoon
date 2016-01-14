
import static Bar.staticMethodBar;

public class Foo extends Unknown {
	Foo foo;

	public void inv() {
		new Foo(0, 0).staticMethod();
		foo.staticMethod();
		this.staticMethod();
		Foo.staticMethod();
		staticMethod();
		Bar.staticMethodBar();
		staticMethodBar();
		Fii.Fuu.m();
	}
}