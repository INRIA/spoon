package spoon.test.template.testclasses.inheritance;

import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.ExtensionTemplate;
import spoon.template.Parameter;

import java.io.Serializable;
import java.rmi.Remote;

public class InterfaceTemplate extends ExtensionTemplate implements Serializable, A, B {
	// interface templates supports TypeReference
	@Parameter
	public CtTypeReference A;

	@Parameter
	public Class B = Remote.class;

	private final Factory factory;

	public InterfaceTemplate(Factory factory) {
		this.factory = factory;
		A = getFactory().Type().createReference(Comparable.class);
	}

	@Override
	public Factory getFactory() {
		return factory;
	}
}
interface A{}
interface B{}