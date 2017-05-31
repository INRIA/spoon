package spoon.test.template.testclasses.bounds;

import java.util.Collection;

import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.template.StatementTemplate;
import spoon.template.TemplateParameter;

public class CheckBoundTemplate extends StatementTemplate { 		
	public TemplateParameter<Collection<?>> _col_;
	
	@Override
	public void statement() {
		if (_col_.S().size() > 10)
			throw new IndexOutOfBoundsException();
	}

	/**
	 * Sets _col_ to be replaced by a reference to variable (a local variable, a
	 * field, a parameter
	 */
	public void setVariable(CtVariable<?> var) {
		CtVariableRead<Collection<?>> va = var.getFactory().Core().createVariableRead();
		va.setVariable((CtVariableReference<Collection<?>>) var.getReference());
		_col_ = va;
	}
}