package spoon.test.ctCase;

public class ClassWithSwitchExample {

	int methodWithSwitch(char aChar) {
		int result;
		switch (aChar) {
			case 'a':
				result = 1;
				break;
			case 'z':
				result = 25;
				break;
			default:
				result = -1;
				break;
		}
		return result;
	}
	
}
