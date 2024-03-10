package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtPattern;
public interface CtPatternAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtPattern> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {}
