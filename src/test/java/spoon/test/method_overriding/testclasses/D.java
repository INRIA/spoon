package spoon.test.method_overriding.testclasses;

import java.util.List;

public class D extends B<Integer, Integer> {

	public D() {
	}

	@Override
	B<Integer, Integer> m1(C c){
		return null;
	}
	
	@Override
	D m2(C c){
		return null;
	}
	
	@Override
	void m4(List<? extends A<Integer>> c){
	}
	
}
