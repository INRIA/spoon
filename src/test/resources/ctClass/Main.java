import java.net.http.HttpClient;

import static java.net.http.HttpClient.Version.HTTP_1_1;

import module java.sql;

static HttpClient httpClient = HttpClient.newBuilder().version(HTTP_1_1).build();

void jdbc() throws SQLException {
	try (Connection connection = DriverManager
		.getConnection("jdbc:mysql://localhost:3306/myDb", "user1", "pass")) {
	}
}

static class Person {
	private String name;
	public Person(String name) {
		this.name = name;
	}
}

void main() {
	IO.println(greeting());
}

String greeting() {
	return message;
}

final String message = "Hello, World!";
