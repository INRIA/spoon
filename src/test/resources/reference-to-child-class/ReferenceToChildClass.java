import java.util.ArrayList;
import java.util.List;

class ReferenceToChildClass implements Foo<ReferenceToChildClass.Bar<?>> {
	private final List<Bar<?>> bars = new ArrayList<>();

	static final class Bar<T> {
	}
}
