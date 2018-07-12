package spoon.test.secondaryclasses.testclasses;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

class TopLevel {

	class InnerClass {
		Vector<?> v;
	}
	
}

class Secondary implements Serializable, I, K {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<?,?> m;
	String s;
	int i;
	void m() {}
	void m1() {}
	int m3() {
		
		return 0;
	}
}

enum E {
	T1, T2
}

interface I {}

interface K {}
