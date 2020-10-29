package spoon.architecture;

/**
 * This defines an architecture test. An architecture test is a rule the source code should fullfil. It consists of 2 parts, precondition and constraint.
 * In the precondition meta model elements are selected and in the constraint the elements must hold a condition. For creating instances of this use {@link #of(IPrecondition, Checkable)}.
 * @param <T>  is the element type, that gets checked.
 * @param <M>  is the meta model type e.g. {@code CtModel} if you use the spoon-meta-model.
 */
public class ArchitectureTest<T, M> {

	private IPrecondition<T, M> preCondition;
	private Checkable<? super T> constraint;

	private ArchitectureTest(IPrecondition<T, M> preCondition, Checkable<? super T> constraint) {
		this.preCondition = preCondition;
		this.constraint = constraint;
	}
	/**
	 * Creates an architecture test of the given precondition and checkable. Both parameter mustn't be null.
	 * @param <T> is the element type, that gets checked.
	 * @param <M> is the meta model type e.g. {@code CtModel} if you use the spoon-meta-model.
	 * @param preCondition  is the element selector.
	 * @param constraint  a condition the selected elements must hold.
	 * @return  an architecture test.
	 */
	public static <T, M> ArchitectureTest<T, M> of(IPrecondition<T, M> preCondition, Checkable<? super T> constraint) {
		return new ArchitectureTest<>(preCondition, constraint);
	}
	/**
	 * Checks the rule for a given model. Checking a rule is filtering with {@code IPrecondition} and checking with {@code Checkable}.
	 * @param model  the model elements get selected from.
	 */
	public void runCheck(M model) {
		preCondition.apply(model).stream().forEach(constraint::checkConstraint);
	}
}
