package spoon.architecture.preconditions.modifier;

public abstract class Modifiers {

	final int a = 3;
	static Object b;
	transient Object c;
	public Object d;
	public synchronized strictfp int bar() {
		return 42;
	}
}
