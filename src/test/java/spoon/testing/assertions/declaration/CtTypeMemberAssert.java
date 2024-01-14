package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtTypeMember;
public class CtTypeMemberAssert extends AbstractAssert<CtTypeMemberAssert, CtTypeMember> {
	public CtTypeMemberAssert(CtTypeMember actual) {
		super(actual, CtTypeMemberAssert.class);
	}
}
