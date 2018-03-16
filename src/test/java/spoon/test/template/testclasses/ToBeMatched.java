package spoon.test.template.testclasses;

import java.util.Arrays;
import java.util.stream.Collectors;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.factory.Factory;

public class ToBeMatched {

	public void match1() {
		Arrays.asList("a", "b", "c", "d", "e", "f", "a", "b", "c", "d", "e");
	}

	public void match2() {
		Arrays.asList("a", "b", "b", "b", "c", "c", "d", "d", "d", "d", "d");
	}
	
}
