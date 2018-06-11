package spoon.test.template.testclasses.match;

public class MatchMap {

	@Check()
	void matcher1() {
	}
	
	@Check(value = "xyz")
	void m1() {
	}
	
	@Check(timeout = 123, value = "abc")
	void m2() {
	}

	@Deprecated
	void notATestAnnotation() {
	}

	@Deprecated
	@Check(timeout = 456)
	void deprecatedTestAnnotation1() {
	}

	@Check(timeout = 4567)
	@Deprecated
	void deprecatedTestAnnotation2() {
	}
}
