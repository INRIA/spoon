package spoon.test.reference;

public class ReferencingClass {

	public ReferencingClass() {
		delegate =  new ReferencedClass();
	}
	
	public String name() {
		return delegate.name;
	}
	
	public int ID() {
		return delegate.ID;
	}
	
	private ReferencedClass delegate;
}
