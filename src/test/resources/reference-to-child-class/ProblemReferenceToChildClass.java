import java.util.ArrayList;
import java.util.List;

class ProblemReferenceToChildClass implements Foo<ProblemReferenceToChildClass.Bar<?>> {
	private final List<Bar<?>> bars = new ArrayList<>();

	private static final class Bar<T> {
	}
}
