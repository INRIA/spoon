package spoon.test.targeted.testclasses;

public class InternalSuperCall{

	public void methode(){
		InternalSuperCall.super.toString();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}

