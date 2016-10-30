package spoon.test.staticFieldAccess2;

public class ImplicitStaticClassAccess {
	static String ImplicitStaticClassAccess = "";
	
	ImplicitStaticClassAccess() {
		this(ImplicitStaticClassAccess.class);
	}
	
	ImplicitStaticClassAccess(Class<?> clazz) {
		ImplicitStaticClassAccess.class.getName();
	}

	public void testLocalMethodInvocations() {
		ImplicitStaticClassAccess.class.getName();
	}
	
}
