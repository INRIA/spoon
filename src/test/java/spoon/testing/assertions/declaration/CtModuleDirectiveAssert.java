package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtModuleDirective;
public class CtModuleDirectiveAssert extends AbstractAssert<CtModuleDirectiveAssert, CtModuleDirective> {
	public CtModuleDirectiveAssert(CtModuleDirective actual) {
		super(actual, CtModuleDirectiveAssert.class);
	}
}
