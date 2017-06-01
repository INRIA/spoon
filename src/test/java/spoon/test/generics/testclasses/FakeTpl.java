package spoon.test.generics.testclasses;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * Created by urli on 01/06/2017.
 */
public interface FakeTpl<T extends CtElement> {
    T apply(CtType<?> targetType);
}
