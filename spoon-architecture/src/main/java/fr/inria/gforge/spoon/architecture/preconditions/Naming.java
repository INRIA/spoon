package fr.inria.gforge.spoon.architecture.preconditions;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeInformation;

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

//Qualified

	public static <T extends CtTypeInformation> Predicate<T> equalsQualified(String name) {
		return (input) -> input.getQualifiedName().equals(name);
	}
	public static  <T extends CtTypeInformation> Predicate<T> containsQualified(String name) {
		return (input) -> input.getQualifiedName().contains(name);
	}
	public static  <T extends CtTypeInformation> Predicate<T> startsWithQualified(String name) {
		return (input) -> input.getQualifiedName().startsWith(name);
	}
	public static  <T extends CtTypeInformation> Predicate<T> endsWithQualified(String name) {
		return (input) -> input.getQualifiedName().endsWith(name);
	}
	public static  <T extends CtTypeInformation> Predicate<T> matchesQualified(String regex) {
		return (input) -> input.getQualifiedName().matches(regex);
	}
	public static  <T extends CtTypeInformation> Predicate<T> matchesQualified(Pattern regex) {
		return (input) -> regex.matcher(input.getQualifiedName()).matches();
	}

	public static  <T extends CtTypeInformation> Predicate<T> matchesNotQualified(String regex) {
		return (input) -> !input.getQualifiedName().matches(regex);
	}
	public static  <T extends CtTypeInformation> Predicate<T> matchesNotQualified(Pattern regex) {
		return (input) -> !regex.matcher(input.getQualifiedName()).matches();
	}
}
