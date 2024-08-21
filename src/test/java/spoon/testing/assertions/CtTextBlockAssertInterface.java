package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtTextBlock;
public interface CtTextBlockAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTextBlock> extends SpoonAssert<A, W> , CtLiteralAssertInterface<A, W> {}
