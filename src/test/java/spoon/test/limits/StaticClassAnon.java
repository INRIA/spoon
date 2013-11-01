package spoon.test.limits;

public class StaticClassAnon {

	static void methode() {

	}

	static {
		@SuppressWarnings("unused")
		class StaticIntern {
			public void hasAMethod() {
				StaticClassAnon.methode();
			}
		}
	}

}
