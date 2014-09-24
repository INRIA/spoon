package spoon.test.annotation.testclasses;

@TestAnnotation
public class Main {

	@TestAnnotation
	public Main()
	{ }

	public void m(@Bound(max = 8) int a) {
	} 

	@AnnotParamTypes(
		integer=42, integers={42},
		string="Hello World!", strings={"Hello","World"},
		clazz=Integer.class, classes={Integer.class, String.class},
		b=true, byt=42, c='c', s=(short)42, l=42, f=3.14f, d=3.14159,
		e=AnnotParamTypeEnum.G, ia=@InnerAnnot("dd"))
	public void m1() {}
	
	final public static int INTEGER = 42;
	final public static String STRING = "Hello World!";
	final public static String STRING1 = "Hello";
	final public static String STRING2 = "world";
	final public static boolean BOOLEAN = false;
	final public static byte BYTE = 42;
	final public static char CHAR = 'c';
	final public static short SHORT = 42;
	final public static short LONG = 42;
	final public static float FLOAT = 3.14f;
	final public static double DOUBLE = 3.14159;

	@AnnotParamTypes(
		integer=INTEGER, integers={INTEGER},
		string=STRING, strings={STRING1,STRING2},
		clazz=Integer.class, classes={Integer.class, String.class},
		b=BOOLEAN, byt=BYTE, c=CHAR, s=SHORT, l=LONG, f=FLOAT, d=DOUBLE,
		e=AnnotParamTypeEnum.G, ia=@InnerAnnot("dd"))
	public void m2() {}

	@Override
	public String toString()
	{
		return super.toString();
	}
}
