package spoon.test.src;

import java.util.List;
import java.util.Vector;

import spoon.test.annotation.Bound;

@Bound(max = 5)
public class Stack<T> {

	List<T> elements = new Vector<T>();

	public void push(T element) {
		elements.add(0, element);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public T pop() {
		return elements.remove(0);
	}
	
	public static void main(String[] args) {
		Stack<Stack<?>> s=new Stack<Stack<?>>();
		for(int i=0;i<10;i++) {
			s.push(s);
			System.out.println(s.elements);
		}
	}
}
