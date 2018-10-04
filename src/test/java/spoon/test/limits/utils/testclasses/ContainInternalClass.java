package spoon.test.limits.utils.testclasses;

public class ContainInternalClass {

	public class InternalClass {
		public class InsideInternalClass {

		}
	}

	Runnable toto = new Runnable() {
		public void run() {
		}

		@SuppressWarnings("unused")
		static final long serialVersionUID = 1L;
	};

}