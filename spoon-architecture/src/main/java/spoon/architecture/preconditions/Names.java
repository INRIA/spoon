/**
 * SPDX-License-Identifier:  MIT
 */
package spoon.architecture.preconditions;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypeInformation;
/**
 * This provides default naming matcher for qualified and simple name. For regex usage see {@code #matches(Pattern)} and {@code #matchesQualified(Pattern)}.
 * For regex there are methods for negation provided, for the other matcher use {@code Predicate#negate()} if you want to negate them. <p>
 * All matcher are <b>null safe</b> and stateless. A fully qualified name is a java name consisting of one or more java identifiers separated by dots(.).
 * <pre>{@code {javaIdentifier}.[javaIdentifier]}</pre>
 */
public class Names {

	private Names() {
		// private constructor for util class
	}
	/**
	 * Checks if the given string is equal to the elements name. {@see String#equals(Object)}
	 * @param <T>  element type of input
	 * @param name  string for comparison
	 * @return  a predicate checking if an elements name is equal to {@code name}
	 */
	public static <T extends CtNamedElement> Predicate<T> equals(String name) {
		return (input) -> input != null && input.getSimpleName().equals(name);
	}
	/**
	 * Checks if the given string is contained in the elements name. {@see String#contains(CharSequence)}
	 * @param <T>  element type of input
	 * @param name  contained string
	 * @return  a predicate checking if an elements name contains {@code name}
	 */
	public static  <T extends CtNamedElement> Predicate<T> contains(String name) {
		return (input) -> input != null && input.getSimpleName().contains(name);
	}
	/**
	 * Checks if the given string is a prefix of the elements name. {@see String#startsWith(String)}
	 * @param <T>  element type of input
	 * @param name  prefix
	 * @return  a predicate checking if an elements name startsWith {@code name}
	 */
	public static  <T extends CtNamedElement> Predicate<T> startsWith(String name) {
		return (input) -> input != null && input.getSimpleName().startsWith(name);
	}
	/**
	 * Checks if the given string is a suffix of the elements name. {@see String#endsWith(String)}
	 * @param <T>  element type of input
	 * @param name  suffix
	 * @return  a predicate checking if an elements name endsWith {@code name}
	 */
	public static  <T extends CtNamedElement> Predicate<T> endsWith(String name) {
		return (input) -> input != null && input.getSimpleName().endsWith(name);
	}
	/**
	 * Checks if the given string matches the elements name. The string is converted to a regex, see {@link String#matches(String)} for details.
	 * @param <T>  element type of input
	 * @param regex  regex
	 * @return  a predicate checking if {@code name} matches elements name.
	 */
	public static  <T extends CtNamedElement> Predicate<T> matches(String regex) {
		return (input) -> input != null && input.getSimpleName().matches(regex);
	}
		/**
	 * Checks if the given pattern matches the elements name.
	 * @param <T>  element type of input
	 * @param regex  regex-pattern
	 * @return  a predicate checking if {@code name} matches elements name.
	 */
	public static  <T extends CtNamedElement> Predicate<T> matches(Pattern regex) {
		return (input) -> input != null && regex.matcher(input.getSimpleName()).matches();
	}
	/**
	 * Checks if the given string matches not the elements name. The string is converted to a regex, see {@link String#matches(String)} for details.
	 * @param <T>  element type of input
	 * @param regex  regex
	 * @return  a predicate checking if {@code name} matches not elements name.
	 */
	public static  <T extends CtNamedElement> Predicate<T> matchesNot(String regex) {
		return (input) -> !input.getSimpleName().matches(regex);
	}
	/**
	 * Checks if the given pattern matches not the elements name.
	 * @param <T>  element type of input
	 * @param regex  regex-pattern
	 * @return  a predicate checking if {@code name} matches not elements name.
	 */
	public static  <T extends CtNamedElement> Predicate<T> matchesNot(Pattern regex) {
		return (input) -> input != null && !regex.matcher(input.getSimpleName()).matches();
	}

//Qualified
	/**
	 * Checks if the given string is equal to the elements qualified name. {@see String#equals(Object)}
	 * @param <T>  element type of input
	 * @param name  string for comparison
	 * @return  a predicate checking if an elements qualified name is equal to {@code name}
	 */
	public static <T extends CtTypeInformation> Predicate<T> equalsQualified(String name) {
		return (input) -> input != null && input.getQualifiedName().equals(name);
	}
	/**
	 * Checks if the given string is contained in the elements qualified name. {@see String#contains(CharSequence)}
	 * @param <T>  element type of input
	 * @param name  contained string
	 * @return  a predicate checking if an elements qualified name contains {@code name}
	 */
	public static  <T extends CtTypeInformation> Predicate<T> containsQualified(String name) {
		return (input) -> input != null && input.getQualifiedName().contains(name);
	}
	/**
	 * Checks if the given string is a prefix of the elements qualified name. {@see String#startsWith(String)}
	 * @param <T>  element type of input
	 * @param name  prefix
	 * @return  a predicate checking if an elements qualified name startsWith {@code name}
	 */
	public static  <T extends CtTypeInformation> Predicate<T> startsWithQualified(String name) {
		return (input) -> input != null &&  input.getQualifiedName().startsWith(name);
	}
	/**
	 * Checks if the given string is a suffix of the elements qualified name. {@see String#endsWith(String)}
	 * @param <T>  element type of input
	 * @param name  suffix
	 * @return  a predicate checking if an elements qualified name endsWith {@code name}
	 */
	public static  <T extends CtTypeInformation> Predicate<T> endsWithQualified(String name) {
		return (input) -> input != null && input.getQualifiedName().endsWith(name);
	}
		/**
	 * Checks if the given string matches the elements qualified name. The string is converted to a regex, see {@link String#matches(String)} for details.
	 * @param <T>  element type of input
	 * @param regex  regex
	 * @return  a predicate checking if {@code name} matches elements qualified name.
	 */
	public static  <T extends CtTypeInformation> Predicate<T> matchesQualified(String regex) {
		return (input) -> input != null && input.getQualifiedName().matches(regex);
	}
	/**
	 * Checks if the given pattern matches the elements qualified name.
	 * @param <T>  element type of input
	 * @param regex  regex-pattern
	 * @return  a predicate checking if {@code name} matches elements qualified name.
	 */
	public static  <T extends CtTypeInformation> Predicate<T> matchesQualified(Pattern regex) {
		return (input) -> input != null && regex.matcher(input.getQualifiedName()).matches();
	}
	/**
	 * Checks if the given string matches not the elements qualified name. The string is converted to a regex, see {@link String#matches(String)} for details.
	 * @param <T>  element type of input
	 * @param regex  regex
	 * @return  a predicate checking if {@code name} matches not elements name.
	 */
	public static  <T extends CtTypeInformation> Predicate<T> matchesNotQualified(String regex) {
		return (input) -> input != null && !input.getQualifiedName().matches(regex);
	}
		/**
	 * Checks if the given pattern matches not the elements qualified name.
	 * @param <T>  element type of input
	 * @param regex  regex-pattern
	 * @return  a predicate checking if {@code name} matches not elements qualified name.
	 */
	public static  <T extends CtTypeInformation> Predicate<T> matchesNotQualified(Pattern regex) {
		return (input) -> input != null && !regex.matcher(input.getQualifiedName()).matches();
	}
}
