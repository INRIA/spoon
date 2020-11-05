package spoon.architecture.constraints.invocationmatcher;

public class ConstructorUsage {

	public ConstructorUsage() {
		foo();
	}

	public void foo() {
		new ConstructorUsage();
	}
}
