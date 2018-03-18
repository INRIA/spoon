package spoon.test.query_function.testclasses;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * The main purpose of this test is to be transfomed by Spoon into Spoon model, 
 * which is then used by {@link spoon.test.query_function.VariableReferencesTest}
 * 
 * The values of the fields must be a sequence starting from 1
 */
public class VariableReferencesModelTest {
	int field = 15;

	@Test
	public void localVarsInNestedBlocks() {
		assertTrue(this.field == 15);
		{
			{
				int field = 1;
				assertTrue(field == 1);
			}
			int f1,f2,f3,field = 2,f4;
			assertTrue(field == 2);
		}
		int field = 3;
		assertTrue(field == 3);
		{
			assertTrue(field == 3);
		}
		assertTrue(this.field == 15);
	}
	
	@Test
	public void localVarsInTryCatch() {
		try
		{
			int field = 4;
			assertTrue(field == 4);
			throw new IllegalArgumentException();
		}
		catch(IllegalArgumentException e) {
			assertTrue(field == 15);
			{
				int field = 5;
				assertTrue(field == 5);
			}
			int field = 6;
			assertTrue(field == 6);
		}
		catch(Exception /*7*/field) {
			//7
			field.getMessage();
		}
	}
	
	@Test
	public void localVarsInWhile() {
		while(true) {
			int field = 8;
			assertTrue(field == 8);
			break;
		}
		int field = 9;
		assertTrue(field == 9);
	}
	
	@Test
	public void localVarsInFor() {
		for(int field=10;field == 10;) {
			assertTrue(field == 10);
			break;
		}
		int field = 11;
		assertTrue(field == 11);
	}
	
	@Test
	public void localVarsInSwitch() {
		switch(7) {
		case 7:
			int field=12;
			assertTrue(field == 12);
			break;
		}
		int field = 13;
		assertTrue(field == 13);
	}
	
	@Test
	public void localVarsInTryWithResource() throws IOException {
		try(/*14*/Reader field=new StringReader("")) {
			//14
			field.toString();
		}
	}
	
	@Test
	public void checkParameter() {
		parameter(16);
	}
	private void parameter(int field) {
		assertTrue(field == 16);
		{
			assertTrue(field == 16);
		}
		while(true) {
			assertTrue(field == 16);
			break;
		}
	}
	
	@Test
	public void parameterInLambdaWithBody() {
		Consumer<Integer> fnc = (field)->{
			assertTrue(field == 17);
		};
		fnc.accept(17);
	}
	
	@Test
	public void parameterInLambdaWithExpression() {
		Consumer<Integer> fnc = (field)->assertTrue(field == 18);
		fnc.accept(18);
	}
	
	@Test
	public void localVarInLambda() {
		Runnable fnc = ()->{
			int field = 19;
			assertTrue(field == 19);
		};
		fnc.run();
		
		int field = 20;
		Runnable fnc2 = ()->{
			assertTrue(field == 20);
		};
		fnc2.run();
	}
	
	static
	{
		int field = 21;
		assertTrue(field == 21);
	}
	
	{
		int field = 22;
		assertTrue(field == 22);
	}
	/*
	 * check localVariable shadowed in Local class method by another LocalVariable declaration
	 */
	@Test
	public void localVarInNestedClass() {
		int field = 23;
		assertTrue(field == 23);
		Runnable fnc = new Runnable(){
			@Override
			public void run() {
				{
					int field = 24;
					assertTrue(field == 24);
				}
				assertTrue(field == 23);
				int field = 25;
				assertTrue(field == 25);
			}
		};
		fnc.run();
		assertTrue(field == 23);
	}
	
	/*
	 * check localVariable shadowed in Local class method by CtField 27
	 */
	@Test
	public void localVarInNestedClass2() {
		int field = 26;
		assertTrue(field == 26);
		Runnable fnc = new Runnable(){
			int field = 27;
			@Override
			public void run() {
				{
					int field = 36;
					assertTrue(field == 36);
				}
				assertTrue(field == 27);
				int field = 28;
				assertTrue(field == 28);
				assertTrue(this.field == 27);
			}
		};
		fnc.run();
		assertTrue(field == 26);
	}
	
	class A {
		int field = 29;
	}
	
	abstract class B extends A {
		abstract void run();
	}
	
	/*
	 * check localVariable shadowed in Local class method by inherited CtField 29
	 */
	@Test
	public void localVarInNestedClass4() {
		int field = 30;
		assertTrue(field == 30);
		B fnc = new B(){
			@Override
			public void run() {
				{
					int field = 31;
					assertTrue(field == 31);
				}
				assertTrue(field == 29);
				int field = 32;
				assertTrue(field == 32);
				assertTrue(this.field == 29);
			}
		};
		fnc.run();
		assertTrue(field == 30);
	}
	
	/*
	 * check localVariable shadowed in Local class anonymous executable by inherited CtField 29
	 */
	@Test
	public void localVarInNestedClass5() {
		int field = 33;
		assertTrue(field == 33);
		class Local {
			{
				{
					int field = 34;
					assertTrue(field == 34);
				}
				assertTrue(field == 33);
				int field = 35;
				assertTrue(field == 35);
			}		
		}
		new Local();
		assertTrue(field == 33);
	}
	
	/*
	 * check localVariable shadowed in Local class method by method parameter 39
	 */
	@Test
	public void localVarInNestedClass6() {
		int field = 37;
		assertTrue(field == 37);
		class Local {
			int field = 38;
			void method(int field) {
				assertTrue(field == 39);
				assertTrue(this.field == 38);
			}		
		}
		new Local().method(39);
		assertTrue(field == 37);
	}
	/*
	 * The value of maxValue must be equal to maximum value of all variable values assigned above.
	 * It is here mainly to keep this model consistent. 
	 * If you write new test method, then always update this maxValue
	 */
	private static final int maxValue = 39;
	
}
