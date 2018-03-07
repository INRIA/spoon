package spoon.test.template.testclasses.match;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.TypeFilter;

public class MatchModifiers {

	public static Pattern createPattern(Factory factory, boolean matchBody) {
		return PatternBuilder.create(factory, MatchModifiers.class, tmb -> tmb.setTypeMember("matcher1"))
			.configureParameters(pb -> {
				pb.parameter("modifiers").attributeOfElementByFilter(CtRole.MODIFIER, new TypeFilter(CtMethod.class));
				pb.parameter("methodName").byString("matcher1");
				pb.parameter("parameters").attributeOfElementByFilter(CtRole.PARAMETER, new TypeFilter(CtMethod.class));
				if (matchBody) {
					pb.parameter("statements").attributeOfElementByFilter(CtRole.STATEMENT, new TypeFilter(CtBlock.class));
				}
			})
			.build();
	}

	
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
