package spoon.test.processing.testclasses;

public class SampleForInsertBefore {

	public SampleForInsertBefore() {
		new Thread() {
		};
	}

	public SampleForInsertBefore(int j) {
		this(j, 0);
		new Thread() {
		};
		switch (j) {
      default:
        break;
    }
    switch (j) {
      default:
        break;
    }
    switch (j) {
      default: {
        break;
      }
    }
	}

	public SampleForInsertBefore(int j, int k) {
		super();
		new Thread() {
		};
	}
	
	void method() {
	}

	void method2() {
		new Thread() {
		};
	}

	Thread method3() {
		return new Thread() {
		};
	}
}
