package spoon.test.methodreference.testclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

public class Foo {

	private final List<Person> roster;
	private final Person[] rosterAsArray;
	private final Phone[] rosterPhoneAsArray = new Phone[10];

	public Foo() {
		roster = new ArrayList<>();
		rosterAsArray = roster.toArray(new Person[roster.size()]);
	}

	public void m() {
		Arrays.sort(rosterAsArray, Person::compareByAge);
	}

	public void m0() {
		final Person tarzan = new Person("Tarzan", 18, new Phone("0681273948"));
		Arrays.sort(rosterPhoneAsArray, tarzan.phone::compareByNumbers);
	}

	public void m1() {
		ComparisonProvider myComparisonProvider = new ComparisonProvider();
		Arrays.sort(rosterAsArray, myComparisonProvider::compareByName);
	}

	public void m2() {
		String[] stringArray = { "Barbara", "James", "Mary", "John",
				"Patricia", "Robert", "Michael", "Linda" };
		Arrays.sort(stringArray, String::compareToIgnoreCase);
	}

	public void m3() {
		transferElements(roster, HashSet<Person>::new);
	}

	public void m4() {
		personFactory(Person::new);
	}

	public void m5() {
		typeFactory(Type<String>::new);
	}

	class ComparisonProvider {
		public int compareByName(Person a, Person b) {
			return a.name.compareTo(b.name);
		}

		public int compareByAge(Person a, Person b) {
			return a.age - b.age;
		}
	}

	public static final class Person {
		public final String name;
		public final int age;
		public final Phone phone;

		public Person() {
			name = "";
			age = 0;
			phone = null;
		}

		public Person(String name, int age, Phone phone) {
			this.name = name;
			this.age = age;
			this.phone = phone;
		}

		public static int compareByAge(Person a, Person b) {
			return a.age - b.age;
		}
	}

	public class Type<T> {
	}

	public static final class Phone {
		public final String numbers;

		public Phone(String numbers) {
			this.numbers = numbers;
		}

		public int compareByNumbers(Phone a, Phone b) {
			return a.numbers.compareTo(b.numbers);
		}
	}

	public static <T, SOURCE extends Collection<T>, DEST extends Collection<T>>
	DEST transferElements(
			SOURCE sourceCollection,
			Supplier<DEST> collectionFactory) {

		DEST result = collectionFactory.get();
		for (T t : sourceCollection) {
			result.add(t);
		}
		return result;
	}

	public static void personFactory(Supplier<Person> personFactory) {
	}

	public static void typeFactory(Supplier<Type> typeFactory) {
	}
}
