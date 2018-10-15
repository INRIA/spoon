package spoon.test.generics.testclasses2;

import java.util.Set;

public interface LikeCtClass<T extends Object> {
	Set<AnType<T>> getConstructors();
	<C extends LikeCtClass<T>> C setConstructors(Set<AnType<T>> constructors);
}

interface AnType<U> {}