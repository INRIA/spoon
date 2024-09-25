package spoon.testing.assertions;
import java.util.Collection;
import org.assertj.core.api.AbstractCollectionAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.declaration.CtRecord;
import spoon.reflect.declaration.CtRecordComponent;
import spoon.reflect.reference.CtTypeReference;
public interface CtRecordAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtRecord> extends SpoonAssert<A, W> , CtClassAssertInterface<A, W> {
	default AbstractCollectionAssert<?, Collection<? extends CtTypeReference<?>>, CtTypeReference<?>, ?> getPermittedTypes() {
		return Assertions.assertThat(actual().getPermittedTypes());
	}

	default AbstractCollectionAssert<?, Collection<? extends CtRecordComponent>, CtRecordComponent, ?> getRecordComponents() {
		return Assertions.assertThat(actual().getRecordComponents());
	}
}
