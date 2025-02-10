package spoon.support.compiler.jdt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferenceBuilderTest {
    private static ReferenceBuilder referenceBuilder;


    @BeforeAll
    public static void setUp() {
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();
        JDTTreeBuilder treeBuilder = new JDTTreeBuilder(factory);
        referenceBuilder = treeBuilder.references;
    }

    @Test
    public void testGetTypeReferenceSimpleCase() {
        CtTypeReference<Object> typeReference = referenceBuilder.getTypeReference("SomeType<Integer, Double, String>");
        List<CtTypeReference<?>> typeArgList = typeReference.getActualTypeArguments();

        // Assertions about main type
        assertEquals("SomeType", typeReference.getSimpleName());
        assertEquals(3, typeArgList.size());
        // Assertions about first type-argument
        CtTypeReference<?> firstType = typeArgList.get(0);
        assertEquals("Integer", firstType.getSimpleName());
        assertEquals(0, firstType.getActualTypeArguments().size());
        // Assertions about second type-argument
        CtTypeReference<?> secondType = typeArgList.get(1);
        assertEquals("Double", secondType.getSimpleName());
        assertEquals(0, secondType.getActualTypeArguments().size());
        // Assertions about third type-argument
        CtTypeReference<?> thirdType = typeArgList.get(2);
        assertEquals("String", thirdType.getSimpleName());
        assertEquals(0, thirdType.getActualTypeArguments().size());
    }

    @Test
    public void testGetTypeReferenceComplexGenericsCase() {
        CtTypeReference<?> typeReference = referenceBuilder
                .getTypeReference("SomeType<Integer, NestedFirst<Double, String>, Boolean, NestedSecond<Integer, MoreNested<Double>>>");
        List<CtTypeReference<?>> typeArgList = typeReference.getActualTypeArguments();

        // Assertions about main type
        assertEquals("SomeType", typeReference.getSimpleName());
        assertEquals(4, typeArgList.size());
        // Assertions about first type-argument
        CtTypeReference<?> firstType = typeArgList.get(0);
        assertEquals("Integer", firstType.getSimpleName());
        assertEquals(0, firstType.getActualTypeArguments().size());
        // Assertions about second type-argument (and its nested types)
        CtTypeReference<?> secondType = typeArgList.get(1);
        List<CtTypeReference<?>> secondTypeArgList = secondType.getActualTypeArguments();
        assertEquals("NestedFirst", secondType.getSimpleName());
        assertEquals(2, secondTypeArgList.size());
        assertEquals("Double", secondTypeArgList.get(0).getSimpleName());
        assertEquals(0, secondTypeArgList.get(0).getActualTypeArguments().size());
        assertEquals("String", secondTypeArgList.get(1).getSimpleName());
        assertEquals(0, secondTypeArgList.get(1).getActualTypeArguments().size());
        // Assertions about third type-argument
        CtTypeReference<?> thirdType = typeArgList.get(2);
        assertEquals("Boolean", thirdType.getSimpleName());
        assertEquals(0, thirdType.getActualTypeArguments().size());
        // Assertions about fourth type-argument (and its nested types)
        CtTypeReference<?> fourthType = typeArgList.get(3);
        List<CtTypeReference<?>> fourthTypeArgList = fourthType.getActualTypeArguments();
        assertEquals("NestedSecond", fourthType.getSimpleName());
        assertEquals(2, fourthTypeArgList.size());
        assertEquals("Integer", fourthTypeArgList.get(0).getSimpleName());
        assertEquals(0, fourthTypeArgList.get(0).getActualTypeArguments().size());
        CtTypeReference<?> fourthTypeMoreNested = fourthTypeArgList.get(1);
        assertEquals("MoreNested", fourthTypeMoreNested.getSimpleName());
        assertEquals(1, fourthTypeMoreNested.getActualTypeArguments().size());
        assertEquals("Double", fourthTypeMoreNested.getActualTypeArguments().get(0).getSimpleName());
        assertEquals(0 , fourthTypeMoreNested.getActualTypeArguments().get(0).getActualTypeArguments().size());
    }

    @Test
    public void testGetTypeReferenceWildcardWithTypeArgsCase() {
        CtTypeReference<?> typeReference = referenceBuilder.getTypeReference("?<Integer, String>");

        // Assertions
        assertEquals("?", typeReference.getSimpleName());
        assertEquals(0, typeReference.getActualTypeArguments().size());
    }

    @Test
    public void testGetTypeReferenceSimpleWildcardCase() {
        CtTypeReference<?> typeReference = referenceBuilder.getTypeReference("?");

        // Assertions
        assertEquals("?", typeReference.getSimpleName());
        assertEquals(0, typeReference.getActualTypeArguments().size());
    }

    @Test
    public void testGetTypeReferenceSimpleTypeCase() {
        CtTypeReference<?> typeReference = referenceBuilder.getTypeReference("Integer");

        // Assertions
        assertEquals("Integer", typeReference.getSimpleName());
        assertEquals(0, typeReference.getActualTypeArguments().size());
    }
}
