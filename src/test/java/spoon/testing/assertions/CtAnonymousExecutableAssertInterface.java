package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnonymousExecutable;
public interface CtAnonymousExecutableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnonymousExecutable> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtTypeMemberAssertInterface<A, W> {
	default CtReceiverParameterAssertInterface<?, ?> getReceiverParameter() {
		return SpoonAssertions.assertThat(actual().getReceiverParameter());
	}
}
