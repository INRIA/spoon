package spoon.test.enums;

public enum Regular {
	A, B, C;
	Regular D;
	int i;
	
	public static void main(String[] args) {
		for (Regular e:values()) {
			System.out.println(e);
		}
	}
}
