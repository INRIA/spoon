package spoon.test.generics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Tree<V extends Serializable & Comparable<V>> {
	
	V node() { return null; }
	<T> T node2() { return null; }
	<T extends Tree<V> & Comparable<T>> T node3() { 
		List<V> l=new ArrayList<V>();
		if(l==null);
		return null; }
	<T extends Tree<V> & Comparable<T>> T node4() { return null; }
	
}