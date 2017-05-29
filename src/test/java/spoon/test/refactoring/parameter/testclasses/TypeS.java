package spoon.test.refactoring.parameter.testclasses;

public class TypeS extends TypeR {
	@Override
	@TestHierarchy("R_method1")
	public void method1(Double p1) {
	}
	
	private void methodWithLambdaOf_A() {
		IFaceB ifaceB = p->{
			@TestHierarchy("A_method1")
			int x;
		};
		ifaceB.method1(1);
	}

	private void methodWithLambdaOf_R() {
		IFaceT ifaceT = p->{
			@TestHierarchy("R_method1")
			int x;
		};
		ifaceT.method1(1.0);
	}
}
