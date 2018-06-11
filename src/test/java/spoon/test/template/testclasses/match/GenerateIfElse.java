package spoon.test.template.testclasses.match;

public class GenerateIfElse {

	public void generator(boolean option) {
		if (option) {
			//generates `print`
			System.out.print("string");
		} else {
			//generates `println`
			System.out.println("string");
		}
	}
	
}
