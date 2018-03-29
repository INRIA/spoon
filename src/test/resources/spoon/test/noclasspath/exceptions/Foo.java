
public class Foo {

	void testCatchWithUnknownException(File file) {
		try {
			new FileReader(file);
		}
		catch (UnknownException e) {
			System.out.println(e);
		}
	}

	void testMultiCatchWithUnknownException(File file) {
		try {
			new FileReader(file);
		}
		catch (UnknownException | FileNotFoundException e) {
			System.out.println(e);
		}
	}
}