package spoon.test.position.testclasses;

import java.util.List;

public class PositionParameterTypeWithReference<T> {
	List<T> field1;
	List<T>[][] field2;
	List<T // */ >
	/*// */> 
	//>
	field3;
	List<List<?>> field4;
	boolean m1(Object o) {
		return o instanceof List<?>;
	}
}