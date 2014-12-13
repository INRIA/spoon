package spoon.test.template;

import spoon.reflect.declaration.CtField;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class ArrayResizeTemplate extends ExtensionTemplate {

	@Parameter
	int _poolSizeIncrement_;

	@Parameter
	static int _staticPoolSizeIncrement_;

	@Parameter("_field_")
	String __field_;

	@Parameter()
	Class<?> _Type_;

	@Local
	public ArrayResizeTemplate(CtField<?> field, int inc) {
		__field_ = field.getSimpleName();
		_Type_ = field.getType().getActualClass();
		_poolSizeIncrement_ = inc;
		_TargetType_ = field.getDeclaringType().getActualClass();
	}

	int poolSize_ = 100;

	@Local
	_Type_[] _field_;

	public void resize__field_() {
		_Type_[] tmp = new _Type_[poolSize_ + _poolSizeIncrement_];
		System.arraycopy(_field_, 0, tmp, 0, poolSize_);
		_field_ = tmp;
	}

	@Parameter
	Class<?> _TargetType_;

	static _TargetType_[] instances = new _TargetType_[_staticPoolSizeIncrement_];

	static _TargetType_[][][] instances3 = new _TargetType_[_staticPoolSizeIncrement_][][];

}

interface _Type_ {
}

interface _TargetType_ {
}