package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.declaration.CtInterface;
interface CtInterfaceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtInterface<?>> extends CtSealableAssertInterface<A, W> , SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtTypeAssertInterface<A, W> {
    default AbstractStringAssert<?> getLabel() {
        return org.assertj.core.api.Assertions.assertThat(actual().getLabel());
    }
}