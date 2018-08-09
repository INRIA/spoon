package spoon.test.visibility.testclasses;

import spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf;

public class MethodeWithNonAccessibleTypeArgument {
	
	public void method(){
		new AccessibleClassFromNonAccessibleInterf().method(new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf());
	}
}
