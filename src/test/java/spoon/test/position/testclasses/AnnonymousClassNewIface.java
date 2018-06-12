package spoon.test.position.testclasses;

import java.util.Set;
import java.util.function.Consumer;

public class AnnonymousClassNewIface {
	public void m() {
		Object o = new Consumer<Set<?>>() {
			@Override
			public void accept(Set<?> t) {
			}
		};
	}	

}
