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

	public static Pattern createPattern(Factory factory, boolean matchBody) {
		CtType<?> type = factory.Type().get(MatchModifiers.class);
		return PatternBuilder.create(new PatternBuilderHelper(type).setTypeMember("matcher1").getPatternElements())
			.configureParameters(pb -> {
				pb.parameter("modifiers").byRole(new TypeFilter(CtMethod.class), CtRole.MODIFIER);
				pb.parameter("methodName").byString("matcher1");
				pb.parameter("parameters").byRole(new TypeFilter(CtMethod.class), CtRole.PARAMETER);
				if (matchBody) {
					pb.parameter("statements").byRole(new TypeFilter(CtBlock.class), CtRole.STATEMENT);
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
