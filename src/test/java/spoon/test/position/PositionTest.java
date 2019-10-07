/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.position;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.CompoundSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.comment.testclasses.BlockComment;
import spoon.test.comment.testclasses.Comment1;import spoon.test.position.testclasses.AnnonymousClassNewIface;
import spoon.test.position.testclasses.ArrayArgParameter;
import spoon.test.position.testclasses.CatchPosition;
import spoon.test.position.testclasses.CompilationUnitComments;
import spoon.test.position.testclasses.Expressions;
import spoon.test.position.testclasses.Foo;
import spoon.test.position.testclasses.FooAbstractMethod;
import spoon.test.position.testclasses.FooAnnotation;
import spoon.test.position.testclasses.FooClazz;
import spoon.test.position.testclasses.FooClazz2;
import spoon.test.position.testclasses.FooClazzWithComments;
import spoon.test.position.testclasses.FooEnum;
import spoon.test.position.testclasses.FooField;
import spoon.test.position.testclasses.FooForEach;
import spoon.test.position.testclasses.FooGeneric;
import spoon.test.position.testclasses.FooInterface;
import spoon.test.position.testclasses.FooLabel;
import spoon.test.position.testclasses.FooLambda;
import spoon.test.position.testclasses.FooMethod;
import spoon.test.position.testclasses.FooStatement;
import spoon.test.position.testclasses.FooSwitch;
import spoon.test.position.testclasses.Kokos;
import spoon.test.position.testclasses.NoMethodModifiers;
import spoon.test.position.testclasses.PositionParameterTypeWithReference;
import spoon.test.position.testclasses.PositionTry;
import spoon.test.position.testclasses.SomeEnum;
import spoon.test.position.testclasses.TypeParameter;
import spoon.test.position.testclasses.MoreLambda;
import spoon.test.query_function.testclasses.VariableReferencesModelTest;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class PositionTest {

	@Test
	public void testPositionClass() {
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/"));
		final CtType<FooClazz> foo = build.Type().get(FooClazz.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition position = (BodyHolderSourcePosition) foo.getPosition();

		assertEquals(4, position.getLine());
		assertEquals(6, position.getEndLine());

		assertEquals(42, position.getSourceStart());
		assertEquals(79, position.getSourceEnd());
		assertEquals("@Deprecated\n"
				+ "public class FooClazz {\n"
				+ "\n"
				+ "}", contentAtPosition(classContent, position));

		assertEquals("{\n\n}", contentAtPosition(classContent, position.getBodyStart(), position.getBodyEnd()));

		// this specifies that getLine starts at name (and not at Javadoc or annotation)
		final CtType<FooClazz> foo2 = build.Type().get(FooClazz2.class);
		assertEquals(42, foo2.getPosition().getSourceStart());
		assertEquals(4, foo2.getPosition().getLine());
		assertEquals(4, foo2.getPosition().getEndLine());

		assertEquals("FooClazz", contentAtPosition(classContent, position.getNameStart(), position.getNameEnd()));
		assertEquals("@Deprecated\npublic", contentAtPosition(classContent, position.getModifierSourceStart(), position.getModifierSourceEnd()));
	}
	
	
	@Test
	public void testPositionClassWithComments() {
		//contract: check that comments before and after the 'class' keyword are handled well by PositionBuilder
		//and it produces correct `modifierEnd`
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/"));
		final CtType<FooClazzWithComments> foo = build.Type().get(FooClazzWithComments.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition position = (BodyHolderSourcePosition) foo.getPosition();

//		assertEquals(4, position.getLine());
//		assertEquals(6, position.getEndLine());

		assertEquals(42, position.getSourceStart());
		assertEquals(132, position.getSourceEnd());
		assertEquals("/*c1*/\n" + 
				"//lc1\n" + 
				"public /*c2*/\n" + 
				"//lc2 /*\n" + 
				"class \n" + 
				"// */\n" + 
				"/*c3 class // */\n" + 
				"FooClazzWithComments {\n" + 
				"\n" + 
				"}", contentAtPosition(classContent, position));

		assertEquals("{\n\n}", contentAtPosition(classContent, position.getBodyStart(), position.getBodyEnd()));

		// this specifies that getLine starts at name (and not at Javadoc or annotation)
		final CtType<FooClazz> foo2 = build.Type().get(FooClazz2.class);
		assertEquals(42, foo2.getPosition().getSourceStart());
		assertEquals(4, foo2.getPosition().getLine());
		assertEquals(4, foo2.getPosition().getEndLine());

		assertEquals("FooClazzWithComments", contentAtPosition(classContent, position.getNameStart(), position.getNameEnd()));
		assertEquals("/*c1*/\n" + 
				"//lc1\n" + 
				"public", contentAtPosition(classContent, position.getModifierSourceStart(), position.getModifierSourceEnd()));
	}

	@Test
	public void testPositionParameterTypeReference() throws Exception {
		//contract: the parameterized type reference has a source position which includes parameter types, etc.
		final CtType<?> foo = buildClass(PositionParameterTypeWithReference.class);
		String classContent = getClassContent(foo);

		{
			assertEquals("T extends List<?>", contentAtPosition(classContent, foo.getFormalCtTypeParameters().get(0).getPosition()));
			assertEquals("X", contentAtPosition(classContent, foo.getFormalCtTypeParameters().get(1).getPosition()));
		}
		{	
			CtTypeReference<?> field1Type =  foo.getField("field1").getType();
			//this probably points to an bug in JDT. But we have no workaround in Spoon
			assertEquals("List<T>", contentAtPosition(classContent, field1Type.getPosition()));
		}
		{
			CtTypeReference<?> field2Type =  foo.getField("field2").getType();
			//this already worked well
			assertEquals("List<T>[][]", contentAtPosition(classContent, field2Type.getPosition()));
		}
		{
			CtTypeReference<?> field3Type =  foo.getField("field3").getType();
			//this probably points to an bug in JDT. But we have no workaround in Spoon, which handles spaces and comments too
			assertEquals("List<T // */ >\n\t/*// */>", contentAtPosition(classContent, field3Type.getPosition()));
		}
		{
			CtTypeReference<?> field4Type =  foo.getField("field4").getType();
			assertEquals("List<List<?>>", contentAtPosition(classContent, field4Type.getPosition()));
		}
		{
			CtTypeReference<?> fieldType =  foo.getField("field5").getType();
			assertEquals("List<? extends List<?>>", contentAtPosition(classContent, fieldType.getPosition()));
		}
		{
			CtReturn<?> retStmt = foo.getMethodsByName("m1").get(0).getBody().getStatement(0);
			assertEquals("o instanceof List<?>", contentAtPosition(classContent, retStmt.getReturnedExpression().getPosition()));
		}
		{
			CtReturn<?> retStmt = foo.getMethodsByName("m2").get(0).getBody().getStatement(0);
			assertEquals("false || o instanceof List<?>", contentAtPosition(classContent, retStmt.getReturnedExpression().getPosition()));
		}
		{
			CtTypeParameter methodGType = foo.getMethodsByName("m3").get(0).getFormalCtTypeParameters().get(0);
			assertEquals("U extends List<?>", contentAtPosition(classContent, methodGType.getPosition()));
		}
	}
	
	@Test
	public void testPositionInterface() {
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/"));
		final CtType<FooInterface> foo = build.Type().get(FooInterface.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition position = (BodyHolderSourcePosition) foo.getPosition();

		assertEquals(7, position.getLine());
		assertEquals(9, position.getEndLine());

		assertEquals(96, position.getSourceStart());
		assertEquals(169, position.getSourceEnd());
		assertEquals("@Deprecated\n"
				+ "@InnerAnnot(value=\"machin\")\n"
				+ "public interface FooInterface {\n"
				+ "\n"
				+ "}", contentAtPosition(classContent, position));

		assertEquals("{\n\n}", contentAtPosition(classContent, position.getBodyStart(), position.getBodyEnd()));

		assertEquals("FooInterface", contentAtPosition(classContent, position.getNameStart(), position.getNameEnd()));
		assertEquals("@Deprecated\n@InnerAnnot(value=\"machin\")\npublic", contentAtPosition(classContent, position.getModifierSourceStart(), position.getModifierSourceEnd()));
		
		{
			SourcePosition annPosition = foo.getAnnotations().get(0).getPosition();
			assertEquals("@Deprecated", contentAtPosition(classContent, annPosition.getSourceStart(), annPosition.getSourceEnd()));
		}
		{
			SourcePosition annPosition = foo.getAnnotations().get(1).getPosition();
			assertEquals("@InnerAnnot(value=\"machin\")", contentAtPosition(classContent, annPosition.getSourceStart(), annPosition.getSourceEnd()));
		}
	}

	@Test
	public void testPositionAnnotation() {
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/"));
		final CtType<FooAnnotation> foo = build.Type().get(FooAnnotation.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition position = (BodyHolderSourcePosition) foo.getPosition();

		assertEquals(9, position.getLine());
		assertEquals(11, position.getEndLine());

		assertEquals(163, position.getSourceStart());
		assertEquals(279, position.getSourceEnd());
		assertEquals("@Target(value={})\n"
				+ "@Retention(RetentionPolicy.RUNTIME)  \n"
				+ "public abstract @interface FooAnnotation {\n"
				+ "\tString value();\n"
				+ "}", contentAtPosition(classContent, position));

		assertEquals("{\n"
				+ "\tString value();\n"
				+ "}", contentAtPosition(classContent, position.getBodyStart(), position.getBodyEnd()));

		assertEquals("FooAnnotation", contentAtPosition(classContent, position.getNameStart(), position.getNameEnd()));
		assertEquals("@Target(value={})\n"
				+ "@Retention(RetentionPolicy.RUNTIME)  \npublic abstract", contentAtPosition(classContent, position.getModifierSourceStart(), position.getModifierSourceEnd()));
		
		CtMethod<?> method1 = foo.getMethodsByName("value").get(0);
		BodyHolderSourcePosition position1 = (BodyHolderSourcePosition) method1.getPosition();

		assertEquals(10, position1.getLine());
		assertEquals(10, position1.getEndLine());

		assertEquals(263, position1.getSourceStart());
		assertEquals(277, position1.getSourceEnd());

		assertEquals("String value();", contentAtPosition(classContent, position1));
		assertEquals("value", contentAtPosition(classContent, position1.getNameStart(), position1.getNameEnd()));
		assertEquals("", contentAtPosition(classContent, position1.getModifierSourceStart(), position1.getModifierSourceEnd()));
		//contract: body of abstract method is empty
		assertEquals("", contentAtPosition(classContent, position1.getBodyStart(), position1.getBodyEnd()));
	}

	@Test
	public void testPositionField() throws Exception {
		final Factory build = build(FooField.class);
		final CtType<FooField> foo = build.Type().get(FooField.class);
		String classContent = getClassContent(foo);

		DeclarationSourcePosition position1 = (DeclarationSourcePosition) foo.getField("field1").getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(5, position1.getEndLine());

		assertEquals(68, position1.getSourceStart());
		assertEquals(95, position1.getSourceEnd());

		assertEquals("public final int field1 = 0;", contentAtPosition(classContent, position1));
		assertEquals("field1", contentAtPosition(classContent, position1.getNameStart(), position1.getNameEnd()));
		assertEquals("public final", contentAtPosition(classContent, position1.getModifierSourceStart(), position1.getModifierSourceEnd()));

		DeclarationSourcePosition position2 = (DeclarationSourcePosition) foo.getField("field2").getPosition();

		assertEquals(7, position2.getLine());
		assertEquals(8, position2.getEndLine());

		assertEquals(99, position2.getSourceStart());
		assertEquals(116, position2.getSourceEnd());

		assertEquals("int field2 =\n"
				+ "\t\t\t0;", contentAtPosition(classContent, position2));
		assertEquals("field2", contentAtPosition(classContent, position2.getNameStart(), position2.getNameEnd()));
		assertEquals("", contentAtPosition(classContent, position2.getModifierSourceStart(), position2.getModifierSourceEnd()));

		CtAssignment m = foo.getMethod("m").getBody().getStatement(0);
		CtFieldAccess assigned = (CtFieldAccess) m.getAssigned();
		SourcePosition position3 = assigned.getPosition();
		assertEquals(13, position3.getLine());
		assertEquals(13, position3.getEndLine());

		assertEquals(168, position3.getSourceStart());
		assertEquals(184, position3.getSourceEnd());

		assertEquals("FooField.f.field2", contentAtPosition(classContent, position3));

		CtFieldAccess target = (CtFieldAccess) assigned.getTarget();
		SourcePosition position4 = target.getPosition();
		assertEquals(13, position4.getLine());
		assertEquals(13, position4.getEndLine());

		assertEquals(168, position4.getSourceStart());
		assertEquals(177, position4.getSourceEnd());

		assertEquals("FooField.f", contentAtPosition(classContent, position4));

		CtExpression typeAccess = target.getTarget();
		SourcePosition position5 = typeAccess.getPosition();
		assertEquals(13, position5.getLine());
		assertEquals(13, position5.getEndLine());

		assertEquals(168, position5.getSourceStart());
		assertEquals(175, position5.getSourceEnd());

		assertEquals("FooField", contentAtPosition(classContent, position5));
	}

	@Test
	public void testPositionGeneric() throws Exception {
		final Factory build = build(FooGeneric.class);
		final CtClass<FooGeneric> foo = build.Class().get(FooGeneric.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition position = (BodyHolderSourcePosition) foo.getPosition();

		assertEquals(3, position.getLine());
		assertEquals(31, position.getEndLine());

		assertEquals(42, position.getSourceStart());
		assertEquals(411, position.getSourceEnd());

		assertEquals("FooGeneric", contentAtPosition(classContent, position.getNameStart(), position.getNameEnd()));
		assertEquals("public", contentAtPosition(classContent, position.getModifierSourceStart(), position.getModifierSourceEnd()));


		DeclarationSourcePosition position1 = (DeclarationSourcePosition) foo.getField("variable").getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(5, position1.getEndLine());

		assertEquals(88, position1.getSourceStart());
		assertEquals(118, position1.getSourceEnd());

		assertEquals("public final T variable = null;", contentAtPosition(classContent, position1));
		assertEquals("variable", contentAtPosition(classContent, position1.getNameStart(), position1.getNameEnd()));
		assertEquals("public final", contentAtPosition(classContent, position1.getModifierSourceStart(), position1.getModifierSourceEnd()));

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		BodyHolderSourcePosition position2 = (BodyHolderSourcePosition) method1
				.getPosition();

		assertEquals("public @Deprecated static <S> S m(int parm1) {\n"
				+ "\t\treturn null;\n"
				+ "\t}", contentAtPosition(classContent, position2));
		assertEquals("m", contentAtPosition(classContent, position2.getNameStart(), position2.getNameEnd()));

		// /!\ the annotations can be between two modifiers
		assertEquals("public @Deprecated static", contentAtPosition(classContent, position2.getModifierSourceStart(), position2.getModifierSourceEnd()));
	}

	@Test
	public void testPositionMethod() throws Exception {
		final Factory build = build(FooMethod.class);
		final CtClass<FooMethod> foo = build.Class().get(FooMethod.class);
		String classContent = getClassContent(foo);

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		BodyHolderSourcePosition position1 = (BodyHolderSourcePosition) method1.getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(7, position1.getEndLine());

		assertEquals(69, position1.getSourceStart());
		assertEquals(114, position1.getSourceEnd());

		assertEquals("public static void m(int parm1) {\n"
				+ "\t\treturn;\n"
				+ "\t}", contentAtPosition(classContent, position1));
		assertEquals("m", contentAtPosition(classContent, position1.getNameStart(), position1.getNameEnd()));
		assertEquals("public static", contentAtPosition(classContent, position1.getModifierSourceStart(), position1.getModifierSourceEnd()));
		//contract: body contains starting and ending brackets {}
		assertEquals("{\n"
				+ "\t\treturn;\n"
				+ "\t}", contentAtPosition(classContent, position1.getBodyStart(), position1.getBodyEnd()));

		DeclarationSourcePosition positionParam1 = (DeclarationSourcePosition) method1.getParameters().get(0).getPosition();

		assertEquals(5, positionParam1.getLine());
		assertEquals(5, positionParam1.getEndLine());

		assertEquals(90, positionParam1.getSourceStart());
		assertEquals(98, positionParam1.getSourceEnd());

		assertEquals("int parm1", contentAtPosition(classContent, positionParam1));
		assertEquals("parm1", contentAtPosition(classContent, positionParam1.getNameStart(), positionParam1.getNameEnd()));
		assertEquals("", contentAtPosition(classContent, positionParam1.getModifierSourceStart(), positionParam1.getModifierSourceEnd()));

		CtMethod method2 = foo.getMethodsByName("mWithDoc").get(0);
		BodyHolderSourcePosition position2 = (BodyHolderSourcePosition) method2.getPosition();

		assertEquals(13, position2.getLine());
		assertEquals(15, position2.getEndLine());

		assertEquals("/**\n"
				+ "\t * Method with javadoc\n"
				+ "\t * @param parm1 the parameter\n"
				+ "\t */\n"
				+ "\tint mWithDoc(int parm1) {\n"
				+ "\t\treturn parm1;\n"
				+ "\t}", contentAtPosition(classContent, position2));
		assertEquals("mWithDoc", contentAtPosition(classContent, position2.getNameStart(), position2.getNameEnd()));
		assertEquals("", contentAtPosition(classContent, position2.getModifierSourceStart(), position2.getModifierSourceEnd()));

		CtConstructor<FooMethod> constructor = foo.getConstructor(build.Type().integerPrimitiveType());
		SourcePosition position3 = constructor.getPosition();
		contentAtPosition(classContent, position3);

		CtMethod mWithLine = foo.getMethod("mWithLine", build.Type().integerPrimitiveType());
		SourcePosition position4 = mWithLine.getPosition();
		contentAtPosition(classContent, position4);
		
		{
			CtMethod<?> methodWithEmptyBody = foo.getMethodsByName("emptyMethod").get(0);
			BodyHolderSourcePosition pos = (BodyHolderSourcePosition) methodWithEmptyBody.getPosition();
			assertEquals("public void emptyMethod() {}", contentAtPosition(classContent, pos));
			
			assertEquals("{}", contentAtPosition(classContent, pos.getBodyStart(), pos.getBodyEnd()));
		}
	}

	@Test
	public void testPositionAbstractMethod() throws Exception {
		final Factory build = build(FooAbstractMethod.class);
		final CtClass<FooMethod> foo = build.Class().get(FooAbstractMethod.class);
		String classContent = getClassContent(foo);

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		BodyHolderSourcePosition position1 = (BodyHolderSourcePosition) method1.getPosition();

		assertEquals(5, position1.getLine());
		assertEquals(5, position1.getEndLine());

		assertEquals(86, position1.getSourceStart());
		assertEquals(125, position1.getSourceEnd());

		assertEquals("public abstract void m(final int parm1);", contentAtPosition(classContent, position1));
		assertEquals("m", contentAtPosition(classContent, position1.getNameStart(), position1.getNameEnd()));
		assertEquals("public abstract", contentAtPosition(classContent, position1.getModifierSourceStart(), position1.getModifierSourceEnd()));
		//contract: body of abstract method is empty
		assertEquals("", contentAtPosition(classContent, position1.getBodyStart(), position1.getBodyEnd()));

		DeclarationSourcePosition positionParam1 = (DeclarationSourcePosition) method1.getParameters().get(0).getPosition();

		assertEquals(5, positionParam1.getLine());
		assertEquals(5, positionParam1.getEndLine());

		assertEquals(109, positionParam1.getSourceStart());
		assertEquals(123, positionParam1.getSourceEnd());

		assertEquals("final int parm1", contentAtPosition(classContent, positionParam1));
		assertEquals("parm1", contentAtPosition(classContent, positionParam1.getNameStart(), positionParam1.getNameEnd()));
		assertEquals("final", contentAtPosition(classContent, positionParam1.getModifierSourceStart(), positionParam1.getModifierSourceEnd()));
	}

	@Test
	public void testPositionStatement() throws Exception {
		final Factory build = build(FooStatement.class);
		final CtType<FooStatement> foo = build.Type().get(FooStatement.class);
		String classContent = getClassContent(foo);

		CtMethod<?> method1 = foo.getMethodsByName("m").get(0);
		CtBlock<?> body = method1.getBody();
		SourcePosition positionBody = body.getPosition();

		assertEquals(7, positionBody.getLine());
		assertEquals(23, positionBody.getEndLine());

		assertEquals("{\n"
				+ "\t\tint field2 = m2(parm1);\n"
				+ "\t\tthis.field = m2(parm1);\n"
				+ "\t\tif(parm1 > 2 && true) {\n"
				+ "\t\t\tswitch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}\n"
				+ "\t\t\tint count = 0;\n"
				+ "\t\t\tfor (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}\n"
				+ "\t\t}\n"
				+ "\t\treturn;\n"
				+ "\t}", contentAtPosition(classContent, positionBody));

		SourcePosition positionLocalVariable = body.getStatement(0).getPosition();

		assertEquals(8, positionLocalVariable.getLine());
		assertEquals(8, positionLocalVariable.getEndLine());

		assertEquals("int field2 = m2(parm1);", contentAtPosition(classContent, positionLocalVariable));

		SourcePosition positionFieldWrite = body.getStatement(1).getPosition();

		assertEquals(9, positionFieldWrite.getLine());
		assertEquals(9, positionFieldWrite.getEndLine());

		assertEquals("this.field = m2(parm1);", contentAtPosition(classContent, positionFieldWrite));

		CtIf ctIf = body.getStatement(2);
		SourcePosition positionIf = ctIf.getPosition();

		assertEquals(10, positionIf.getLine());
		assertEquals(21, positionIf.getEndLine());

		assertEquals("if(parm1 > 2 && true) {\n"
				+ "\t\t\tswitch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}\n"
				+ "\t\t\tint count = 0;\n"
				+ "\t\t\tfor (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}\n"
				+ "\t\t}", contentAtPosition(classContent, positionIf));

		SourcePosition positionSwitch = ((CtBlock)ctIf.getThenStatement()).getStatement(0).getPosition();

		assertEquals(11, positionSwitch.getLine());
		assertEquals(16, positionSwitch.getEndLine());

		assertEquals("switch (parm1) {\n"
				+ "\t\t\tcase 1:\n"
				+ "\t\t\t\treturn;\n"
				+ "\t\t\tdefault:\n"
				+ "\t\t\t\tparm1++;\n"
				+ "\t\t\t}", contentAtPosition(classContent, positionSwitch));

		positionLocalVariable = ((CtBlock)ctIf.getThenStatement()).getStatement(1).getPosition();

		assertEquals(17, positionLocalVariable.getLine());
		assertEquals(17, positionLocalVariable.getEndLine());

		assertEquals("int count = 0;", contentAtPosition(classContent, positionLocalVariable));

		SourcePosition positionFor = ((CtBlock)ctIf.getThenStatement()).getStatement(2).getPosition();

		assertEquals(18, positionFor.getLine());
		assertEquals(20, positionFor.getEndLine());

		assertEquals("for (int i =0; i< parm1; i++) {\n"
				+ "\t\t\t\tcount ++;\n"
				+ "\t\t\t}", contentAtPosition(classContent, positionFor));

		SourcePosition positionReturn = method1.getBody().getStatement(3).getPosition();

		assertEquals(22, positionReturn.getLine());
		assertEquals(22, positionReturn.getEndLine());

		assertEquals("return;", contentAtPosition(classContent, positionReturn));
	}


	private String getClassContent(CtType type) {
		File file = type.getPosition().getFile();
		try {
			return FileUtils.readFileToString(file, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String contentAtPosition(String content, int start, int end) {
		return content.substring(start,  end + 1);
	}

	private String contentAtPosition(String content, SourcePosition position) {
		return content.substring(position.getSourceStart(),  position.getSourceEnd() + 1);
	}

	@Test
	public void testSourcePosition() {
		SourcePosition s = new spoon.Launcher().getFactory().Core().createClass().getPosition();
		assertFalse(s.isValidPosition());
		assertFails(() -> s.getSourceStart());
		assertFails(() -> s.getSourceEnd());
		assertFails(() -> s.getColumn());
		assertFails(() -> s.getLine());
		assertEquals("(unknown file)", s.toString());
		assertTrue(s.hashCode() > 0); // no NPE
	}
	
	private static void assertFails(Runnable code) {
		try {
			code.run();
			fail();
		} catch (Exception e) {
			//OK
		}
	}

	@Test
	public void defaultConstructorPositionTest() throws Exception {
		CtClass<Foo> aClass = (CtClass<Foo>) buildClass(Foo.class);
		CtConstructor<Foo> defaultConstructor = aClass.getConstructor();
		assertEquals(SourcePosition.NOPOSITION, defaultConstructor.getPosition());
		CtStatement implicitSuperCall = defaultConstructor.getBody().getStatement(0);
		assertTrue(implicitSuperCall.isImplicit());
		assertEquals(SourcePosition.NOPOSITION, implicitSuperCall.getPosition());
	}

	@Test
	public void getPositionOfImplicitBlock() {
		// contract: position of implicit block in if (cond) [implicit block] else [implicit block] should be the source position of implicit block content.
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/position/testclasses/ImplicitBlock.java");
		launcher.buildModel();

		CtIf ifElement = launcher.getModel().getElements(new TypeFilter<>(CtIf.class)).get(0);
		CtStatement thenStatement = ifElement.getThenStatement();

		assertTrue(thenStatement instanceof CtBlock);

		CtBlock thenBlock = (CtBlock) thenStatement;
		SourcePosition positionThen = thenBlock.getPosition();
		CtStatement returnStatement = thenBlock.getStatement(0);
		assertEquals(returnStatement.getPosition(), positionThen);
		assertEquals("ImplicitBlock.java", positionThen.getFile().getName());
		assertEquals(7, positionThen.getLine());

		CtStatement elseStatement = ifElement.getElseStatement();

		assertTrue(elseStatement instanceof CtBlock);

		CtBlock elseBlock = (CtBlock) elseStatement;
		SourcePosition positionElse = elseBlock.getPosition();
		CtStatement otherReturnStatement = elseBlock.getStatement(0);
		assertEquals(otherReturnStatement.getPosition(), positionElse);
		assertEquals("ImplicitBlock.java", positionThen.getFile().getName());
		assertEquals(8, positionElse.getLine());

		assertNotEquals(returnStatement, otherReturnStatement);
	}
	@Test
	public void testPositionMethodTypeParameter() throws Exception {
		//contract: the Method TypeParameter T extends List<?> has simple source position
		//the previous used DeclarationSourcePosition had incorrect details
		final CtType<?> foo = buildClass(TypeParameter.class);
		String classContent = getClassContent(foo);

		CtTypeParameter typeParam = foo.getMethodsByName("m").get(0).getFormalCtTypeParameters().get(0);
		assertEquals("T extends List<?>", contentAtPosition(classContent, typeParam.getPosition()));
		assertFalse(typeParam.getPosition() instanceof DeclarationSourcePosition);
	}
	
	@Test
	public void testPositionOfAnnonymousType() throws Exception {
		//contract: the annonymous type has consistent position
		final CtEnum foo = (CtEnum) buildClass(SomeEnum.class);
		String classContent = getClassContent(foo);

		CtNewClass<?> newClass = (CtNewClass<?>) foo.getEnumValue("X").getDefaultExpression();
		CtClass<?> annonClass = newClass.getAnonymousClass();
		assertEquals("{\n" + 
				"		void m() {};\n" + 
				"	}", contentAtPosition(classContent, annonClass.getPosition()));
		BodyHolderSourcePosition bhsp = (BodyHolderSourcePosition) annonClass.getPosition();
		int start = annonClass.getPosition().getSourceStart();
		int end = annonClass.getPosition().getSourceEnd();
		//body is equal to source start/end
		assertEquals(start, bhsp.getBodyStart());
		assertEquals(end, bhsp.getBodyEnd());

		//there is no name and no modifiers
		assertEquals(start - 1, bhsp.getNameEnd());
		assertEquals(start, bhsp.getModifierSourceStart());
		assertEquals(start - 1, bhsp.getModifierSourceEnd());
		assertEquals(start, bhsp.getNameStart());
		assertEquals(start - 1, bhsp.getNameEnd());
	}
	
	@Test
	public void testPositionOfAnnonymousTypeByNewInterface() throws Exception {
		//contract: the annonymous type has consistent position
		final CtType<?> foo = buildClass(AnnonymousClassNewIface.class);
		String classContent = getClassContent(foo);

		CtLocalVariable<?> localVar = (CtLocalVariable<?>) foo.getMethodsByName("m").get(0).getBody().getStatement(0);
		CtNewClass<?> newClass = (CtNewClass<?>) localVar.getDefaultExpression();
		CtClass<?> annonClass = newClass.getAnonymousClass();
		BodyHolderSourcePosition bhsp = (BodyHolderSourcePosition) annonClass.getPosition();
		int start = annonClass.getPosition().getSourceStart();
		int end = annonClass.getPosition().getSourceEnd();
		assertEquals("{\r\n" + 
				"			@Override\r\n" + 
				"			public void accept(Set<?> t) {\r\n" + 
				"			}\r\n" + 
				"		}", contentAtPosition(classContent, start, end));
		
		assertEquals("{\r\n" + 
				"			@Override\r\n" + 
				"			public void accept(Set<?> t) {\r\n" + 
				"			}\r\n" + 
				"		}", contentAtPosition(classContent, bhsp.getBodyStart(), bhsp.getBodyEnd()));

		//there is no name and no modifiers and they are located at source start
		assertEquals(start - 1, bhsp.getNameEnd());
		assertEquals(start, bhsp.getModifierSourceStart());
		assertEquals(start - 1, bhsp.getModifierSourceEnd());
		assertEquals(start, bhsp.getNameStart());
		assertEquals(start - 1, bhsp.getNameEnd());
	}
	
	@Test
	public void testPositionOfCtImport() throws Exception {
		//contract: the CtImport has position
		final CtType<?> foo = buildClass(
			launcher ->	launcher.getEnvironment().setAutoImports(true), 
			AnnonymousClassNewIface.class);
		String originSources = foo.getPosition().getCompilationUnit().getOriginalSourceCode();
		List<CtImport> imports = foo.getPosition().getCompilationUnit().getImports();
		assertEquals(2, imports.size());
		Iterator<CtImport> iter = imports.iterator();
		{
			CtImport ctImport = iter.next();
			assertEquals("import java.util.Set;", contentAtPosition(originSources, ctImport.getPosition()));
		}
		{
			CtImport ctImport = iter.next();
			assertEquals("import java.util.function.Consumer;", contentAtPosition(originSources, ctImport.getPosition()));
		}
	}
	

	@Test
	public void testEmptyModifiersOfMethod() throws Exception {
		//contract: the modifiers of Method without modifiers are empty and have correct start
		final CtType<?> foo = buildClass(NoMethodModifiers.class);
		String classContent = getClassContent(foo);

		BodyHolderSourcePosition bhsp = (BodyHolderSourcePosition) foo.getMethodsByName("m").get(0).getPosition();
		assertEquals("void m();", contentAtPosition(classContent, bhsp));
		int start = bhsp.getSourceStart();
		int end = bhsp.getSourceEnd();
		assertEquals(start, bhsp.getModifierSourceStart());
		assertEquals(start - 1, bhsp.getModifierSourceEnd());
		assertEquals("m", contentAtPosition(classContent, bhsp.getNameStart(), bhsp.getNameEnd()));
		assertEquals(end, bhsp.getBodyStart());
		assertEquals(end - 1, bhsp.getBodyEnd());
	}

	@Test
	public void testTypeModifiersPositionAfterComment() throws Exception {
		// contract: the modifier position is correct even when comment contains modifier name too
		CtType<?> type = ModelUtils.buildClass(cfg -> {
			cfg.getEnvironment().setCommentEnabled(true);
		}, Kokos.class);
		String classContent = getClassContent(type);
		
		CtExtendedModifier modifier = type.getExtendedModifiers().iterator().next();
		SourcePosition commentPos = type.getComments().get(0).getPosition();
		//modifier is not positioned in comment, but after comment
		assertTrue(commentPos.getSourceEnd() < modifier.getPosition().getSourceStart());
		
		assertEquals("public", contentAtPosition(classContent, modifier.getPosition()));
	}

	@Test
	public void testPositionTryCatch() throws Exception {
		//contract: check that the variable in the catch has a correct position
		CtType<?> foo = buildClass(PositionTry.class);
		String classContent = getClassContent(foo);

		List<CtCatchVariable> elements = foo.getElements(new TypeFilter<>(CtCatchVariable.class));

		CtCatchVariable withoutModifier = elements.get(0);
		assertEquals("java.lang.Exception e", contentAtPosition(classContent, withoutModifier.getPosition().getSourceStart(), withoutModifier.getPosition().getSourceEnd()));
		assertEquals("e", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameEnd()));
		assertEquals("", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceEnd()));

		CtCatchVariable withModifier = elements.get(1);
		assertEquals("final java.lang.Exception e", contentAtPosition(classContent, withModifier.getPosition().getSourceStart(), withModifier.getPosition().getSourceEnd()));
		assertEquals("e", contentAtPosition(classContent,
				((DeclarationSourcePosition) withModifier.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withModifier.getPosition()).getNameEnd()));
		assertEquals("final", contentAtPosition(classContent,
				((DeclarationSourcePosition) withModifier.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withModifier.getPosition()).getModifierSourceEnd()));

		CtCatchVariable withMultipleCatch = elements.get(2);
		assertEquals("NullPointerException | java.lang.ArithmeticException e", contentAtPosition(classContent, withMultipleCatch.getPosition().getSourceStart(), withMultipleCatch.getPosition().getSourceEnd()));
		assertEquals("e", contentAtPosition(classContent,
				((DeclarationSourcePosition) withMultipleCatch.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withMultipleCatch.getPosition()).getNameEnd()));
		assertEquals("", contentAtPosition(classContent,
				((DeclarationSourcePosition) withMultipleCatch.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withMultipleCatch.getPosition()).getModifierSourceEnd()));

		foo = buildClass(Comment1.class);
		classContent = getClassContent(foo);
		elements = foo.getElements(new TypeFilter<>(CtCatchVariable.class));
		withoutModifier = elements.get(0);
		assertEquals("Exception ex", contentAtPosition(classContent, withoutModifier.getPosition().getSourceStart(), withoutModifier.getPosition().getSourceEnd()));
		assertEquals("ex", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameEnd()));
		assertEquals("", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceEnd()));


		foo = buildClass(VariableReferencesModelTest.class);
		classContent = getClassContent(foo);
		elements = foo.getElements(new TypeFilter<>(CtCatchVariable.class));
		withoutModifier = elements.get(0);
		assertEquals("IllegalArgumentException e", contentAtPosition(classContent, withoutModifier.getPosition().getSourceStart(), withoutModifier.getPosition().getSourceEnd()));
		assertEquals("e", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameEnd()));
		assertEquals("", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceEnd()));

		withoutModifier = elements.get(1);
		assertEquals("Exception /*7*/field", contentAtPosition(classContent, withoutModifier.getPosition().getSourceStart(), withoutModifier.getPosition().getSourceEnd()));
		assertEquals("field", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getNameEnd()));
		assertEquals("", contentAtPosition(classContent,
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceStart(),
				((DeclarationSourcePosition) withoutModifier.getPosition()).getModifierSourceEnd()));



	}
	@Test
	public void testArrayArgParameter() throws Exception {
		//contract: the parameter declared like `String arg[]`, `String[] arg` and `String []arg` has correct positions
		final CtType<?> foo = buildClass(ArrayArgParameter.class);
		String classContent = getClassContent(foo);

		{
			CtParameter<?> param = foo.getMethodsByName("m1").get(0).getParameters().get(0);
			assertEquals("String[] arg", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String[]", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m2").get(0).getParameters().get(0);
			assertEquals("String []arg", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String []", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m3").get(0).getParameters().get(0);
			assertEquals("String arg[]", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String arg[]", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m4").get(0).getParameters().get(0);
			assertEquals("/*1*/ String /*2*/ arg /*3*/ [ /*4*/ ]", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String /*2*/ arg /*3*/ [ /*4*/ ]", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m5").get(0).getParameters().get(0);
			assertEquals("/*1*/ String /*2*/ arg /*3*/ [ /*4 []*/ ] /* 5 */[][]/**/ []", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String /*2*/ arg /*3*/ [ /*4 []*/ ] /* 5 */[][]/**/ []", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m6").get(0).getParameters().get(0);
			assertEquals("String[]//[]\n" + 
					"			p[]", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String[]//[]\n" + 
					"			p[]", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m7").get(0).getParameters().get(0);
			assertEquals("String...arg", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String...", contentAtPosition(classContent, param.getType().getPosition()));
		}
		{
			CtParameter<?> param = foo.getMethodsByName("m8").get(0).getParameters().get(0);
			assertEquals("String[]...arg", contentAtPosition(classContent, param.getPosition()));
			assertEquals("String[]...", contentAtPosition(classContent, param.getType().getPosition()));
		}
	}
	
	@Test
	public void testExpressions() throws Exception {
		//contract: the expression including type casts has correct position which includes all brackets too
		final CtType<?> foo = buildClass(Expressions.class);
		String classContent = getClassContent(foo);
		List<CtInvocation<?>> statements = (List) foo.getMethodsByName("method").get(0).getBody().getStatements();

		int idx = 0;
		assertEquals("\"x\"", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("(\"x\")", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("(String)null", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("( String) ( (Serializable)(( (null ))))", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("(((String) null))", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("( /*c2*/\n" + 
				"				(\n" + 
				"						/*c3*/  String\n" + 
				"						/*c4*/) //c5\n" + 
				"				null /*c6*/\n" + 
				"				//c7\n" + 
				"				)", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("(List<?>) null", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		assertEquals("(List<List<Map<String,Integer>>>) null", contentAtPosition(classContent, statements.get(idx++).getArguments().get(0).getPosition()));
		
		//contract: check the position of expression without type casts
		{
			CtExpression<?> expr = statements.get(1).getArguments().get(0);
			assertEquals("(\"x\")", contentAtPosition(classContent, expr.getPosition()));
			//if there is no expression, then it uses primitive SourcePosition
			assertFalse(expr.getPosition() instanceof CompoundSourcePosition);
		}

		//contract: check the position of children of the most complex expression
		{
			CtExpression<?> expr = statements.get(3).getArguments().get(0);
			assertEquals("( String) ( (Serializable)(( (null ))))", contentAtPosition(classContent, expr.getPosition()));
			//if there is type cast in expression, then it uses CompoundSourcePosition
			assertTrue(expr.getPosition() instanceof CompoundSourcePosition);
			
			//contract: check the position of type casts
			assertEquals("( String)", contentAtPosition(classContent, expr.getTypeCasts().get(0).getPosition()));
			assertEquals("(Serializable)", contentAtPosition(classContent, expr.getTypeCasts().get(1).getPosition()));
			//contract: check the position of expression "name"
			CompoundSourcePosition compoundSourcePosition = (CompoundSourcePosition) expr.getPosition();
			assertEquals("(( (null )))", contentAtPosition(classContent, compoundSourcePosition.getNameStart(), compoundSourcePosition.getNameEnd()));
		}
	}
	@Test
	public void testCatchPosition() throws Exception {
		//contract: check the catch position
		final CtType<?> foo = buildClass(CatchPosition.class);
		String classContent = getClassContent(foo);
		CtTry tryStatement = (CtTry) foo.getMethodsByName("method").get(0).getBody().getStatement(0);
		{
			CtCatch catcher = tryStatement.getCatchers().get(0);
			CtCatchVariable<?> catchVar = catcher.getParameter();
			
			BodyHolderSourcePosition catcherPos = (BodyHolderSourcePosition) catcher.getPosition();
			DeclarationSourcePosition catchVarPos = (DeclarationSourcePosition) catchVar.getPosition();

			assertEquals(" catch (final IOException e) {\n" + 
					"			throw new RuntimeException(e);\n" + 
					"		}", contentAtPosition(classContent, catcherPos.getSourceStart(), catcherPos.getSourceEnd()));
			assertEquals("final IOException e", contentAtPosition(classContent, catchVarPos.getSourceStart(), catchVarPos.getSourceEnd()));

			assertEquals("", contentAtPosition(classContent, catcherPos.getModifierSourceStart(), catcherPos.getModifierSourceEnd()));
			assertEquals("final", contentAtPosition(classContent, catchVarPos.getModifierSourceStart(), catchVarPos.getModifierSourceEnd()));
			
			assertEquals(" catch (", contentAtPosition(classContent, catcherPos.getModifierSourceEnd() + 1, catcherPos.getNameStart() - 1));
			assertEquals(" IOException ", contentAtPosition(classContent, catchVarPos.getModifierSourceEnd() + 1, catchVarPos.getNameStart() - 1));

			assertEquals("final IOException e", contentAtPosition(classContent, catcherPos.getNameStart(), catcherPos.getNameEnd()));
			assertEquals("e", contentAtPosition(classContent, catchVarPos.getNameStart(), catchVarPos.getNameEnd()));

			assertEquals("{\n" + 
				"			throw new RuntimeException(e);\n" + 
				"		}", contentAtPosition(classContent, catcherPos.getBodyStart(), catcherPos.getBodyEnd()));
		}
		{
			CtCatch catcher = tryStatement.getCatchers().get(1);
			BodyHolderSourcePosition pos = (BodyHolderSourcePosition) catcher.getPosition();
			assertEquals(" /*1*/ catch/*2*/ ( /*3*/ final @Deprecated /*4*/ ClassCastException /*5*/ e /*6*/) /*7*/ {\n" + 
					"			throw new RuntimeException(e);\n" + 
					"		}", contentAtPosition(classContent, pos.getSourceStart(), pos.getSourceEnd()));
			assertEquals("", contentAtPosition(classContent, pos.getModifierSourceStart(), pos.getModifierSourceEnd()));
			assertEquals(" /*3*/ final @Deprecated /*4*/ ClassCastException /*5*/ e", contentAtPosition(classContent, pos.getNameStart(), pos.getNameEnd()));
			assertEquals("{\n" + 
					"			throw new RuntimeException(e);\n" + 
					"		}", contentAtPosition(classContent, pos.getBodyStart(), pos.getBodyEnd()));
		}
		{
			CtCatch catcher = tryStatement.getCatchers().get(2);
			BodyHolderSourcePosition pos = (BodyHolderSourcePosition) catcher.getPosition();
			assertEquals(" catch /* ignore this catch */\n" + 
					"		//and this catch too!\n" + 
					"		( /**catch it ( */\n" + 
					"				//catch (\n" + 
					"				OutOfMemoryError|RuntimeException e) {\n" + 
					"			throw new RuntimeException(e);\n" + 
					"		}", contentAtPosition(classContent, pos.getSourceStart(), pos.getSourceEnd()));
			assertEquals("", contentAtPosition(classContent, pos.getModifierSourceStart(), pos.getModifierSourceEnd()));
			assertEquals(" /**catch it ( */\n" + 
					"				//catch (\n" + 
					"				OutOfMemoryError|RuntimeException e", contentAtPosition(classContent, pos.getNameStart(), pos.getNameEnd()));
			assertEquals("{\n" + 
					"			throw new RuntimeException(e);\n" + 
					"		}", contentAtPosition(classContent, pos.getBodyStart(), pos.getBodyEnd()));
		}
	}
	@Test
	public void testEnumConstructorCallComment() throws Exception {
		//contract: check position the enum constructor call 
		final CtType<?> foo = buildClass(FooEnum.class);
		
		String classContent = getClassContent(foo);
		CtField<?> field = foo.getField("GET");
		{
			assertEquals("/**\n" + 
					"	 * Getter.\n" + 
					"	 * T get()\n" + 
					"	 */\n" + 
					"	GET(-1)", contentAtPosition(classContent, field.getPosition()));
			
			assertEquals("(-1)", contentAtPosition(classContent, field.getDefaultExpression().getPosition()));
		}
	}
	@Test
	public void testSwitchCase() throws Exception {
		//contract: check position of the statements of the case of switch
		final CtType<?> foo = buildClass(FooSwitch.class);
		
		String classContent = getClassContent(foo);
		CtSwitch<?> switchStatement = foo.getMethodsByName("m1").get(0).getBody().getStatement(0);
		int caseIdx = 0;
		{
			CtCase<?> caseStmt = switchStatement.getCases().get(caseIdx++);
			assertEquals("case C0:", contentAtPosition(classContent, caseStmt.getPosition()));
		}
		{
			CtCase<?> caseStmt = switchStatement.getCases().get(caseIdx++);
			assertEquals("case C1: \n" + 
					"			System.out.println();\n" + 
					"			break;", contentAtPosition(classContent, caseStmt.getPosition()));
		}
		{
			CtCase<?> caseStmt = switchStatement.getCases().get(caseIdx++);
			assertEquals("case C2: {\n" + 
					"			return 2;\n" + 
					"		}", contentAtPosition(classContent, caseStmt.getPosition()));
		}
		{
			CtCase<?> caseStmt = switchStatement.getCases().get(caseIdx++);
			assertEquals("case C3: {\n" + 
					"			return 2;\n" + 
					"		}", contentAtPosition(classContent, caseStmt.getPosition()));
		}
		{
			CtCase<?> caseStmt = switchStatement.getCases().get(caseIdx++);
			assertEquals("default:\n" + 
					"			System.out.println();\n" + 
					"			break;", contentAtPosition(classContent, caseStmt.getPosition()));
		}
	}
	@Test
	public void testFooForEach() throws Exception {
		//contract: check position of the for each position
		final CtType<?> foo = buildClass(FooForEach.class);
		
		String classContent = getClassContent(foo);
		List<CtForEach> stmts = (List) foo.getMethodsByName("m").get(0).getBody().getStatements();
		int caseIdx = 0;
		{
			CtForEach forEach = stmts.get(caseIdx++);
			assertEquals("for (String item : items) {}", contentAtPosition(classContent, forEach.getPosition()));
			assertEquals("String item", contentAtPosition(classContent, forEach.getVariable().getPosition()));
			assertEquals("items", contentAtPosition(classContent, forEach.getExpression().getPosition()));
		}
		{
			CtForEach forEach = stmts.get(caseIdx++);
			assertEquals("for (final String item : items) {\n" + 
					"		}", contentAtPosition(classContent, forEach.getPosition()));
			assertEquals("final String item", contentAtPosition(classContent, forEach.getVariable().getPosition()));
			assertEquals("items", contentAtPosition(classContent, forEach.getExpression().getPosition()));
		}
		{
			CtForEach forEach = stmts.get(caseIdx++);
			assertEquals("for (/*1*/ final @Deprecated /*2*/ String /*3*/ i /*4*/ : items) \n" + 
					"			this.getClass();", contentAtPosition(classContent, forEach.getPosition()));
			assertEquals("/*1*/ final @Deprecated /*2*/ String /*3*/ i", contentAtPosition(classContent, forEach.getVariable().getPosition()));
			assertEquals("items", contentAtPosition(classContent, forEach.getExpression().getPosition()));
		}
	}

	@Test
	public void testEndColumn() throws Exception {
		//contract: check end column
		final Factory build = build(FooStatement.class);
		final CtType<FooStatement> foo = build.Type().get(FooStatement.class);
		CtMethod<?> m = foo.getMethodsByName("m").get(0);
		SourcePosition pos = m.getPosition();
		assertEquals(7, pos.getLine());
		assertEquals(14, pos.getColumn());
		assertEquals(23, pos.getEndLine());
		assertEquals(2, pos.getEndColumn());
	}

	@Test
	public void testFirstLineColumn() {
		//contract: element, positioned before the first line separator in a file, should have correct column
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/TestSimpleClass.java"));
		CtType<?> type = build.Type().get("spoon.test.position.testclasses.TestSimpleClass");
		assertEquals(54, type.getPosition().getColumn());
	}

	@Test
	public void testSingleLineClassColumn() {
		//contract: element, positioned in a file without EOL, should have correct column
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/TestSingleLineClass.java"));
		CtType<?> type = build.Type().get("spoon.test.position.testclasses.TestSingleLineClass");
		assertEquals(54, type.getPosition().getColumn());
	}

	@Test
	public void testLabel() throws Exception {
		//contract: check position of labeled statement
		final Factory build = build(FooLabel.class);
		final CtType<FooLabel> foo = build.Type().get(FooLabel.class);
		String classContent = getClassContent(foo);
		List<CtStatement> stmts = foo.getMethodsByName("m").get(0).getBody().getStatements();
		int idx = 0;
		assertEquals("label1: while(x) {}", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
		assertEquals("label2: getClass();", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
		assertEquals("labelx: label3: new String();", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
		//contract: the nested label from previous line is not moved to next statement
		assertNull(stmts.get(idx).getLabel());
		assertEquals("getClass();", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
		assertEquals("label4: x = false;", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
		assertEquals("label5: /*c1*/ return;", contentAtPosition(classContent, stmts.get(idx++).getPosition()));
	}

	@Test
	public void testNestedLabels() throws Exception {
		//contract: check position of nested labeled statements
		final Factory build = build(FooLabel.class);
		final CtType<FooLabel> foo = build.Type().get(FooLabel.class);
		String classContent = getClassContent(foo);
		{
			CtStatement stmt = foo.getMethodsByName("m2").get(0).getBody().getStatement(0);
			assertTrue(stmt instanceof CtBlock);
			assertFalse(stmt.isImplicit());
			assertEquals("label1: {label2: while(x);}", contentAtPosition(classContent, stmt.getPosition()));
			assertEquals("label2: while(x);", contentAtPosition(classContent, ((CtBlock) stmt).getStatement(0).getPosition()));
		}
		{
			CtStatement stmt = foo.getMethodsByName("m2").get(0).getBody().getStatement(1);
			assertTrue(stmt instanceof CtBlock);
			assertTrue(stmt.isImplicit());
			assertEquals("label1: label2: while(x);", contentAtPosition(classContent, stmt.getPosition()));
			assertEquals("label2: while(x);", contentAtPosition(classContent, ((CtBlock) stmt).getStatement(0).getPosition()));
		}
		{
			CtStatementList stmts = ((CtSwitch<?>) foo.getMethodsByName("m5").get(0).getBody().getStatement(0)).getCases().get(0);

			CtStatement labelledEmtpyStatement = stmts.getStatement(0);
			assertTrue(labelledEmtpyStatement instanceof CtBlock);
			assertTrue(labelledEmtpyStatement.isImplicit());
			assertEquals("label:;", contentAtPosition(classContent, labelledEmtpyStatement.getPosition()));

			CtStatement multiLatbelledStatement = stmts.getStatement(1);
			assertTrue(multiLatbelledStatement instanceof CtBlock);
			assertTrue(multiLatbelledStatement.isImplicit());
			assertEquals("laval3: label1: label2: while(true);", contentAtPosition(classContent, multiLatbelledStatement.getPosition()));
		}
		{
			CtWhile stmt1 = (CtWhile) foo.getMethodsByName("m6").get(0).getBody().getStatement(0);
			assertEquals("labelW", stmt1.getLabel());
			CtTry stmt2 = ((CtBlock) stmt1.getBody()).getStatement(0);
			assertNull(stmt2.getLabel());
			assertEquals("try { label2: while(true); } finally {}", contentAtPosition(classContent, stmt2.getPosition()));
			CtWhile stmt3 = stmt2.getBody().getStatement(0);
			assertEquals("label2", stmt3.getLabel());
			assertEquals("label2: while(true);", contentAtPosition(classContent, stmt3.getPosition()));
		}
	}

	@Test
	public void testPackageDeclaration() throws Exception {
		//contract: check position of package declaration
		final Factory build = build(FooLabel.class);
		final CtType<FooLabel> foo = build.Type().get(FooLabel.class);
		String classContent = getClassContent(foo);
		CtPackageDeclaration packDecl = foo.getPosition().getCompilationUnit().getPackageDeclaration();
		assertEquals("package spoon.test.position.testclasses;", contentAtPosition(classContent, packDecl.getPosition()));
	}
	@Test
	public void testPackageDeclarationPosition() throws Exception {
		//contract: check position of package declaration after file comment
		final Factory build = build(BlockComment.class);
		final CtType<?> type = build.Type().get(BlockComment.class);
		String classContent = getClassContent(type);
		final CtPackageDeclaration packDecl = type.getPosition().getCompilationUnit().getPackageDeclaration();
		assertEquals("package spoon.test.comment.testclasses;", contentAtPosition(classContent, packDecl.getPosition()));
	}

	@Test
	public void testImportPosition() throws Exception {
		// contract: import position includes its comment
		CtType<?> type = ModelUtils.buildClass(cfg -> {
			cfg.getEnvironment().setCommentEnabled(true);
			cfg.getEnvironment().setAutoImports(true);
		}, CompilationUnitComments.class);
		String classContent = getClassContent(type);
		
		CtCompilationUnit cu = type.getPosition().getCompilationUnit();
		CtImport imprt = cu.getImports().get(0);
		
		assertEquals("//import comment\n" + 
				"import java.util.ArrayList;", contentAtPosition(classContent, imprt.getPosition()));
		assertEquals("java.util.ArrayList", contentAtPosition(classContent, imprt.getReference().getPosition()));
	}

	@Test
	public void testPackageDeclarationWithCommentPosition() throws Exception {
		// contract: package declaration position includes its comment. The file comment is not included
		CtType<?> type = ModelUtils.buildClass(cfg -> {
			cfg.getEnvironment().setCommentEnabled(true);
			cfg.getEnvironment().setAutoImports(true);
		}, CompilationUnitComments.class);
		String classContent = getClassContent(type);
		
		CtCompilationUnit cu = type.getPosition().getCompilationUnit();
		CtPackageDeclaration packageDecl = cu.getPackageDeclaration();
		
		assertEquals("/* package declaration comments*/\n" + 
				"package spoon.test.position.testclasses;", contentAtPosition(classContent, packageDecl.getPosition()));
		assertEquals("spoon.test.position.testclasses", contentAtPosition(classContent, packageDecl.getReference().getPosition()));
	}

	@Test
	public void testCommentedOutClass() {
		//contract: commented out class should not fail model build
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/TestCommentedOutClass.java"));
		CtType<?> type = build.Type().get("spoon.test.position.testclasses.TestCommentedOutClass");
		assertNull(type);
	}

	@Test
	public void testSourcePositionOfFieldReference() throws Exception {
		//contract: Source position of field reference is as expected
		CtType<?> foo = ModelUtils.buildClass(cfg -> {
			cfg.getEnvironment().setCommentEnabled(true);
			cfg.getEnvironment().setAutoImports(true);
		}, FooField.class);
		String classContent = getClassContent(foo);
		
		CtAssignment<?, ?> assignment =  (CtAssignment<?, ?>) foo.getMethodsByName("m").get(0).getBody().getStatements().get(0);
		CtFieldWrite<?> fieldWrite = (CtFieldWrite<?>) assignment.getAssigned();
		assertEquals("FooField.f.field2", contentAtPosition(classContent, fieldWrite.getPosition()));
		CtFieldRead<?> fieldRead = (CtFieldRead<?>) fieldWrite.getTarget();
		assertEquals("FooField.f", contentAtPosition(classContent, fieldRead.getPosition()));
		CtFieldReference<?> fieldRef2 = fieldRead.getVariable();
		assertEquals("f", contentAtPosition(classContent, fieldRef2.getPosition()));
	}

	@Test
	public void testPositionBuilderFailureIsCaugth() {
		//contract: parsing incorrect java code should not lead to a crash because of the position builder,
		//      but rather, incomplete position information.
		try {
			CtClass cl = Launcher.parseClass("class A { void foo() {");
			assertTrue(cl.getSimpleName().equals("A"));
			assertTrue(cl.getMethods().size() == 1);
		} catch(Exception e) {
			fail("Error while parsing incomplete class declaration");
		}
	}

	@Test
	public void testNoClasspathVariableAccessInInnerClass1() {
		// contract: creating variable access in no classpath should not break source position
		// https://github.com/INRIA/spoon/issues/3052
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/lambdas/InheritedClassesWithLambda1.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		List<CtClass> allClasses = model.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(3, allClasses.size());
		CtClass failing = allClasses.stream().filter(t -> t.getSimpleName().equals("Failing")).findFirst().get();
		assertEquals("InheritedClassesWithLambda1.java", failing.getPosition().getFile().getName());
		assertEquals(11, failing.getPosition().getLine());

		// in addition check that the variable reference is correct
		CtLambda lambda = model.getElements(new TypeFilter<>(CtLambda.class)).get(0);
		CtFieldRead field = (CtFieldRead) (((CtInvocation) lambda.getExpression()).getTarget());
		assertEquals("com.pkg.InheritedClassesWithLambda1.Failing", field.getVariable().getDeclaringType().toString());
	}

	@Test
	public void testNoClasspathVariableAccessInInnerClass2() {
		// contract: same as for testNoClasspathVariableAccessInInnerClass1,
		// but here we have inner class inside another inner class
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/lambdas/InheritedClassesWithLambda2.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		List<CtClass> allClasses = model.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(4, allClasses.size());
		CtClass failing = allClasses.stream().filter(t -> t.getSimpleName().equals("Failing")).findFirst().get();
		assertEquals("InheritedClassesWithLambda2.java", failing.getPosition().getFile().getName());
		assertEquals(11, failing.getPosition().getLine());

		// in addition check that the variable reference is correct
		CtLambda lambda = model.getElements(new TypeFilter<>(CtLambda.class)).get(0);
		CtFieldRead field = (CtFieldRead) (((CtInvocation) lambda.getExpression()).getTarget());
		assertEquals("InheritedClassesWithLambda2.OneMoreClass.Failing", field.getVariable().getDeclaringType().toString());
	}

	@Test
	public void testNoClasspathVariableAccessInInnerInterface() {
		// contract: same as for testNoClasspathVariableAccessInInnerClass1,
		// but here we have interface instead of class
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/lambdas/InheritedInterfacesWithLambda.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		List<CtInterface> allInterfaces = model.getElements(new TypeFilter<>(CtInterface.class));
		assertEquals(1, allInterfaces.size());
		CtInterface failing = allInterfaces.stream().filter(t -> t.getSimpleName().equals("Failing")).findFirst().get();
		assertEquals("InheritedInterfacesWithLambda.java", failing.getPosition().getFile().getName());
		assertEquals(3, failing.getPosition().getLine());

		// in addition check that the variable reference is correct
		CtLambda lambda = model.getElements(new TypeFilter<>(CtLambda.class)).get(0);
		CtFieldRead field = (CtFieldRead) (((CtInvocation) lambda.getExpression()).getTarget());
		assertEquals("InheritedInterfacesWithLambda.Failing", field.getVariable().getDeclaringType().toString());
	}

	@Test
	public void testLinePositionOkWithOneLineClassCode() {
		final Launcher launcher = new Launcher();

		launcher.addInputResource(new VirtualFile("public class A { public Object b() { return a < b; }}", "chunk.java"));
		launcher.buildModel();

		final List<CtElement> listOfBadPositionElements = launcher.getModel()
			.getElements(new TypeFilter<>(CtElement.class))
			.stream()
			// filtering out elements that do not have a line position
			.filter(elt -> elt.getPosition().isValidPosition())
			.collect(Collectors.toList());

		assertTrue("Some Spoon elements have an invalid line position",
			listOfBadPositionElements.stream().allMatch(elt -> elt.getPosition().getLine() == 1));
	}

	@Test
	public void testLambdaParameterPosition() {
		// contract: position of lambda parameter is correct
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/FooLambda.java"));
		final CtType<?> foo = build.Type().get(FooLambda.class);

		String classContent = getClassContent(foo);

		CtReturn<?> retStmt = (CtReturn<?>) foo.getMethodsByName("m").get(0).getBody().getStatement(0);
		CtLambda<?> lambdaExpr = (CtLambda<?>) retStmt.getReturnedExpression();
		CtParameter<?> param = lambdaExpr.getParameters().get(0);
		assertEquals("i", contentAtPosition(classContent, param.getPosition()));
	}

	@Test
	public void testLambdaParameterPosition1() {
		// contract: position of lambda parameter is correct
		final Factory build = build(new File("src/test/java/spoon/test/position/testclasses/MoreLambda.java"));
		final CtType<?> foo = build.Type().get(MoreLambda.class);

		String classContent = getClassContent(foo);

		List<CtLambda>  lambdas = foo.getElements(new TypeFilter(CtLambda.class));
		lambdas.stream().forEach(
				l -> l.getParameters().stream().forEach(
						p -> assertEquals(((CtParameter) p).getSimpleName(), contentAtPosition(classContent, ((CtParameter) p).getPosition()))
				)
		);
	}
}
