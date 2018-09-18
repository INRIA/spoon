package spoon.test.initializers.testclasses;

import java.util.ArrayList;
import java.util.List;

public class InstanceInitializers {

	{
		// static initializer
		x=3;
	}
	
	int x;
	
	int y = 3;
	
	// with autoboxing
	Integer z = 5;
	
	List<Double> k = new ArrayList<Double>();
	
	List<Double> l = new ArrayList<Double>() {
		private static final long serialVersionUID = 1L;
		final static double PI = 3.14;
        final double PI2 = 3.14;
        double PI3 = 3.14;
        { add(12.0); add(15.0); add(PI); add(PI2); add(PI3); }};
	
}
