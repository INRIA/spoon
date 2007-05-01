package spoon.test.control;

public class Fors {

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	void multipleInit() {
		for (int i = 0, j = 0; i < 2; j++) {

		}
	}

	void empty1() {
		int i = 0;
		for (i = 0;; i++) {
		}
	}

	void empty2() {
		int i = 0;
		for (;; i++) {
		}
	}

	public static void main(String[] args) {
		System.out.println("test");
		m();
		if(args.length==0) {
			System.out.println("no args");
		} else {
			System.out.println("args");
		}
		System.out.println("end");
	}
	
	static void m() {
		
	}
}
