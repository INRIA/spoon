package spoon.test.annotation.testclasses;

public @interface AnnotParamTypes {

	int integer();
	int[] integers();

	String string();
	String[] strings();

	Class<?> clazz();
	Class<?>[] classes();
	
	boolean b();
	byte byt();
	char c();
	short s();
	long l();
	float f();
	double d();
	
	AnnotParamTypeEnum e();
	InnerAnnot ia();
}
