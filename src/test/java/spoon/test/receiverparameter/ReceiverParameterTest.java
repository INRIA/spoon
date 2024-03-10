package spoon.test.receiverparameter;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
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
        List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
        assertEquals(1, receiverParams.size());
        CtReceiverParameter next1 = receiverParams.iterator().next();
        assertEquals("receiver.SimpleReceiverParameter", next1.getType().getQualifiedName());
        // TODO: toString
    }


    @ModelTest(
            value = "src/test/resources/receiver/InnerClassCtor.java"
    )
    void innerClassCtor(CtModel model) {
        CtType<?> targetType = model.getAllTypes().iterator().next();
        List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
        assertEquals(1, receiverParams.size());
        CtReceiverParameter next1 = receiverParams.iterator().next();
        assertEquals("receiver.SimpleReceiverParameter", next1.getType().getQualifiedName());
        CtMethod<?> ctMethod = targetType.getMethods().stream().filter(v -> v.getReceiverParameter() != null).findFirst().get();
        System.out.println(ctMethod.toString());
        //TODO fix name
    }
}
