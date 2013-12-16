package spoon.test.generics;

import java.io.File;
import java.util.ArrayList;

public class ClassThatBindsAGenericType extends ArrayList<File> {

	public static void main(String[] args) throws Exception {
	}
	
}

class ClassThatDefinesANewTypeArgument<T> {
	void foo(T t){}
}