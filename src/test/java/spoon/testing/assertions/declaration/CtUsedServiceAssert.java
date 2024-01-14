package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtUsedService;
public class CtUsedServiceAssert extends AbstractAssert<CtUsedServiceAssert, CtUsedService> {
	public CtUsedServiceAssert(CtUsedService actual) {
		super(actual, CtUsedServiceAssert.class);
	}
}
