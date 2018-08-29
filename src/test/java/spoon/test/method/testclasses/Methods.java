package spoon.test.method.testclasses;

import java.util.List;

public interface Methods<U, V extends String> {
	<T> void objectMethod1(T p);
	<T extends Object> void objectMethod2(T p);
	void objectMethod3(U p);
	void objectMethod4(Object p);
	
	<T extends String> void stringMethod1(T p);
	void stringMethod2(String p);
	void stringMethod3(V p);
	
	<T extends List> void listMethod1(T p);
	<T extends List<String>> void listMethod2(T p);
	<T extends List<?>> void listMethod3(T p);
	void listMethod4(List<?> p);
	void listMethod5(List p);
	void listMethod6(List<String> p);
	void listMethod7(List<? extends String> p);
	void listMethod8(List<? super String> p);
	<T extends List<? extends String>> void listMethod9(T p);
}
