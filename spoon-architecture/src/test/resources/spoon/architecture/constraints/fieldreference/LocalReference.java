package spoon.architecture.constraTs.fieldreference;

public class LocalReference {

	//  ref in field
	private int a = 3;
	private int b = a;
	// ref in constructor
	private int c;
	// ref in method
	private int e;
	// ref in static block
	private int f;
	{
		f = 5;
		b = 4;
	}
	LocalReference() {
		this.c = 42;
	}
	foo() {
		e = 5;
	}

}
