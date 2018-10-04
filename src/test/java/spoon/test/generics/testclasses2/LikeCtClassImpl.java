package spoon.test.generics.testclasses2;

import java.util.Set;

public class LikeCtClassImpl<T extends Object> implements LikeCtClass<T> {
	public Set<AnType<T>> getConstructors() {
		return null;
	}
	public <C extends LikeCtClass<T>> C setConstructors(Set<AnType<T>> constructors) {
		return (C)this;
	}
}
