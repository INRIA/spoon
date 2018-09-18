package spoon.test.reference.testclasses;

public class MyClass2 {
	final MyClass myClass;
	final MyClass3<Integer, String> myClass3;

	public MyClass2() {
		myClass = new MyClass();
		myClass3 = new MyClass3<Integer, String>();
	}

	public void methodA() {
		myClass.method1("guyfez");
	}

	public void methodB() {
		myClass.method2();
	}

	public void methodC() {
		myClass3.methodI(1);
		myClass3.methodII(42, "Call method II");
	}

	public void methodD() {
		methodE("Call method B");
	}

	public void methodE(String param) {
		methodF(42, "Call method C");
	}

	public void methodF(int param1, String param2) {
	}
}
