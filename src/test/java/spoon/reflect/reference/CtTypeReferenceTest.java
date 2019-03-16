package spoon.reflect.reference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.primitives.Primitives.allPrimitiveTypes;
import static com.google.common.primitives.Primitives.wrap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.junit.MockitoJUnit.rule;
import static spoon.testing.utils.Check.assertNotNull;

public class CtTypeReferenceTest {
    private final TypeFactory typeFactory = new TypeFactory();
    @Rule public MockitoRule mockito = rule();
    @Mock private Factory factory;
    @Mock private Environment environment;
    @Mock private FineModelChangeListener listener;
    @Mock private ClassLoader classLoader;

    @Before public void setUp() {
        when(factory.Type()).thenReturn(typeFactory);
        when(factory.getEnvironment()).thenReturn(environment);
        when(environment.getModelChangeListener()).thenReturn(listener);
        when(environment.getInputClassLoader()).thenReturn(classLoader);
    }

    @Test public void unbox() throws ClassNotFoundException {
        testBoxingFunction(CtTypeReferenceImpl::new, false);
    }

    @Test public void box() throws ClassNotFoundException {
        testBoxingFunction(CtTypeReferenceImpl::new, true);
    }

    private void testBoxingFunction(Supplier<CtTypeReference<?>> supplier, boolean box) throws ClassNotFoundException {
        Function<CtTypeReference<?>, CtTypeReference<?>> boxingFunction = box ?
            CtTypeReference::box :
            CtTypeReference::unbox;
        for (Class<?> primitiveType : allPrimitiveTypes()) {
            testBoxingFunction(supplier, primitiveType, box ? wrap(primitiveType) : primitiveType, false,
                               boxingFunction);
            testBoxingFunction(supplier, wrap(primitiveType), box ? wrap(primitiveType) : primitiveType, true,
                               boxingFunction);
        }
        testBoxingFunction(supplier, String.class, String.class, false, boxingFunction);
    }

    private void testBoxingFunction(Supplier<? extends CtTypeReference<?>> supplier, Class<?> inputClass,
                                    Class<?> expectedClass, boolean mockClassLoader,
                                    Function<CtTypeReference<?>, CtTypeReference<?>> boxingFunction)
        throws ClassNotFoundException {
        CtTypeReference<?> reference = supplier.get();
        reference.setFactory(factory);
        reference.setSimpleName(inputClass.getName());
        if (mockClassLoader) {
            when(classLoader.loadClass(inputClass.getName()))
                .thenAnswer((Answer<Object>) invocationOnMock -> inputClass);
        }

        CtTypeReference<?> result = boxingFunction.apply(reference);

        assertNotNull(result);
        assertEquals(expectedClass.getName(), result.getQualifiedName());
    }
}