package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtModule;
public class CtModuleAssert extends AbstractAssert<CtModuleAssert, CtModule> {
	public CtModuleAssert(CtModule actual) {
		super(actual, CtModuleAssert.class);
	}
}
