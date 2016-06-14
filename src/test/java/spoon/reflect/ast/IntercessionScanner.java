package spoon.reflect.ast;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class IntercessionScanner extends CtScanner {
	protected final Factory factory;
	protected final List<CtTypeReference<?>> COLLECTIONS;
	protected final CtTypeReference<CtElement> CTELEMENT_REFERENCE;

	public IntercessionScanner(Factory factory) {
		this.factory = factory;
		COLLECTIONS = Arrays.asList( //
				factory.Type().createReference(Collection.class), //
				factory.Type().createReference(List.class), //
				factory.Type().createReference(Set.class) //
		);
		CTELEMENT_REFERENCE = factory.Type().createReference(CtElement.class);
	}

	protected abstract boolean isToBeProcessed(CtMethod<?> candidate);

	protected abstract void process(CtMethod<?> element);

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		if (isToBeProcessed(m)) {
			process(m);
		}
		super.visitCtMethod(m);
	}

	protected boolean avoidThrowUnsupportedOperationException(CtMethod<?> candidate) {
		if (candidate.getBody().getStatements().size() != 1) {
			return true;
		}
		if (!(candidate.getBody().getStatement(0) instanceof CtThrow)) {
			return true;
		}
		CtThrow ctThrow = candidate.getBody().getStatement(0);
		if (!(ctThrow.getThrownExpression() instanceof CtConstructorCall)) {
			return true;
		}
		final CtConstructorCall<? extends Throwable> thrownExpression = (CtConstructorCall<? extends Throwable>) ctThrow.getThrownExpression();
		if (!thrownExpression.getType().equals(factory.Type().createReference(UnsupportedOperationException.class))) {
			return true;
		}
		return false;
	}

	protected boolean takeSetterForCtElement(CtMethod<?> candidate) {
		return candidate.getParameters().get(0).getType().isSubtypeOf(CTELEMENT_REFERENCE);
	}

	protected boolean avoidInterfaces(CtMethod<?> candidate) {
		return candidate.getBody() != null;
	}
}
