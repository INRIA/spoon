package spoon.test.refactoring.testclasses;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;

public class VariableRename
{
	public VariableRename() throws Throwable
	{
		int local1 = 0;
		//call all not private methods of this class automatically, to check assertions
		Method[] methods = getClass().getDeclaredMethods();
		for (Method method : methods) {
			try {
				if(Modifier.isPrivate(method.getModifiers())) {
					continue;
				}
				method.invoke(this);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException("Invocation of method "+method.getName()+" failed", e);
			}
		}
	}
	
	void callConflictWithParam() {
		conflictWithParam(2);
	}
	
	/**
	 * tests conflict of local variable with parameter
	 */
	private void conflictWithParam(@TryRename("-var1") int var2) {
		@TryRename("-var2")
		int var1 = 1;
		assertTrue(var1 == 1);
		assertTrue(var2 == 2);
	}
	
	/**
	 * tests conflict of local variable with CtCatchVariable
	 */
	private void conflictWithCatchVariable() {
		@TryRename({"-var2", "-var3"})
		int var1 = 1;
		try {
			assertTrue(var1 == 1);
			@TryRename({"-var1", "var3"})
			int var2 = 2;
			assertTrue(var2 == 2);
			throw new NumberFormatException();
		} catch (@TryRename({"-var1", "var2"}) NumberFormatException var3) {
			assertTrue(var1 == 1);
		}
		assertTrue(var1 == 1);
	}

	/**
	 * Tests nested class and conflict with field, 
	 * and nested local variable references, which must would be shadowed
	 */
	void nestedClassMethodWithRefs() {
		@TryRename({"-var2", "-var3", "-var4", "-var5", "-var6"})
		int var1 = 1;
		new Consumer<Integer>() {
			//must not rename to var1, because it would shadow var1 reference below
			@TryRename({"-var1", "-var3", "-var4", "-var5", "-var6"})
			int var2 = 2;
			@Override
			public void accept(
					//must not rename to var1, because reference to var1 below would be shadowed
					@TryRename({"-var1", "var2", "-var3", "-var5", "-var6"}) Integer var4
				) {
				//cannot rename to var1, because reference to var1 below would be shadowed 
				@TryRename({"-var1", "var2", "-var4", "-var5", "-var6"})
				int var3 = 3;
				try {
					//cannot rename to var1, because reference to var1 below would be shadowed 
					@TryRename({"-var1", "var2", "-var3", "-var4", "var6"})
					int var5 = 5;
					assertTrue(var1 == 1);
					assertTrue(var2 == 2);
					assertTrue(var3 == 3);
					assertTrue(var4 == 4);
					assertTrue(var5 == 5);
					throw new NumberFormatException();
				} catch (
						//cannot rename to var1, because reference to var1 below would be shadowed 
						@TryRename({"-var1", "var2", "-var3", "-var4", "var5"}) NumberFormatException var6) {
					assertTrue(var1 == 1);
					assertTrue(var2 == 2);
					assertTrue(var3 == 3);
					assertTrue(var4 == 4);
				}
			}
		}.accept(4);
		assertTrue(var1 == 1);
	}
	/**
	 * Tests nested class and conflict with field, 
	 * and no nested local variable references so rename is possible
	 */
	void nestedClassMethodWithoutRefs() {
		@TryRename({"var2", "var3", "var4", "var5", "var6"})
		int var1 = 1;
		new Consumer<Integer>() {
			//must not rename to var1, because it would shadow var1 reference below
			@TryRename({"-var1", "var3", "var4", "var5", "var6"})
			int var2 = 2;
			@Override
			public void accept(
					//must not rename to var1, because reference to var1 below would be shadowed
					@TryRename({"-var1", "var2", "-var3", "-var5", "-var6"}) Integer var4
				) {
				//can rename to var1, because reference to var1 below is not shadowed 
				@TryRename({"var1", "var2", "-var4", "-var5", "-var6"})
				int var3 = 3;
				try {
					//can rename to var1, because reference to var1 below is not shadowed 
					@TryRename({"var1", "var2", "-var3", "-var4", "var6"})
					int var5 = 5;
//					assertTrue(var1 == 1);//do not reference it in scope of other vars, so it can be renamed
					assertTrue(var2 == 2);
					assertTrue(var3 == 3);
					assertTrue(var4 == 4);
					assertTrue(var5 == 5);
					throw new NumberFormatException();
				} catch (
						//can rename to var1, because reference to var1 below is not shadowed 
						@TryRename({"var1", "var2", "-var3", "-var4", "var5"}) NumberFormatException var6) {
//					assertTrue(var1 == 1);//do not reference it in scope of other vars, so it can be renamed
					assertTrue(var2 == 2);
					assertTrue(var3 == 3);
					assertTrue(var4 == 4);
				}
			}
		}.accept(4);
		assertTrue(var1 == 1);
	}
	
