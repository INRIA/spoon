package spoon.architecture;

public class ArchitectureTest<T, M> {

	private IPrecondition<T, M> preCondition;
	private Checkable<? super T> constraint;

	private ArchitectureTest(IPrecondition<T, M> preCondition, Checkable<? super T> constraint) {
		this.preCondition = preCondition;
		this.constraint = constraint;
	}
	public static <T, M> ArchitectureTest<T, M> of(IPrecondition<T, M> preCondition, Checkable<? super T> constraint) {
		return new ArchitectureTest<>(preCondition, constraint);
	}
	public void runCheck(M model) {
		preCondition.apply(model).stream().forEach(constraint::checkConstraint);
	}
}
