package spoon.test.lambda.testclasses;

import java.util.Collections;
import java.util.List;

public class Intersection<T> {

	public void m(){
		multiInheritedGenericsList().stream().filter(elt -> elt.test());
	}
	
	public void m2(){
		D f = (C & D)(() -> System.out.println());
		f.test();
	}

	public void m3(){
		D f = (D & C)(() -> System.out.println());
		f.test();
	}

	public void m4(){
		D f = (E & D)(() -> System.out.println());
		f.test();
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

	public interface C {
	}

	public interface D {
		void test();
	}
	public interface E {
		void test();
	}
}
