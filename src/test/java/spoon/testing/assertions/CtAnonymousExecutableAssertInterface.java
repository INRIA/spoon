package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnonymousExecutable;
interface CtAnonymousExecutableAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnonymousExecutable> extends SpoonAssert<A, W> , CtExecutableAssertInterface<A, W> , CtTypeMemberAssertInterface<A, W> {}
