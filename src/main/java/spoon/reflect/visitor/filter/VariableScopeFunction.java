package spoon.reflect.visitor.filter;


import spoon.SpoonException;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * The mapping function, accepting {@link CtVariable}
 * <ul>
 * <li>CtLocalVariable - local variable declared in body
 * <li>CtField - member field of an type
 * <li>CtParameter - method parameter
 * <li>CtCatchVariable - try - catch variable
 * </ul>
 * and returning all the CtElements, which exists in visibility scope of this variable.
 */
public class VariableScopeFunction implements CtConsumableFunction<CtVariable<?>> {

	protected final Visitor visitor = new Visitor();
	protected CtConsumer<Object> outputConsumer;

	@Override
	public void apply(CtVariable<?> variable, CtConsumer<Object> outputConsumer) {
		this.outputConsumer = outputConsumer;
		variable.accept(visitor);
	}

	private static final FieldScopeFunction fieldScopeFunction = new FieldScopeFunction();
	private static final LocalVariableScopeFunction localVariableScopeFunction = new LocalVariableScopeFunction();
	private static final ParameterScopeFunction parameterScopeFunction = new ParameterScopeFunction();
	private static final CatchVariableScopeFunction catchVariableScopeFunction = new CatchVariableScopeFunction();

	protected class Visitor extends CtScanner {
		@Override
		protected void enter(CtElement e) {
			throw new SpoonException("Unsupported variable of type " + e.getClass().getName());
		}
		/**
		 * calls outputConsumer for each reference of the field
		 */
		@Override
		public <T> void visitCtField(CtField<T> field) {
			fieldScopeFunction.apply(field, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the local variable
		 */
		@Override
		public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
			localVariableScopeFunction.apply(localVariable, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the parameter
		 */
		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			parameterScopeFunction.apply(parameter, outputConsumer);
		}

		/**
		 * calls outputConsumer for each reference of the catch variable
		 */
		@Override
		public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
			catchVariableScopeFunction.apply(catchVariable, outputConsumer);
		}
	}
}
