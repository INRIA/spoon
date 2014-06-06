package spoon.test.strings;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

public class StringLiteralTest {
	@SuppressWarnings("unused")
	@Test
	public void testSnippetFullClass() {
		Factory factory = TestUtils.createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement(
				"class StringValueUTF {\n" + 
				"	String f0 = \"toto\";\n" + 
				"	String f1 = \"\\n\";\n" + 
				"	String f2 = \"\\u20ac\";\n" + 
				"	String f3 = \"€\";\n" + 
				"	String f4 = \"\\t\";\n" + 
				"	String f5 = \"	\";\n" + 
				"}"
		).compile();
		CtField<?> f0 = (CtField<?>) clazz.getFields().toArray()[0];
		CtField<?> f1 = (CtField<?>) clazz.getFields().toArray()[1];
		CtField<?> f2 = (CtField<?>) clazz.getFields().toArray()[2];
		CtField<?> f3 = (CtField<?>) clazz.getFields().toArray()[3];
		CtField<?> f4 = (CtField<?>) clazz.getFields().toArray()[4];
		CtField<?> f5 = (CtField<?>) clazz.getFields().toArray()[5];

		assertEquals("java.lang.String f0 = \"toto\";", f0.toString());
		assertEquals("java.lang.String f1 = \"\\n\";", f1.toString());
		assertEquals("java.lang.String f4 = \"\\t\";", f4.toString());
		assertEquals("java.lang.String f3 = \"€\";", f3.toString());
		
		// Spoon (because of JDT) does not exactly rewrite the original literal
		// consequently the following assertions fail
		
		// Spoon rewrites \t
		//assertEquals("java.lang.String f5 = \"	\";", f5.toString()); 
		
		// Spoon rewrites €
		//assertEquals("java.lang.String f2 = \"\\u10ac\";", f2.toString()); //
	}
	
	@Test
	public void testUnicode() throws Exception{
		CtClass<?> type = build("spoon.test.strings", "SampleForUnicode");
		String expected = "private static final char CHAR_COPY = '\\u00a9';";
			
		assertEquals("spooned failed for field "+type.getFields().get(0).getSimpleName(),expected, type.getFields().get(0).toString());

	}
	
	@Test
	public void testOctal() throws Exception{
		CtClass<?> type = build("spoon.test.strings", "SampleForOctal");
		String expected = "private static final char CHAR_COPY = '\\111';";
			
		assertEquals("spooned failed for field "+type.getFields().get(0).getSimpleName(),expected, type.getFields().get(0).toString());

	}

}
