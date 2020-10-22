package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import java.util.regex.Pattern;
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
	public static  <T extends CtNamedElement> Predicate<T> matches(String regex) {
		return (input) -> input.getSimpleName().matches(regex);
	}
	public static  <T extends CtNamedElement> Predicate<T> matches(Pattern regex) {
		return (input) -> regex.matcher(input.getSimpleName()).matches();
	}

	public static  <T extends CtNamedElement> Predicate<T> matchesNot(String regex) {
		return (input) -> !input.getSimpleName().matches(regex);
	}
	public static  <T extends CtNamedElement> Predicate<T> matchesNot(Pattern regex) {
		return (input) -> !regex.matcher(input.getSimpleName()).matches();
	}
}
