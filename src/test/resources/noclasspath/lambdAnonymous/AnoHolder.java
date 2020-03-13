import java.util.ArrayList;
import java.util.List;

public class AnoHolder {
	public static void main(String[] args) {
		List<Doer>doers = new ArrayList<>();

		doers.add(new Doer() {// $1
			@Override
			public void run() {
				System.out.println("Tata");
			}
		});


		doers.stream().map(d -> new Doer() {
			@Override
			public void run() {// $2
				System.out.println("Toto");
			}
		}).map(d -> new Doer() {// $3
			@Override
			public void run() {
				System.out.println("Titi");
			}
		}).forEach(d -> d.run());



		doers.add(new Doer() {// $4
			@Override
			public void run() {
				System.out.println("Tutu");
			}
		});

	}

	public static void run(Doer d) {
		d.run();
	}
}