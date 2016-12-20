package spoon.test.template;


import spoon.template.Parameter;

public class SubTemplate extends SuperTemplate {

	public void toBeOverriden() {
		super.toBeOverriden();
	}

	@Parameter
	public void ignoredMethod(){}

	class InnerClass{}
}
