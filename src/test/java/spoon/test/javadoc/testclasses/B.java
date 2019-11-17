package spoon.test.javadoc.testclasses;

public class B {

	/**
	 * @version 42
	 */
	enum Something { ONE, TWO, THREE }

	/**
	 * @author somebody
	 */
	void m1() {}

	/**
	 * @deprecated
	 */
	void m2() {}

	/**
	 * @exception RuntimeException if ...
	 */
	void m3() {}

	/**
	 * @param a ...
	 */
	void m4(int a) {}

	/**
	 * @return 42
	 */
	int m5() { return 42; }

	/**
	 * @see B
	 */
	void m6() {}

	/**
	 * @serial description
	 */
	int m7;

	/**
	 * @serialData description
	 */
	void m8() {}

	/**
	 * @serialField description
	 */
	int m9;

	/**
	 * @since 42
	 */
	void m10() {}

	/**
	 * @throws RuntimeException if ...
	 */
	void m11() {}

	/**
	 * @somethingInconsistent ...
	 */
	void m12() {}
}
