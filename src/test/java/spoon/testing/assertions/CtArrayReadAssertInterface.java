package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayRead;
public interface CtArrayReadAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayRead<?>> extends SpoonAssert<A, W> , CtArrayAccessAssertInterface<A, W> {}
