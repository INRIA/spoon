package constructor;

public class Employee extends Person {
	private String officeID;

	public Employee(String name, int age, String officeID) {
		if (age < 18 || age > 65) {
			throw new IllegalArgumentException();
		}
		super(name, age);
		this.officeID = officeID;
	}

	public Employee(int age, String officeID) {
		if (age < 18 || age > 65) {
			throw new IllegalArgumentException();
		}
		this("Bob", age, officeID);
	}
}

class Person {
	private String name;
	private int age;
	Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
}
