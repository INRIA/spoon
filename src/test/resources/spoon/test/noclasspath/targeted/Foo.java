
import static Bar.staticMethodBar;
import static Bar.staticFieldBar;

public class Foo extends Unknown {
	Foo foo;

	public Foo() {
		this.bar = null;
	}

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

	public void field() {
		int x = new Foo().i;
		x = foo.i;
		x = this.i;
		x = foo;
		x = Foo.staticField;
		x = staticField;
		x = Bar.staticFieldBar;
		x = staticFieldBar;
		x = Fii.Fuu.i;
	}
}