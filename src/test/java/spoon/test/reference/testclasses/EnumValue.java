package spoon.test.reference.testclasses;

public class EnumValue {

	public EnumValue() {
	}

	public <T extends Enum<T>> T asEnum() //StackOverflow when referenced
	{
		return null;
	}

	public Object unwrap() {
		return asEnum();
	}
}
