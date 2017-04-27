package spoon.test.generics.testclasses;

import java.util.List;

public class Orange<K,L> {

	class A<O extends K, M extends O> {
		List<List<M>> list2m;
		void method(List<? extends M> param) {}
	}
	
	class B<N extends K, P extends N> extends A<N, P> {
	}
}
