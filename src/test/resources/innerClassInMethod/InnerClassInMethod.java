package innerClassInMethod;

public class InnerClassInMethod {

	void m() {
		new Runnable() { public void run() {}};
		class NotAnonymousClass$1 {}
	}
}
