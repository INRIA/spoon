package deprecated;

import java.util.List;

public class Bar {
	private boolean test = new Foo("a").test2();
	private boolean unused;
	private boolean testLambda;

	public static foo() {
		new Foo().test1();
		List.of("a").stream().forEach((v) -> new Foo(v).test4());
	}
}
