package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
public interface CtExecutableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtExecutable<?>> extends CtBodyHolderAssertInterface<A, W> , SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtTypedElementAssertInterface<A, W> {
	default CtBlockAssertInterface<?, ?> getBody() {
		return SpoonAssertions.assertThat(actual().getBody());
	}

	default ListAssert<CtParameter<?>> getParameters() {
		return Assertions.assertThat(actual().getParameters());
	}

	default CtReceiverParameterAssertInterface<?, ?> getReceiverParameter() {
		return SpoonAssertions.assertThat(actual().getReceiverParameter());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<? extends Throwable>>, CtTypeReference<? extends Throwable>, ?> getThrownTypes() {
		return Assertions.assertThat(actual().getThrownTypes());
	}
}
