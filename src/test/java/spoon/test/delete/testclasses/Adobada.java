package spoon.test.delete.testclasses;

@Deprecated
public class Adobada {
	{
		int i;
		int j;
	}

	static {
		int i;
		int j;
	}

	public Adobada() {
		int i;
		int j;
	}

	public void m() {
		int i;
		int j;
	}

	public Adobada m2() {
		return new Adobada() {
			@Override
			public void m() {
				int i;
				int j;
			}
		};
	}

	public void m3() {
		switch (1) {
		case 1:
			int i;
			int j;
		default:
			int o;
			int b;
		}
	}

	public void m4(int i, float j, String s) {
		if (true) {
			System.err.println("");
		}
		int k;
		j = i = k = 3;
	}

	public void methodUsingjlObjectMethods() {
		notify();
	}
}
