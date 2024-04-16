package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtTypeParameter;
public interface CtFormalTypeDeclarerAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFormalTypeDeclarer> extends SpoonAssert<A, W> , CtTypeMemberAssertInterface<A, W> {
	default ListAssert<CtTypeParameter> getFormalCtTypeParameters() {
		return Assertions.assertThat(actual().getFormalCtTypeParameters());
	}
}
