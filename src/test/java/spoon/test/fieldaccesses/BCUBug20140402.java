package spoon.test.fieldaccesses;

public class BCUBug20140402 {
	Object[] data;
	public void run(){
		int a = this.data.length;
	}
}

