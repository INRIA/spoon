import java.util.ArrayList;
import java.util.Collection;

public class Foo {
	public void accept(Collection y) {}

	public void doSomething(Collection x) {
		accept(new ArrayList<>(x));
	}
}
