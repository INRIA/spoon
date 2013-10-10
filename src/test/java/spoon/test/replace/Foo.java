package spoon.test.replace;

class Foo {
	int i;
	void foo() {
		int x = 3;
		int z;
		z= x+1;
		System.out.println(z);
	}
}

class Bar {
	float i;
}
