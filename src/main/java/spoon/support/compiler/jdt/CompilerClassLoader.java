package spoon.support.compiler.jdt;

import java.net.URL;
import java.net.URLClassLoader;

public class CompilerClassLoader extends URLClassLoader {
	public CompilerClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
}