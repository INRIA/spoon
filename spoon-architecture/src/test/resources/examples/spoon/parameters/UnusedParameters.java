package examples.spoon.parameters;

public class UnusedParameters {

	UnusedParameters(int a) {

	}
	public void name(String args) {
		first: for (int i = 0; i < 10; i++) {
			second: for (int j = 0; j < 5; j++) {
				break first;
			}
		}
	}
}
