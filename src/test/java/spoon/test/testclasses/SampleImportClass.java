package spoon.test.testclasses;

import java.util.List;
import static java.util.Collections.EMPTY_LIST;

public class SampleImportClass {

	public SampleImportClass() {
		new Thread() {
		};
	}

	public SampleImportClass(int j) {
		this(j, 0);
		new Thread() {
		};
		List<?> emptyList = EMPTY_LIST;
	}

	public SampleImportClass(int j, int k) {
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
