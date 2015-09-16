package spoon.test.prettyprinter.testclasses;

import spoon.test.prettyprinter.testclasses.TypeIdentifierCollision.Class0.ClassA;

public class TypeIdentifierCollision
{
	public enum ENUM{
		E1(spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.globalField, spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1),
		;
		final int NUM;
		final Enum<?> e;

		private ENUM( int num, Enum<?> e )
		{
			NUM = num;
			this.e = e;
		}
	}
	private int localField;

	public void setFieldUsingExternallyDefinedEnumWithSameNameAsLocal()
	{
		localField = spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.ENUM.E1.ordinal();
	}

	public void setFieldUsingLocallyDefinedEnum()
	{
		localField = ENUM.E1.ordinal();
	}

	public void setFieldOfClassWithSameNameAsTheCompilationUnitClass()
	{
		spoon.test.prettyprinter.testclasses.sub.TypeIdentifierCollision.globalField = localField;
	}

	public void referToTwoInnerClassesWithTheSameName()
	{
		ClassA.VAR0 = ClassA.getNum();
		Class1.ClassA.VAR1 = Class1.ClassA.getNum();
	}

	static class Class0
	{
		public static class ClassA
		{
			public static int VAR0;
			public static int getNum()
			{
				return 0;
			}
		}
	}

	static class Class1
	{
		public static class ClassA
		{
			public static int VAR1;
			public static int getNum()
			{
				return 0;
			}
		}
	}
}