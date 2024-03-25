package spoon.test.receiverparameter;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.assertions.SpoonAssertions;
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
        CtReceiverParameter receiverParam = receiverParams.iterator().next();
        assertEquals("receiver.SimpleReceiverParameter", receiverParam.getType().getQualifiedName());
        SpoonAssertions.assertThat(receiverParam.getType()).extracting(CtTypeInformation::getQualifiedName).isEqualTo("receiver.SimpleReceiverParameter");
    }


    @ModelTest(
            value = "src/test/resources/receiver/InnerClassCtor.java"
    )
    void innerClassCtor(CtModel model) {
        CtType<?> targetType = model.getAllTypes().iterator().next();
        List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
        assertEquals(1, receiverParams.size());
        CtReceiverParameter next1 = receiverParams.iterator().next();
        assertEquals("receiver.Outer", next1.getType().getQualifiedName());
        CtConstructor<?> ctConstructor = targetType.getElements(new TypeFilter<>(CtConstructor.class)).stream().filter(v -> v.getReceiverParameter() != null).findFirst().get();
        SpoonAssertions.assertThat(ctConstructor).isNotNull();
        SpoonAssertions.assertThat(ctConstructor.getReceiverParameter()).isNotNull();
    }
}
