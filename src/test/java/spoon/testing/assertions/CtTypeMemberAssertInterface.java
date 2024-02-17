package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypeMember;
public interface CtTypeMemberAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeMember> extends SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtModifiableAssertInterface<A, W> {}
