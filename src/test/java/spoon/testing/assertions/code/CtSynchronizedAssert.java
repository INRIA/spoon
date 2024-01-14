package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSynchronized;
public class CtSynchronizedAssert extends AbstractAssert<CtSynchronizedAssert, CtSynchronized> {
	public CtSynchronizedAssert(CtSynchronized actual) {
		super(actual, CtSynchronizedAssert.class);
	}
}
