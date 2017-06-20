package spoon.test.template.testclasses;

public class SubstituteLiteralTemplate {

	String stringField1 = "$param1$";
	String stringField2 = "Substring $param1$ is substituted too - $param1$";

	void m1() {
		System.out.println(Params.$param1$());
	}
}

class Params {
	static int $param1$() {
		return 0;
	}
}
