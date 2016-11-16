package spoon.test.ctBodyHolder.testclasses;

public class ClassWithBodies
{
	String var;
	
	public ClassWithBodies()
	{
		var = "constructor_body";
	}
	
	public void method() {
		var = "method_body";
	}
	public void method2() {
		try {
			var = "try_body";
		} catch(Exception e) {
			var = "catch_body";
		}
		for(int i=0; i<10; i++) var="for_statemnt";
		for(int i=0; i<10; i++) {
			var="for_block";
		}
		while(1+1>var.length()) {
			var="while_block";
		}
	}
}
