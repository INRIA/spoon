package spoon.test.limits;

import spoon.test.limits.utils.AccessibleClassFromNonAccessibleInterf;

public class MethodeWithNonAccessibleTypeArgument {
	
	public void method(){
		new AccessibleClassFromNonAccessibleInterf().method(new AccessibleClassFromNonAccessibleInterf());
	}
}
