package spoon.test.prettyprinter.testclasses;

public class MissingVariableDeclaration {

	//this field is removed by refactoring. Then the PrettyPrinter of m fails...
	int testedField;
	
	void failingMethod() {
		testedField = 1;
	}

}
