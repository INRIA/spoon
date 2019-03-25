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

    /**
     * If this represents a wrapped type, returns a {@link CtTypeReference} for the unwrapped type,
     * or return this in other cases.
     */
    @Test public void unbox() throws ClassNotFoundException {
        testBoxingFunction(CtTypeReferenceImpl::new, false);
    }

    /**
     * If this represents an unwrapped type, returns a {@link CtTypeReference} for the wrapped type,
     * or return this in other cases.
     */
    @Test public void box() throws ClassNotFoundException {
        testBoxingFunction(CtTypeReferenceImpl::new, true);
    }

    private void testBoxingFunction(Supplier<CtTypeReference<?>> supplier, boolean box) throws ClassNotFoundException {
        Function<CtTypeReference<?>, CtTypeReference<?>> boxingFunction = box ?
            CtTypeReference::box :
            CtTypeReference::unbox;
        for (Class<?> primitiveType : allPrimitiveTypes()) {
            //contract: box(Primitive) -> Boxed, unbox(Primitive) -> Primitive
            testBoxingFunction(supplier, primitiveType, box ? wrap(primitiveType) : primitiveType, false,
                               boxingFunction);
            //contract: box(Boxed) -> Boxed, unbox(Boxed) -> Primitive
            testBoxingFunction(supplier, wrap(primitiveType), box ? wrap(primitiveType) : primitiveType, true,
                               boxingFunction);
        }
        //contract: box(Object) -> Object, unbox(Object) -> object
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

        //contract: boxing/unboxing do not yield null
        assertNotNull(result);

        //contract: box/unbox returns a reference toward the expected type
        assertEquals(expectedClass.getName(), result.getQualifiedName());
    }
}