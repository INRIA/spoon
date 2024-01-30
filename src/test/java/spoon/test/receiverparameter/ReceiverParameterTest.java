package spoon.test.receiverparameter;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.ModelTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiverParameterTest {

    @ModelTest(
        value = "src/test/resources/receiver/SimpleReceiverParameter.java"
    )
    void simpleParameter(CtModel model) {
        CtType<?> targetType = model.getAllTypes().iterator().next();
        targetType.getMethods().forEach(System.out::println);
        List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
        assertEquals(1, receiverParams.size());
        CtReceiverParameter next1 = receiverParams.iterator().next();
        assertEquals("this", next1.getSimpleName());
        assertEquals("spoon.test.receiverparameter.SimpleReceiverParameter", next1.getType().getQualifiedName());
        System.out.println(targetType);
    }
}
