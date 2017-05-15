package spoon.test.method_overriding.testclasses;

import java.util.List;

public class B<S, R extends S> extends A<S> {

	public B() {
	}
	
	@Override
	B<S, R> m1(C c){
		return null;
	}
	
	@Override
	<T extends A<S>> T m2(C c){
		return null;
	}
	
	@Override
	void m3(List<? super C> c){
	}
	
	@Override
	void m5(S u) {
		super.m5(u);
	}

	@Override
	void m4(List<? extends A<S>> c) {
	}
}
