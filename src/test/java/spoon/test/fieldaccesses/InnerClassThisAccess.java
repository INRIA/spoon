package spoon.test.fieldaccesses;

public class InnerClassThisAccess {
	public void method() {
	}
	public class InnerClassForTest{
		@SuppressWarnings("unused")
		private void methode() {
			InnerClassThisAccess.this.method();
		}
	}

}
