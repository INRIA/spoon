package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModuleDirective;
interface CtModuleDirectiveAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModuleDirective> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {}