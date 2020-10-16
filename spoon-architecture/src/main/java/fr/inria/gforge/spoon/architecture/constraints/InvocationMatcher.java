package fr.inria.gforge.spoon.architecture.constraints;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class InvocationMatcher implements Predicate<CtMethod<?>> {

	private Set<CtExecutableReference<?>> lookUp = new HashSet<>();

	public InvocationMatcher(CtModel model) {
		List<CtInvocation<?>> invocations = model.getElements(new TypeFilter<>(CtInvocation.class));
		for (CtInvocation<?> ctInvocation : invocations) {
			CtExecutableReference<?> exec = ctInvocation.getExecutable();
			if (exec != null) {
				lookUp.add(exec);
			}
		}
		model.getElements(new TypeFilter<>(CtExecutableReference.class)).stream()
				.map(v -> v.getExecutableDeclaration()).filter(Objects::nonNull)
				.forEach(v -> lookUp.add(v.getReference()));
	}

	@Override
	public boolean test(CtMethod<?> t) {
		return lookUp.contains(t.getReference());
	}

}
