package spoon.test.fieldaccesses;

public class Mouse {

	int[] is;
	
	int age;

	Mouse son;

	public void method() {
		son.age = 2;
		this.son.age = 3;
		age = is.length;
	}

}
