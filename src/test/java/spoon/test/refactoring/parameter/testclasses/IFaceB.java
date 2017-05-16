package spoon.test.refactoring.parameter.testclasses;

public interface IFaceB<T> {
	@TestHierarchy("A_method1")
	void method1(T p1);
}
