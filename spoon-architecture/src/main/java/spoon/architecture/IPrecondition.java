package spoon.architecture;

import java.util.Collection;
import java.util.function.Function;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;

public interface IPrecondition<T extends CtElement> extends Function<CtModel, Collection<T>> {
	// Marker interface
}
