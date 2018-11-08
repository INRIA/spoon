package spoon.test.annotation.testclasses;

@TestAnnotation
public class Main {

	@TestAnnotation
	public Main() {
	}

	public void m(@Bound(max = 8) int a) {
	}

	// test default value
	public void nn(@Bound int param2) {
	}

	@AnnotParamTypes(
			integer = 42, integers = { 42 },
			string = "Hello World!", strings = { "Hello", "World" },
			clazz = Integer.class, classes = { Integer.class, String.class },
			b = true, byt = 42, c = 'c', s = (short) 42, l = 42, f = 3.14f, d = 3.14159,
			e = AnnotParamTypeEnum.G, ia = @InnerAnnot("dd"))
	public void m1() {
	}

	public static final int INTEGER = 42;
	public static final String STRING = "Hello World!";
	public static final String STRING1 = "Hello";
	public static final String STRING2 = "world";
	public static final boolean BOOLEAN = false;
	public static final byte BYTE = 42;
	public static final char CHAR = 'c';
	public static final short SHORT = 42;
	public static final short LONG = 42;
	public static final float FLOAT = 3.14f;
	public static final double DOUBLE = 3.14159;

	@AnnotParamTypes(
			integer = INTEGER, integers = { INTEGER },
			string = STRING, strings = { STRING1, STRING2 },
			clazz = Integer.class, classes = { Integer.class, String.class },
			b = BOOLEAN, byt = BYTE, c = CHAR, s = SHORT, l = LONG, f = FLOAT, d = DOUBLE,
			e = AnnotParamTypeEnum.G, ia = @InnerAnnot("dd"))
	public void m2() {
	}

	@AnnotParamTypes(
			integer = INTEGER + 3, integers = { INTEGER - 2, INTEGER * 3 },
			string = STRING + "concatenated", strings = { STRING1 + "concatenated", STRING2 + "concatenated" },
			clazz = Integer.class, classes = { Integer.class, String.class },
			b = !BOOLEAN, byt = BYTE ^ 1, c = CHAR | 'd', s = SHORT / 2, l = LONG + 1, f = FLOAT * 2f, d = DOUBLE / 3d,
			e = AnnotParamTypeEnum.G, ia = @InnerAnnot("dd" + "dd"))
	public void m3() {
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@AnnotArray({ RuntimeException.class })
	public void testValueWithArray() {
	}

	@AnnotArray(RuntimeException.class)
	public void testValueWithoutArray() {
	}
}
