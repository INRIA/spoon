package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.TypeFilter;

public class MatchModifiers {


	public void matcher1() {
	}
	
	public static void publicStaticMethod() {
	}
	
	void packageProtectedMethodWithParam(int a, MatchModifiers me) {
	}

	private void withBody() {
		this.getClass();
		System.out.println();
	}
	
	int noMatchBecauseReturnsInt() {return 0;}
}
