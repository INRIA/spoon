package spoon.test.refactoring.parameter.testclasses;

public class TypeC extends TypeB {
	@Override
	@TestHierarchy("A_method1")
	public void method1(Exception p1) {
		super.method1(p1);
	}
}
