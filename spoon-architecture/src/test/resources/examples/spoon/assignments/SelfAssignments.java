package examples.spoon.assignments;

public class SelfAssignments {

	private static int a;
	SelfAssignments() {
		a = 5;
		this.a = this.a;
	}

}
