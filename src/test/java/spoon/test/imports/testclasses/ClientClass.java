package spoon.test.imports.testclasses;

import spoon.test.imports.testclasses.internal.ChildClass;
import spoon.test.imports.testclasses.internal.PublicInterface2;

public class ClientClass extends ChildClass {
	private class InnerClass extends ChildClass.InnerClassProtected {}
	private class InnerClass2 implements PublicInterface2 {}
	private class InnerClass3a implements PublicInterface2.NestedInterface {}
	private class InnerClass3b extends PublicInterface2.NestedClass {}
	//SuperClass is package protected so it is not visible. 
//	private class InnerClassX implements spoon.test.imports.testclasses.internal.SuperClass.PublicInterface {}
	//ChildClass.PackageProtectedInterface is package protected so it is not visible.
//	private class InnerClassX implements spoon.test.imports.testclasses.internal.ChildClass.PackageProtectedInterface {}
	private class InnerClass4 implements ChildClass.ProtectedInterface {}
	private class InnerClass5 implements ChildClass.ProtectedInterface.NestedOfProtectedInterface {}
	private class InnerClass6 implements ChildClass.ProtectedInterface.NestedPublicInterface {}
	private class InnerClass7 implements ChildClass.PublicInterface {}
	private class InnerClass8 implements ChildClass.PublicInterface.NestedOfPublicInterface {}
	private class InnerClass9 implements ChildClass.PublicInterface.NestedPublicInterface {}
} 
