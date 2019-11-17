package spoon.test.prettyprinter.testclasses.loop;

import java.util.Arrays;

public class ForEachNoBraces {

	public void example() {
		for (int i : Arrays.asList(1, 2, 3, 4, 5))
			System.out.println(i);
	}

}

