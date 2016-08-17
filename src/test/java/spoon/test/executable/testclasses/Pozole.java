package spoon.test.executable.testclasses;

public class Pozole implements Runnable {
	public void cook() {
		run();
	}

	@Override
	public void run() {

	}

	void m() {
		int i;
		{
			i = 0;
		}
		int x = 2 * i;
	}
}
