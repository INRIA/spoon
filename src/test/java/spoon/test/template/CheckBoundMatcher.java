package spoon.test.template;

import spoon.template.TemplateParameter;

import java.util.Collection;

public class CheckBoundMatcher {
	
	public TemplateParameter<Collection<?>> _col_;

	public void matcher1() {
		if (_col_.S().size() > 10)
			throw new IndexOutOfBoundsException();
	}

	public void matcher2() {
		if (_col_.S().size() > 10)
			System.out.println();
	}

}