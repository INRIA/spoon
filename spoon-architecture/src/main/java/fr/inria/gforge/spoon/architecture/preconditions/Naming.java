package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtNamedElement;

public class Naming {

	private Naming() {

	}
	public static <T extends CtNamedElement> Predicate<T> equals(String name) {
		return (input) -> input.getSimpleName().equals(name);
	}
	public static  <T extends CtNamedElement> Predicate<T> contains(String name) {
		return (input) -> input.getSimpleName().contains(name);
	}
	public static  <T extends CtNamedElement> Predicate<T> startsWith(String name) {
		return (input) -> input.getSimpleName().startsWith(name);
	}
	public static  <T extends CtNamedElement> Predicate<T> endsWith(String name) {
		return (input) -> input.getSimpleName().endsWith(name);
	}
}
