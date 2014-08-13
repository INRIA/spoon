package spoon.test.visibility;

import spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf;

public class MethodeWithNonAccessibleTypeArgument {
	
	public void method(){
		new AccessibleClassFromNonAccessibleInterf().method(new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf());
	}
}
