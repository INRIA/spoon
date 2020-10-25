package spoon.architecture.constraints;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class FieldReferenceMatcher implements Predicate<CtField<?>> {

	private Set<CtFieldReference<?>> lookUp;

	public FieldReferenceMatcher(CtModel model) {
		lookUp = new HashSet<>();
		model.getElements(new TypeFilter<>(CtFieldAccess.class))
				.stream()
				.map(v -> v.getVariable())
				.filter(Objects::nonNull)
				.forEach(lookUp::add);
	}

	@Override
	public boolean test(CtField<?> t) {
		return lookUp.contains(t.getReference());
	}

}
