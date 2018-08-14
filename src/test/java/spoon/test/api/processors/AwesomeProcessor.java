package spoon.test.api.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.test.api.testclasses.Bar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AwesomeProcessor extends AbstractProcessor<CtClass<Bar>> {
	final List<CtClass<Bar>> elements = new ArrayList<>();

	@Override
	public void process(CtClass<Bar> element) {
		// Creates new elements.
		final CtMethod prepareMojito = element.getMethodsByName("doSomething").get(0);
		prepareMojito.setSimpleName("prepareMojito");
		prepareMojito.setType(getFactory().Type().VOID_PRIMITIVE);
		final CtBlock<Object> block = getFactory().Core().createBlock();
		block.addStatement(
				getFactory().Code()
							.createCodeSnippetStatement("System.out.println(\"Prepare mojito\")"));
		prepareMojito.setBody(block);
		final CtMethod makeMojito = prepareMojito.clone();
		makeMojito.setSimpleName("makeMojito");
		final CtBlock<Object> blockMake = getFactory().Core().createBlock();
		blockMake.addStatement(
				getFactory().Code()
							.createCodeSnippetStatement("System.out.println(\"Make mojito!\")"));
		makeMojito.setBody(blockMake);

		// Applies transformation.
		element.addMethod(makeMojito);

		elements.add(element);
	}

	public List<CtClass<Bar>> getElements() {
		return Collections.unmodifiableList(elements);
	}
}
