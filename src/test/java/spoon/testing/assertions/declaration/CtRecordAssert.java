package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtRecord;
public class CtRecordAssert extends AbstractAssert<CtRecordAssert, CtRecord> {
	public CtRecordAssert(CtRecord actual) {
		super(actual, CtRecordAssert.class);
	}
}
