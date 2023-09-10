package spoon.reflect.reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.support.compiler.VirtualFile;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.testing.utils.GitHubIssue;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.primitives.Primitives.allPrimitiveTypes;
import static com.google.common.primitives.Primitives.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.Check.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CtTypeReferenceTest {
    private final TypeFactory typeFactory = new TypeFactory();

    @Mock private Factory factory;
    @Mock private Environment environment;
    @Mock private FineModelChangeListener listener;
    @Mock private ClassLoader classLoader;

    @BeforeEach public void setUp() {
        Mockito.lenient().when(factory.Type()).thenReturn(typeFactory);
        Mockito.lenient().when(factory.getEnvironment()).thenReturn(environment);
        Mockito.lenient().when(environment.getModelChangeListener()).thenReturn(listener);
        Mockito.lenient().when(environment.getInputClassLoader()).thenReturn(classLoader);
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
            Mockito.lenient().when(classLoader.loadClass(inputClass.getName()))
                .thenAnswer((Answer<Object>) invocationOnMock -> inputClass);
        }

        CtTypeReference<?> result = boxingFunction.apply(reference);

        //contract: boxing/unboxing do not yield null
        assertNotNull(result);

        //contract: box/unbox returns a reference toward the expected type
        assertEquals(expectedClass.getName(), result.getQualifiedName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "byte,              2, byte[][]",
        "byte,              3, byte[][][]",
        "java.lang.String,  3, String[][][]",
        "char,              1, char[]",
        "boolean,           1, boolean[]",
        "byte,              1, byte[]",
        "short,             1, short[]",
        "int,               1, int[]",
        "long,              1, long[]",
        "float,             1, float[]",
        "double,            1, double[]",
    })
    void testGetActualClassForArray(String className, int arrayDepth, String expected) {
        // contract: "getActualClass" should return proper classes for multi-dimensional arrays
        Factory factory = new Launcher().getFactory();
        CtArrayTypeReference<?> reference = factory.createArrayReference(
            factory.createReference(className),
            arrayDepth
        );
        assertEquals(
            expected,
            reference.getActualClass().getSimpleName()
        );
    }

    @Test
    void testImplicitInnerClassIsNotQualified() {
        // contract: If the source code contains no explicit outer class reference, so does the model
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.addInputResource(new VirtualFile(
            "class TestInnerClass {\n" +
            "    static class A { static class B {} }\n" +
            "\n" +
            "    A a = new A();\n" +
            "    A.B b = new A.B();\n" +
            "}\n"
        ));

        CtType<?> innerClass = launcher.buildModel().getAllTypes().iterator().next();
        CtField<?> a = innerClass.getField("a");
        assertEquals("A a = new A();", a.toString());
        assertTrue(a.getType().getDeclaringType().isImplicit(), "Declaring access should be implicit");

        CtField<?> b = innerClass.getField("b");
        assertEquals("A.B b = new A.B();", b.toString());
        assertTrue(
            b.getType().getDeclaringType().getDeclaringType().isImplicit(),
            "Declaring access should be implicit"
        );
    }

    @Test
    void testAliasAccessedClassIsNotQualified() {
        // contract: If the source code contains no explicit outer class reference, so does the model
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.addInputResource(new VirtualFile(
	        "class TestInnerClass {\n" +
	        "	static class A { static class B {} }\n" +
	        "}\n",
            "TestInnerClass.java"
        ));
        launcher.addInputResource(new VirtualFile(
            "class Inheritor extends TestInnerClass.A {\n" +
            "  public void foo(B b) {}\n" +
            "}\n",
            "Inheritor.java"
        ));

        launcher.buildModel();
        CtType<?> inheritor = launcher.getFactory().Type().get("Inheritor");
        CtParameter<?> fooParam = inheritor.getMethodsByName("foo").get(0).getParameters().get(0);

        // Not qualified
        assertEquals("B b", fooParam.toString());
    }

    @Test
    @GitHubIssue(issueNumber = -1, fixed = false)
    void testAliasAccessedClassIsNotQualified2() {
        // contract: If the source code contains no explicit outer class reference, so does the model
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(17);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.addInputResource(new VirtualFile(
	        "class TestInnerClass {\n" +
	        "	static class A { static class B {} }\n" +
	        "}\n",
            "TestInnerClass.java"
        ));
        launcher.addInputResource(new VirtualFile(
            "class Inheritor extends TestInnerClass.A {\n" +
            "  public void foo(Inheritor.B b) {}\n" +
            "}\n",
            "Inheritor.java"
        ));

        launcher.buildModel();
        CtType<?> inheritor = launcher.getFactory().Type().get("Inheritor");
        CtParameter<?> fooParam = inheritor.getMethodsByName("foo").get(0).getParameters().get(0);

        assertEquals("Inheritor.B b", fooParam.toString());
    }

}
