package spoon.test.targeted.testclasses;


public class InnerClassThisAccess {
	public void method() {
	}
	public class InnerClassForTest{
		@SuppressWarnings("unused")
		private void method2() {
			InnerClassThisAccess.this.method();
		}
	}

    public void otherMethod() {

        class InnerClass {

            private final boolean b;

            InnerClass(boolean b) {
            	this.b = b;
            }

            boolean getB() {
            	return b;
            }
        }
        
        new InnerClass(true).getB();
    }
}
