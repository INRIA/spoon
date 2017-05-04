package spoon.test.method_overriding.testclasses;

import java.util.List;

public class A<U> {

	public A() {
	}
	
	A<U> m1(C c){
		return null;
	}

	<T extends A<U>> T m2(C c){
		return null;
	}

	void m3(List<? super C> c){
	}
	void m4(List<? extends A<U>> c){
	}
	void m5(U u) {
	}
}
