package spoon.test.fieldaccesses.testclasses.internal;

import java.util.ArrayList;
import java.util.List;

public class Foo extends Bar.Inner {
	class Test {
		class Test2 {
		}
	}
	public static class Fails {
        public final List<String> keyValues = new ArrayList<>();
        
        public Fails() {
        	keyValues.add("");
        }
	}
}
