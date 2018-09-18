package spoon.test.staticFieldAccess2.testclasses;


import spoon.test.staticFieldAccess2.testclasses.Constants;

public class ImplicitStaticFieldReference {
	/*
	 * The static field has exactly same name like the Class name.
	 * for example Apache CXF generates classes like that  
	 */
	public static String ImplicitStaticFieldReference = "c1";
	public static long staticField = Constants.PRIO;

	public String reader() {
		return ImplicitStaticFieldReference;
	}
	
	public void writer(String value) {
		ImplicitStaticFieldReference = value;
	}
	
	public static long longReader() {
		return staticField;
	}
	
	public static void longWriter(long value) {
		staticField = value;
	}

	public void testLocalMethodInvocations() {
		reader();
		longWriter(7);
	}
}
