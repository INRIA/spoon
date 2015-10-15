package spoon.test.ctClass;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.TestUtils;
import spoon.test.ctClass.testclasses.Foo;

public class CtClassTest {

    @Test
    public void getConstructor() throws Exception {
        final Factory build = TestUtils.build(Foo.class);
        final CtClass<?> foo = (CtClass<?>) build.Type().get(Foo.class);

        Assert.assertEquals(3, foo.getConstructors().size());

        CtTypeReference<Object> typeString = build.Code().createCtTypeReference(String.class);
        CtConstructor<?> constructor = foo.getConstructor(typeString);
        Assert.assertEquals(typeString, constructor.getParameters().get(0).getType());

        CtArrayTypeReference<Object> typeStringArray = build.Core().createArrayTypeReference();
        typeStringArray.setComponentType(typeString);
        constructor = foo.getConstructor(typeStringArray);
        Assert.assertEquals(typeStringArray, constructor.getParameters().get(0).getType());

        CtArrayTypeReference<Object> typeStringArrayArray = build.Core().createArrayTypeReference();
        typeStringArrayArray.setComponentType(typeStringArray);
        constructor = foo.getConstructor(typeStringArrayArray);
        Assert.assertEquals(typeStringArrayArray, constructor.getParameters().get(0).getType());
    }
}
