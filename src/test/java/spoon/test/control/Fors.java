package spoon.test.control;

public class Fors {

  void normalFor() {
    for (int i = 0; i < 2; i++) {

    }
    
  }
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

}
