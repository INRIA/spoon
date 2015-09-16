public class Foo {
	public void m() {
		new Bar().m();
		final Bar bar = new Bar("");
		final Tacos t = bar.m(1);
		new Bar().m(1, "5");
		Bar.m("42")
	}
}