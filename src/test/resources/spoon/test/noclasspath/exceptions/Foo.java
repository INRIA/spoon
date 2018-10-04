
public class Foo {

	void testCatchWithUnknownException(File file) {
		try {
			new FileReader(file);
		}
		catch (UnknownException e) {
			System.out.println(e);
		}
	}

	void testMultiCatchWithUnknownException1(File file) {
		try {
			new FileReader(file);
		}
		catch (UnknownException | FileNotFoundException e) {
			System.out.println(e);
		}
	}

	void testMultiCatchWithUnknownException2(File file) {
		try {
			new FileReader(file);
		}
		catch (FileNotFoundException | UnknownException e) {
			System.out.println(e);
		}
	}
}
