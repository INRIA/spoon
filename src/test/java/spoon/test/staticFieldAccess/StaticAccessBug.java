package spoon.test.staticFieldAccess;

import spoon.test.staticFieldAccess.internal.Extends;

import java.util.concurrent.Callable;

/**
 * Created by nicolas on 05/09/2014.
 */
public class StaticAccessBug {

	private Runnable test() throws Exception {
		return new Callable<Runnable>() {
			@Override
			public Runnable call() throws Exception {
				return new Runnable() {
					private static final long C = 3;

					public void run() {
						long test = C;
					}
				};
			}
		}.call();

	}

	public void references() {
		String a = Extends.MY_STATIC_VALUE;
		String b = Extends.MY_OTHER_STATIC_VALUE;

		new Extends().test();
	}

}
