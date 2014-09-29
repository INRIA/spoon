package spoontest.a;

import spoontest.b.ClassB;

public class ClassA {

	public ClassA() {
		namer = new ClassB();
	}
	
	ClassB namer;
}
