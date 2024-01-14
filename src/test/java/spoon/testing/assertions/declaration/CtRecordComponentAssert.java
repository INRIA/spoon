package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtRecordComponent;
public class CtRecordComponentAssert extends AbstractAssert<CtRecordComponentAssert, CtRecordComponent> {
	public CtRecordComponentAssert(CtRecordComponent actual) {
		super(actual, CtRecordComponentAssert.class);
	}
}
