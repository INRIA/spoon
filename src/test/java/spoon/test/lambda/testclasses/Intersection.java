package spoon.test.lambda.testclasses;

import java.util.Collections;
import java.util.List;

public class Intersection<T> {

	public void m(){
		multiInheritedGenericsList().stream().filter(elt -> elt.test());
	}

	public static <T extends A & B> List<T> multiInheritedGenericsList() {
		return Collections.emptyList();
	}

	public interface A {
		boolean test() throws Exception;
	}

	public interface B {
		boolean test();
	}
}
