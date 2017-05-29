package spoon.test.methodreference.testclasses;

import java.io.InputStream;
import java.io.Reader;

public class Cloud<T extends String> {

	<U extends InputStream> void method(T param, U param2) {}
	<U extends Reader> void method(T param, U param2) {}
}

class Sun {
	void foo() {
		Cloud<String> cc = new Cloud<>();
		cc.method("x", (InputStream)null);
	}
}
