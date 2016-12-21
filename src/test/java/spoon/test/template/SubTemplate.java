package spoon.test.template;


import spoon.reflect.declaration.CtParameter;
import spoon.template.Parameter;

import java.util.List;

public class SubTemplate extends SuperTemplate {

	public void toBeOverriden() {
		super.toBeOverriden();
	}

	public void methodWithTemplatedParameters(Object params) {
		super.toBeOverriden();
	}

	@Parameter
	public List<CtParameter> params;

	@Parameter
	public void ignoredMethod(){}

	class InnerClass{}
}
