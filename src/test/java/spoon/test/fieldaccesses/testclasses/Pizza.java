package spoon.test.fieldaccesses.testclasses;

public class Pizza
{
	int size;

	void setSize(int size) {
		this.size = size;
	}

	int getSize(int size) {
		return size;
	}

	void addSize(int plus) {
		size = size + plus;
	}
}
