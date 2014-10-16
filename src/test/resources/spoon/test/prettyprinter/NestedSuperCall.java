package spoon.test.prettyprinter;

public class NestedSuperCall extends NestedCallable.SuperCallable {

	public NestedSuperCall(NestedCallable nc) {
		nc.super("a");
	}
	
}

class NestedCallable {
	
	public class SuperCallable {
		
		public SuperCallable(String msg) {
			this.msg = msg;
		}
		
		@Override
		public String toString() {
			return msg;
		}
		
		private String msg;
	}
	
}