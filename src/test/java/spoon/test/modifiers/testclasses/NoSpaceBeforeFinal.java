package spoon.test.modifiers.testclasses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoSpaceBeforeFinal {
	public void m() {
		try (final InputStream input = new FileInputStream("file.txt")) {
			// some code
		} catch (IOException ex) {
			// some code
		}
	}
}
