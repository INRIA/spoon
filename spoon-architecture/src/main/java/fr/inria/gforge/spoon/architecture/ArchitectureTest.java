package fr.inria.gforge.spoon.architecture;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;

public class ArchitectureTest<T extends CtElement> {

	private Precondition<T> preCondition;
	private Constraint<? super T> constraint;

	private ArchitectureTest(Precondition<T> preCondition, Constraint<? super T> constraint) {
		this.preCondition = preCondition;
		this.constraint = constraint;
	}
	public static <T extends CtElement> ArchitectureTest<T> of(Precondition<T> preCondition, Constraint<? super T> constraint) {
		return new ArchitectureTest<>(preCondition, constraint);
	}
	public void runCheck(CtModel model) {
		preCondition.apply(model).stream().forEach(constraint::checkConstraint);
	}
}
