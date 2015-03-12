package spoon.test.constructorcallnewclass.testclasses;

public enum Bar {
	GREATER(">") {
		@Override
		public boolean matchResult(final int result) {
			return result > 0;
		}
	};

	private String symbol;

	private Bar(final String symbol) {
		this.symbol = symbol;
	}

	public abstract boolean matchResult(int result);
}
