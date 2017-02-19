package spoon.test.imports.testclasses;

import spoon.test.imports.testclasses.internal.ChildClass;

public class ClientClass extends ChildClass {
	private class InnerClass extends spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected {}
	private class InnerClass2 implements spoon.test.imports.testclasses.internal.PublicInterface2 {}
	private class InnerClass3a implements spoon.test.imports.testclasses.internal.PublicInterface2.NestedInterface {}
	private class InnerClass3b extends spoon.test.imports.testclasses.internal.PublicInterface2.NestedClass {}
	//SuperClass is package protected so it is not visible. 
//	private class InnerClassX implements spoon.test.imports.testclasses.internal.SuperClass.PublicInterface {}
	//ChildClass.PackageProtectedInterface is package protected so it is not visible.
//	private class InnerClassX implements spoon.test.imports.testclasses.internal.ChildClass.PackageProtectedInterface {}
	private class InnerClass4 implements spoon.test.imports.testclasses.internal.ChildClass.ProtectedInterface {}
	private class InnerClass5 implements spoon.test.imports.testclasses.internal.ChildClass.ProtectedInterface.NestedOfProtectedInterface {}
	private class InnerClass6 implements spoon.test.imports.testclasses.internal.ChildClass.ProtectedInterface.NestedPublicInterface {}
	private class InnerClass7 implements spoon.test.imports.testclasses.internal.ChildClass.PublicInterface {}
	private class InnerClass8 implements spoon.test.imports.testclasses.internal.ChildClass.PublicInterface.NestedOfPublicInterface {}
	private class InnerClass9 implements spoon.test.imports.testclasses.internal.ChildClass.PublicInterface.NestedPublicInterface {}
} 
