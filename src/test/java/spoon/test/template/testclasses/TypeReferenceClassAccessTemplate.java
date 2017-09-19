package spoon.test.template.testclasses;

import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Local;
import spoon.template.Parameter;

public class TypeReferenceClassAccessTemplate extends ExtensionTemplate {
	Object o;

	$Type$ someMethod($Type$ param) {
		o = $Type$.out;
		$Type$ ret = new $Type$();
		return ret;
	}
	
	@Local
	public TypeReferenceClassAccessTemplate(CtTypeReference<?> typeRef) {
		this.typeRef = typeRef;
	}
	
	@Parameter("$Type$")
	CtTypeReference<?> typeRef;

	@Local
	static class $Type$ {
		static final String out = "";
		static long currentTimeMillis(){
			return 0;
		}
	}

	public static class Example<T> {
		static final String out = "";
		static long currentTimeMillis(){
			return 0;
		}
	}
}
