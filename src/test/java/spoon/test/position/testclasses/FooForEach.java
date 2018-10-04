package spoon.test.position.testclasses;

import java.util.List;

public class FooForEach {

	public void m(List<String> items) {
		for (String item : items) {}
		for (final String item : items) {
		};
		//some comment
		for (/*1*/ final @Deprecated /*2*/ String /*3*/ i /*4*/ : items) 
			this.getClass(); /*5*/
	}
}