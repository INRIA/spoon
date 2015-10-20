package spoon.test.replace.testclasses;

class Foo {
	int i;
	void foo() {
		int x = 3;
		int z;
		z= x+1;
		System.out.println(z);
	}

	void bar() {
		int y = 4;
	}



	public void retry() {

		new Foo();

	}


	private void statements() {
		String a = "";
		System.out.println(a.toLowerCase());
	}
}

class Bar {
	float i;
}
