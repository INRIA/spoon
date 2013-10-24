package spoon.test.limits.utils;

public class ContainInternalClass {

	public class InternalClass {
		public class InsideInternalClass {

		}
	}

	Runnable toto = new Runnable() {
		public void run() {
		}
		static final long serialVersionUID = 1L;
	};

}