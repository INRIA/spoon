package spoon.test.limits;

import spoon.test.limits.utils.ContainInternalClass;

public class StaticConstructorAccessOnInstance {

	ContainInternalClass test = new ContainInternalClass();

	public void methode() {
		ContainInternalClass.InternalClass testBis = test.new InternalClass();
	}
}
