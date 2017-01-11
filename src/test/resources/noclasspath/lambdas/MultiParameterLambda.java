import java.util.Map;
import java.util.HashMap;

import test.Unknown;

public class MultiParameterLambda {

	public void stringLambda() {
		final Map<String, String> map = new HashMap<>();
		map.put("a", "A");
		map.put("b", "B");
		map.put("c", "C");
		map.forEach((key, value) -> System.out.println(key + ", " + value));
	}

	public void integerLambda() {
		final Map<Integer, Integer> map = new HashMap<>();
		map.put(1, 100);
		map.put(2, 200);
		map.put(3, 300);
		map.forEach((key, value) -> System.out.println(key + ", " + value));
	}

	public void unknownLambda() {
		final Map<Unknown, Unknown> map = new HashMap<>();
		map.put(new Unknown(), new Unknown());
		map.put(new Unknown(), new Unknown());
		map.put(new Unknown(), new Unknown());
		map.forEach((key, value) -> System.out.println(key + ", " + value));
	}
}
