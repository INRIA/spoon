package spoon.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.test.filters.testclasses.Foo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static spoon.testing.utils.ModelUtils.build;

class NameFilterTest {

    private Factory factory;

    @BeforeEach
    public void setup() throws Exception {
        factory = build(Foo.class);
    }

    @Test
    public void testNameFilter() throws Exception {
        // contract: legacy NameFilter is tested and works
        CtClass<?> foo = factory.Package().get("spoon.test.filters.testclasses").getType("Foo");
        assertEquals("Foo", foo.getSimpleName());
        List<CtNamedElement> elements = foo.getElements(new NameFilter<>("i"));
        assertEquals(1, elements.size());
    }

    @Test()
    public void testNameFilterThrowsException() {
        CtClass<?> foo = factory.Package().get("spoon.test.filters.testclasses").getType("Foo");
        assertThrows(IllegalArgumentException.class, () -> foo.getElements(new NameFilter<>(null)));
    }
}