package spoon.test.position.testclasses;

import java.io.IOException;

public class CatchPosition {
	void method() {
		try {
			throw new IOException();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} /*1*/ catch/*2*/ ( /*3*/ final @Deprecated /*4*/ ClassCastException /*5*/ e /*6*/) /*7*/ {
			throw new RuntimeException(e);
		} catch /* ignore this catch */
		//and this catch too!
		( /**catch it ( */
				//catch (
				OutOfMemoryError|RuntimeException e) {
			throw new RuntimeException(e);
		} finally {/*final block*/}
	};
}