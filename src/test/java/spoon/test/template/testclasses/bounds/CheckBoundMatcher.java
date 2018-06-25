package spoon.test.template.testclasses.bounds;

import java.util.Collection;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.template.Parameter;
import spoon.template.TemplateParameter;

public class CheckBoundMatcher {
	
	public TemplateParameter<Collection<?>> _col_;
	public TemplateParameter<Integer> _x_;
	public TemplateParameter<Integer> _y_;
	public TemplateParameter<CtBlock> _block_;
	public TemplateParameter<CtStatement> _stmt_;


	public void matcher1() {
		if (_col_.S().size() > 10)
			throw new IndexOutOfBoundsException();
	}

	public void matcher2() {
		if (_col_.S().size() > 10)
			System.out.println();
	}

	public void matcher3() {
		if (_x_.S() > 10)
			throw new IndexOutOfBoundsException();
	}
	
	public void matcher4() {
		if (_x_.S() > _y_.S())
			throw new IndexOutOfBoundsException();
	}

	public void matcher5() {
		if (_x_.S() > _y_.S()) _block_.S();
	}

	public void matcher6() {
		if (_x_.S() > _y_.S()) { _stmt_.S(); }
	}
	
	public void matcher7() {
		if (_x_.S() == _x_.S()) _block_.S();
	}

	/** defines a matcher f* for named templates */
	@Parameter
	Object _w_;

}