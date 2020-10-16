package fr.inria.gforge.spoon.architecture;

public interface IConstraint<T> {
	void checkConstraint(T model);
}
