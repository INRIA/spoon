package spoon.test.template.testclasses;

import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class EnumAccessTemplate extends ExtensionTemplate {

	public void method() throws Throwable {
		_AnEnum_._value_.name();
	}
	
	@Parameter("_AnEnum_")
	CtTypeReference<?> anEnum;
	
	@Parameter
	Enum _value_;

	@Local
	public EnumAccessTemplate(Enum enumValue, Factory factory) {
		this._value_ = enumValue;
		this.anEnum = factory.Type().createReference(enumValue.getClass()); 
	}
	
	@Local
	enum _AnEnum_ {
		@Parameter
		_value_
	}
}