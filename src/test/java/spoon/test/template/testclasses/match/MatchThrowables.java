package spoon.test.template.testclasses.match;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MatchThrowables {

	public void matcher1() throws IOException, FileNotFoundException {
	}
	
	public static void sample1() {
	}
	
	void sample2(int a, MatchThrowables me) throws FileNotFoundException, IllegalArgumentException, IOException, UnsupportedOperationException {
	}

	void sample3(int a, MatchThrowables me) throws FileNotFoundException, IllegalArgumentException, IOException {
	}

	private void sample4() throws IOException, FileNotFoundException {
		this.getClass();
		System.out.println();
	}
	
	int noMatchBecauseReturnsInt() throws IOException, Exception {
		return 0;
	}
}
