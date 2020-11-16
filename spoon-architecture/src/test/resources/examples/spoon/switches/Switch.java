package examples.spoon.switches;

public class Switch {

	public void bar() {
		switch (1) {
			case 2:
				break;

			default:
				break;
		}
	}

	public void foo() {
		int a,b = 4;
		switch (1) {
			case a,b -> System.out.println("foo");;
		}
		int q = switch (1) {
			default -> 3;
		}
	}
}
