package spoon.test.generics;

import spoon.Launcher;

import java.io.File;
import java.util.ArrayList;

public class ClassThatBindsAGenericType extends ArrayList<File> {

	public static void main(String[] args) throws Exception {
		Launcher.main(new String[] {
				"-i", "src/test/java/spoon/test/generics",
				"-g", "--no"
		});
	}
	
}

class ClassThatDefinesANewTypeArgument<T> {
	void foo(T t){}
}