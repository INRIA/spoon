package spoon.test.trycatch.testclasses;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

	public void test() {

		try {
			System.out.println("test");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (BufferedReader br = new BufferedReader(new FileReader("test"))) {
			br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
