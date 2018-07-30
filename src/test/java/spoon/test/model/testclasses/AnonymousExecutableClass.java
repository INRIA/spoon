package spoon.test.model.testclasses;

import java.util.HashMap;
import java.util.Map;

public class AnonymousExecutableClass {

	static Map<String,Integer> numbers =
		new HashMap<String,Integer>() {
			private static final long serialVersionUID = 4293695943460830881L;
		{
			put("one",1);
		}};
}
