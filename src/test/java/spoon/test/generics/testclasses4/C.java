package spoon.test.generics.testclasses4;

import java.util.List;

public class C {
	static <V extends W, W extends T, T extends U, U extends List<String>> void m(W t) {
    	m(t);
    }
}
