package spoon.test.lambda.testclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Foo {

	private List<Person> persons = new ArrayList<>();

	public void m() {
		printPersonsWithCheck(persons, (Check) () -> false);
	}

	public void m2() {
		printPersonsWithPredicate(persons, (Predicate<Person>) p -> p.age > 10);
	}

	public void m3() {
		printPersonsWithCheckPersons(persons, (CheckPersons) (p1, p2) -> p1.age - p2.age > 0);
	}

	public void m4() {
		printPersonsWithPredicate(persons, (Predicate<Person>) (Person p) -> p.age > 10);
	}

	public void m5() {
		printPersonsWithCheckPersons(persons, (CheckPersons) (Person p1, Person p2) -> p1.age - p2.age > 0);
	}

	public void m6() {
		printPersonsWithCheck(persons, (Check) () -> {
			System.err.println("");
			return false;
		});
	}

	public void m7() {
		printPersonsWithPredicate(persons, (Predicate<Person>) p -> {
			p.doSomething();
			return p.age > 10;
		});
	}

	public void m8() {
		if (((Predicate<Person>) p -> p.age > 18).test(new Person(10))) {
			System.err.println("Enjoy, you have more than 18.");
		}
	}

	public void m9() {
		Consumer<Integer> c = (field)->{
			field=1;
		};
	}

	public static void printPersonsWithPredicate(List<Person> roster, Predicate<Person> tester) {
		for (Person p : roster) {
			if (tester.test(p)) {
				p.printPerson();
			}
		}
	}

	public static void printPersonsWithCheckPerson(List<Person> roster, CheckPerson tester) throws Exception {
		for (Person p : roster) {
			if (tester.test(p)) {
				p.printPerson();
			}
		}
	}

	public static void printPersonsWithCheck(List<Person> roster, Check tester) {
		for (Person p : roster) {
			if (tester.test()) {
				p.printPerson();
			}
		}
	}

	public static void printPersonsWithCheckPersons(List<Person> roster, CheckPersons tester) {
		if (tester.test(roster.get(0), roster.get(1))) {
			roster.get(0).printPerson();
		}
	}

	public class Person {
		public final int age;

		public Person(int age) {
			this.age = age;
		}

		public void printPerson() {
			System.out.println(this.toString());
		}

		public void doSomething() {
		}
	}

	public interface CheckPerson {
		boolean test(Person p) throws Exception;
	}

	public interface Check {
		boolean test();
	}

	public interface CheckPersons {
		boolean test(Person p1, Person p2);
		boolean equals(Object other);
	}
}