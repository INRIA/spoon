package spoon.test.template.testclasses;

import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class InvocationTemplate extends ExtensionTemplate {

	IFace iface;
	
	void invoke() {
		iface.$method$();
	}
	
	@Local
	public InvocationTemplate(CtTypeReference<?> ifaceType, String methodName) {
		this.ifaceType = ifaceType;
		this.methodName = methodName;
	}

	@Parameter("IFace")
	CtTypeReference<?> ifaceType;
	
	@Parameter("$method$")
	String methodName;

	
	
	@Local
	interface IFace {
		void $method$();
	}
}
