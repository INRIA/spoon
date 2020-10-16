package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import spoon.reflect.declaration.CtNamedElement;

public class Naming implements Predicate<CtNamedElement> {

	private Predicate<String> nameMatcher;

	private Naming(Predicate<String> nameMatcher) {
		this.nameMatcher = nameMatcher;
	}
	@Override
	public boolean test(CtNamedElement t) {
		return nameMatcher.test(t.getSimpleName());
	}

	public static Naming equal(String name) {
		return new Naming((input) -> input.equals(name));
	}
	public static Naming contains(String name) {
		return new Naming((input) -> input.contains(name));
	}
	public static Naming startsWith(String name) {
		return new Naming((input) -> input.startsWith(name));
	}
	public static Naming endsWith(String name) {
		return new Naming((input) -> input.endsWith(name));
	}
}
