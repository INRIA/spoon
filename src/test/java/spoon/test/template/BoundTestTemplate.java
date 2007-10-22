package spoon.test.template;

import java.util.Collection;

import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.StatementListTemplateParameter;
import spoon.template.Template;

public class BoundTestTemplate extends StatementListTemplateParameter implements
		Template {
	@Parameter
	public int _max_;

	@Local
	Collection<?> elements;

	public BoundTestTemplate(int _max_) {
		this._max_ = _max_;
	}

	public void statements() {
		if (elements.size() >= _max_) {
			throw new RuntimeException("stack is full");
		}
	}
}
