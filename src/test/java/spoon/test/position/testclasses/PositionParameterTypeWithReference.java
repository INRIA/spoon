package spoon.test.position.testclasses;

import java.util.List;

public class PositionParameterTypeWithReference<T extends List<?>, X> {
	List<T> field1;
	List<T>[][] field2;
	List<T // */ >
	/*// */> 
	//>
	field3;
	List<List<?>> field4;
	List<? extends List<?>> field5;
	boolean m1(Object o) {
		return o instanceof List<?>;
	}
	boolean m2(Object o) {
		return false || o instanceof List<?>;
	}
	
	<U extends List<?>> void m3(U u) {}
}