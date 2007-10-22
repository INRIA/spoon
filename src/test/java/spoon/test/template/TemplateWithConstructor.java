package spoon.test.template;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtTypeReference;
import spoon.template.Local;
import spoon.template.Parameter;
import spoon.template.Template;

public class TemplateWithConstructor implements Template {

    @Parameter
    CtTypeReference<?> _Interf_;

    @Local
	public TemplateWithConstructor(CtTypeReference<?> interf) {
		super();
		_Interf_ = interf;
	}

	public TemplateWithConstructor() {
		super();
		System.out.println("new");
	}

	public TemplateWithConstructor(String arg) {
		super();
		System.out.println("new");
	}

	List<_Interf_> toBeInserted = new ArrayList<_Interf_>();

}

interface _Interf_ {}