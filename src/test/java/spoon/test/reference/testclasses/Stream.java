package spoon.test.reference.testclasses;

import java.util.ArrayList;

public class Stream {
	public Stream() {
		new ArrayList().stream().toArray(Bar[]::new);
	}
}
