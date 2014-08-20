package spoon.test.annotation;

public class Main {

	public void m(@Bound(max = 8) int a) {
	} 

	@AnnotParamTypes(
		integer=42, integers={},
		string="Hello World!", strings={"Hello","World"},
		clazz=Integer.class, classes={Integer.class, String.class})
	public void m1() {}
	
	final public static int INTEGER = 42;

	@AnnotParamTypes(
		integer=INTEGER, integers={},
		string="Hello World!", strings={"Hello","World"},
		clazz=Integer.class, classes={Integer.class, String.class})
	public void m2() {}
}