	void nestedClassMethodWithShadowVarWithRefs() {
		@TryRename({"-var2", "var3"})
		int var1 = 2;
		new Runnable() {
			@TryRename({"var1", "var3"})
			int var2 = 3;
			@Override
			public void run() {
				assertTrue(var1 == 2);
				@TryRename({"-var1", "var2"})
				int var3 = 1;
				//this var1 shadows above defined var1. It can be renamed
				@TryRename({"var2", "-var3"})
				int var1 = 4;
				assertTrue(var1 == 4);
				assertTrue(var2 == 3);
				assertTrue(var3 == 1);
			}
		}.run();
		assertTrue(var1 == 2);
	}
	void nestedClassMethodWithShadowVarWithoutRefs() {
		@TryRename({"var2", "var3"})
		int var1 = 2;
		new Runnable() {
			@TryRename({"var1", "var3"})
			int var2 = 3;
			@Override
			public void run() {
				@TryRename({"-var1", "var2"})
//				assertTrue(var1 == 2); //the var1 is not referenced so it can be renamed
				int var3 = 1;
				//this var1 shadows above defined var1. It can be renamed
				@TryRename({"var2", "-var3"})
				int var1 = 4;
				assertTrue(var1 == 4);  
				assertTrue(var2 == 3);
				assertTrue(var3 == 1);
			}
		}.run();
		assertTrue(var1 == 2);
	}

	void nestedClassMethodWithShadowVarAndField() {
		@TryRename({"var2", "var3"})
		int var1 = 2;
		new Runnable() {
			@TryRename({"var2", "var3"})
			//this var1 shadows above defined var1.
			int var1 = 3;
			@Override
			public void run() {
				@TryRename({"-var1", "var2"})
				int var2 = 1;
				assertTrue(var1 == 3);
				@TryRename({"-var2", "var3"})
				int var1 = 4;
				assertTrue(var1 == 4);
				assertTrue(this.var1 == 3);
				assertTrue(var2 == 1);
			}
		}.run();
		assertTrue(var1 == 2);
	}

	void lambda() {
		@TryRename({"-var2", "-var3"})
		int var1 = 1;
		assertTrue(var1 == 1);
		Function<Integer, Integer> fnc = (@TryRename({"-var1", "-var3"}) Integer var2)->{
			@TryRename({"-var1", "-var2"})
			int var3 = 3;
			assertTrue(var1 == 1);
			assertTrue(var2 == 2);
			assertTrue(var3 == 3);
			return var2;
		};
		assertTrue(fnc.apply(2) == 2);
	}
	
	void tryCatch() {
		@TryRename({"-var2", "-var3", "-var4"})
		int var1 = 1;
		assertTrue(var1 == 1);
		try {
			@TryRename({"-var1","var3","var4"})
			int var2 = 2;
			assertTrue(var1 == 1);
			assertTrue(var2 == 2);
			throw new Exception("ex2");
		} catch (@TryRename({"-var1", "var2", "-var4"}) Exception var3) {
			@TryRename({"-var1", "var2", "-var3"})
			int var4 = 4;
			assertTrue(var1 == 1);
			assertTrue(var4 == 4);
		}
	}
}
