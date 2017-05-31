package spoon.test.template.testclasses.constructors;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class TemplateWithConstructor extends ExtensionTemplate {

    @Parameter
    CtTypeReference<?> _Interf_;

    @Local
	public TemplateWithConstructor(CtTypeReference<?> interf) {
		super();
		_Interf_ = interf;
	}

	public TemplateWithConstructor(String arg) {
		super();
		System.out.println("new");
	}

	public TemplateWithConstructor(int arg) {
		super();
		System.out.println("new");
	}

	List<_Interf_> toBeInserted = new ArrayList<_Interf_>();

}

interface _Interf_ {}