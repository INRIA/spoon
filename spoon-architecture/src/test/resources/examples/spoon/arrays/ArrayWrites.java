package examples.spoon.arrays;

public class ArrayWrites {

	int[] bar = new int[5];

	public int test() {
		bar[2 - 1] = 5;
		return bar[2 - 1];
	}
}
