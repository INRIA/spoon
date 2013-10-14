package spoon.test.fieldaccesses;

public class InnerClassThisAccess {
	public void method() {
	}
	public class InnerClassForTest{
		private void methode() {
			InnerClassThisAccess.this.method();
		}
	}

}
