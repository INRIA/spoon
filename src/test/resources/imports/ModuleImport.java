import module java.base;

public class ModuleImport {
	public static void main(String[] args) {
		String[] fruits = new String[] { "apple", "berry", "citrus" };
		Map<String, String> m = Stream
			.of(fruits)
			.collect(Collectors.toMap(
				s -> s.toUpperCase().substring(0,1),
				Function.identity()));
		m.forEach((k, v) ->
			System.out.println(k + " " + v));
	}
}
