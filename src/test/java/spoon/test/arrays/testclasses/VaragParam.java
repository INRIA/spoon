package spoon.test.arrays.testclasses;

import java.util.List;

public class VaragParam {

	void m1(List<?>... arg) {
	}

	void m2(List<?>[] arg) {
	}

	void m3(List<?>[]... arg) {
	}

	void m4(List<?> arg) {
	}
}
