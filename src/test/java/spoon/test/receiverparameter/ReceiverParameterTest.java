package spoon.test.receiverparameter;

import java.util.List;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.assertions.SpoonAssertions;
import spoon.testing.utils.ModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiverParameterTest {

	@ModelTest("src/test/resources/receiver/SimpleReceiverParameter.java")
	void simpleParameter(CtModel model) {
		// contract: receiver parameter is correctly parsed and can be accessed from the model
		CtType<?> targetType = model.getAllTypes().iterator().next();
		List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
		assertEquals(1, receiverParams.size());
		CtReceiverParameter receiverParam = receiverParams.iterator().next();
		SpoonAssertions.assertThat(receiverParam.getType()).extracting(CtTypeInformation::getQualifiedName).isEqualTo("receiver.SimpleReceiverParameter");
		SpoonAssertions.assertThat(receiverParam).extracting(CtElement::toString).isEqualTo("SimpleReceiverParameter this");

	}


	@ModelTest("src/test/resources/receiver/InnerClassCtor.java")
	void innerClassCtor(CtModel model) {
		// contract: constructor of inner class can have receiver parameter with their outer class type
		CtType<?> targetType = model.getAllTypes().iterator().next();
		List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
		assertEquals(1, receiverParams.size());
		CtReceiverParameter next1 = receiverParams.iterator().next();
		SpoonAssertions.assertThat(next1.getType()).extracting(CtTypeInformation::getQualifiedName).isEqualTo("receiver.Outer");
		CtConstructor<?> ctConstructor = targetType.getElements(new TypeFilter<>(CtConstructor.class)).stream().filter(v -> v.getReceiverParameter() != null).findFirst().get();
		SpoonAssertions.assertThat(ctConstructor).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter()).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter().getType()).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter()).extracting(CtElement::toString).isEqualTo("Outer Outer.this");
	}

	@ModelTest("src/test/resources/receiver/Outer.java")
	void innerClassInnerClass(CtModel model) {
		// contract: constructor of inner class which is an innerclass can have receiver parameter with their outer class type
		CtType<?> targetType = model.getAllTypes().iterator().next();
		List<CtReceiverParameter> receiverParams = targetType.getElements(new TypeFilter<>(CtReceiverParameter.class));
		assertEquals(1, receiverParams.size());
		CtConstructor<?> ctConstructor = targetType.getElements(new TypeFilter<>(CtConstructor.class)).stream().filter(v -> v.getReceiverParameter() != null).findFirst().get();
		SpoonAssertions.assertThat(ctConstructor).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter()).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter().getType()).isNotNull();
		SpoonAssertions.assertThat(ctConstructor.getReceiverParameter()).extracting(CtElement::toString).isEqualTo("Middle Middle.this");
	}
}
