package spoon.reflect.declaration.testclasses;

public class Subclass extends ExtendsObject implements Subinterface {
    @Override
    public int compareTo(Object o) {
        return 0;
    }

	@Override
	public void foo() {
		throw new UnsupportedOperationException();
	}
}
