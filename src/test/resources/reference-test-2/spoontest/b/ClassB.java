package spoontest.b;

import spoontest.c.ClassC;

public class ClassB {

	public ClassB() {
		namer = new ClassC();
	}
	
	public String name(Object o) {
		return namer.name(o);
	}
	
	ClassC namer;
}
