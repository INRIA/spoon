package spoon.test.fieldaccesses;

public class InternalSuperCall{

	public void methode(){
		InternalSuperCall.super.toString();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}

