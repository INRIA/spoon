package spoon.test.fieldaccesses.testclasses;

public class BCUBug20140402 {
	Object[] data = null;
	public void run(){
		int a = this.data.length;
	}
}

