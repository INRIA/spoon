package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.AsyncFunction;
import spoon.reflect.visitor.chain.Consumer;

public class Scann extends CtScanner implements AsyncFunction<CtElement, CtElement> {

	private Consumer<CtElement> output;

	public Scann() {
	}

	@Override
	public void apply(CtElement input, Consumer<CtElement> output) {
		this.output = output;
		scan(input);
	}

	@Override
	public void scan(CtElement element) {
		output.accept(element);
		super.scan(element);
	}
}
