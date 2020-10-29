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

/**
 * This defines a field reference matcher. It converts a {@code CtModel} to a lookup for fields. If a field is found, it has a reference and an usage.
 * It's a convince implementation for users for faster architecture check implementations. After creation this class supports parallel multiple lookups.
 * <p>
 * The underlying lookup is done with a hashset for performance reasons.
 * For simplicity {@link #test(CtField)} only checks for any field access, based on the assumption, that no field will have a write without a read.
 */
public class FieldReferenceMatcher implements Predicate<CtField<?>> {

	private Set<CtFieldReference<?>> lookUp;
	/**
	 * Creates a new FieldReferenceMatcher from the given meta model.
	 * @param model  a non null meta model.
	 */
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
