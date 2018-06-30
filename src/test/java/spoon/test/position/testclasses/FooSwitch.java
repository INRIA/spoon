package spoon.test.position.testclasses;

public class FooSwitch {
	
	enum ENUM {
		C0,
		C1,
		C2,
		C3,
		C4
	}

	public int m1(ENUM e) {
		switch(e) {
		case C0: 
		case C1: 
			System.out.println();
			break;
		case C2: {
			return 2;
		}
		case C3: {
			return 2;
		}
		default:
			System.out.println();
			break;
		}
		return 0;
	}
}