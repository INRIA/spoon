import java.net.http.HttpClient;

final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

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

String message = "Hello, World!";
