package spoon.test.model;

public class Foo extends Bar {
	int i;
	void fooMethod(){}
	
	Foo() {}
	
	@Override
	void m() {}
}

class Bar extends Baz {}

class Baz {
	
	int j;
	void bazMethod(){}
	void m() {}

}