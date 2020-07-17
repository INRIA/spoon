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
package spoon.test.comment;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.code.CtCommentImpl;
import spoon.test.comment.testclasses.BlockComment;
import spoon.test.comment.testclasses.Comment1;
import spoon.test.comment.testclasses.Comment2;
import spoon.test.comment.testclasses.CommentsOnStatements;
import spoon.test.comment.testclasses.InlineComment;
import spoon.test.comment.testclasses.JavaDocComment;
import spoon.test.comment.testclasses.JavaDocEmptyCommentAndTags;
import spoon.test.comment.testclasses.OtherJavaDoc;
import spoon.test.comment.testclasses.TestClassWithComments;
import spoon.test.comment.testclasses.WildComments;
import spoon.test.comment.testclasses.WindowsEOL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommentTest {

	private String newLine = System.getProperty("line.separator");

	private Factory getSpoonFactory() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[]{
				"-i", "./src/test/java/spoon/test/comment/testclasses/",
				"-o", "./target/spooned/",
				"-c"
		});
		return launcher.getFactory();
	}

	private CtComment createFakeComment(Factory factory, String content) {
		return factory.Code().createInlineComment(content);
	}

	private CtComment createFakeBlockComment(Factory factory, String content) {
		return factory.Code().createComment(content, CtComment.CommentType.BLOCK);
	}

	@Test
	public void testCombinedPackageInfoComment() {
		Factory f = getSpoonFactory();
		CtPackage p = f.Package().get("spoon.test.comment.testclasses");
		String l_content = ((JavaOutputProcessor) f.getEnvironment().getDefaultFileGenerator()).getPrinter().printPackageInfo(p);
		String EOL = System.getProperty("line.separator");
		assertEquals("/* comment1 */" + EOL
				+ "// comment2" + EOL
				+ "/**" + EOL
				+ " * Comment3" + EOL
				+ " */" + EOL
				+ "@java.lang.Deprecated" + EOL
				+ "package spoon.test.comment.testclasses;" + EOL, l_content);
	}

	private List<CtJavaDocTag> getTagByType(List<CtJavaDocTag> elements, CtJavaDocTag.TagType type) {
		List<CtJavaDocTag> output = new ArrayList<>();
		for (CtJavaDocTag element : elements) {
			if (element.getType() == type) {
				output.add(element);
			}
		}
		return output;
	}

	@Test
	public void testJavaDocCommentOnUnix() {
		//the EOL is taken from JavaDocComment.java, which is committed in git with linux \n
		//that is true on Windows too.
		String EOL = "\n";
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(JavaDocComment.class);
		this.testJavaDocComment(type, EOL);
	}

	@Test
	public void testJavadocShortAndLongComment() {
		// contract: in case we cannot determine if it is a short comment, we take the whole content
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(OtherJavaDoc.class);
		CtJavaDoc classJavaDoc = (CtJavaDoc) type.getComments().get(0);
		assertEquals("A short description without a proper end", classJavaDoc.getShortDescription());
		assertEquals("A short description without a proper end", classJavaDoc.getLongDescription());
	}

	@Test
	public void testJavaDocCommentOnMac() {
		String EOL = "\n";
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/comment/JavaDocComment.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();

		CtClass<?> type = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.comment.testclasses.JavaDocComment");
		this.testJavaDocComment(type, EOL);
	}

	private void testJavaDocComment(CtClass type, String EOL) {
		CtJavaDoc classJavaDoc = (CtJavaDoc) type.getComments().get(0);
		//contract: test that java doc is printed correctly
		String str = classJavaDoc.toString();
		StringTokenizer st = new StringTokenizer(str, System.getProperty("line.separator"));
		boolean first = true;
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (first) {
				//first
				first = false;
				assertEquals(3, line.length());
				assertEquals("/**", line);
			} else {
				if (st.hasMoreTokens()) {
					//in the middle
					assertTrue(line.length() >= 2);
					assertEquals(" *", line.substring(0, 2));
				} else {
					//last
					assertTrue(line.length() == 3);
					assertEquals(" */", line.substring(0, 3));
				}
			}
		}
		assertEquals("JavaDoc test class." + EOL + EOL
				+ "Long description", classJavaDoc.getContent());

		List<CtJavaDocTag> elements = type.getElements(new TypeFilter<>(CtJavaDocTag.class));
		assertEquals(8, elements.size());


		List<CtJavaDocTag> authorTags = getTagByType(elements, CtJavaDocTag.TagType.AUTHOR);
		assertEquals(1, authorTags.size());
		assertEquals("Thomas Durieux", authorTags.get(0).getContent());

		List<CtJavaDocTag> deprecatedTags = getTagByType(elements, CtJavaDocTag.TagType.DEPRECATED);
		assertEquals(1, deprecatedTags.size());
		assertEquals("", deprecatedTags.get(0).getContent());


		List<CtJavaDocTag> sinceTags = getTagByType(elements, CtJavaDocTag.TagType.SINCE);
		assertEquals(2, sinceTags.size());
		assertEquals("1.3", sinceTags.get(0).getContent());
		assertEquals("1.3", sinceTags.get(1).getContent());

		List<CtJavaDocTag> paramTags = getTagByType(elements, CtJavaDocTag.TagType.PARAM);
		assertEquals(1, paramTags.size());
		assertEquals("the parameters", paramTags.get(0).getContent());
		assertEquals("i", paramTags.get(0).getParam());

		CtJavaDocTag tagClone = paramTags.get(0).clone();
		assertEquals("the parameters", tagClone.getContent());
		assertEquals("i", tagClone.getParam());

		List<CtJavaDocTag> throwsTags = getTagByType(elements, CtJavaDocTag.TagType.THROWS);
		assertEquals(1, throwsTags.size());
		assertEquals("an exception", throwsTags.get(0).getContent());
		assertEquals("RuntimeException", throwsTags.get(0).getParam());


		assertEquals("JavaDoc test class.", classJavaDoc.getShortDescription());
		assertEquals("Long description", classJavaDoc.getLongDescription());

		CtJavaDocTag deprecatedTag = classJavaDoc.getTags().get(0);
		assertTrue(classJavaDoc.toString().contains("@deprecated"));
		classJavaDoc.removeTag(0);
		assertEquals(-1, classJavaDoc.toString().indexOf("@deprecated"));
		classJavaDoc.addTag(deprecatedTag);
		assertTrue(classJavaDoc.toString().contains("@deprecated"));
	}

	@Test
	public void testJavaDocEmptyCommentAndTag() {
		String EOL = "\n";	//the sources are checked out with \n even on Windows.

		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(JavaDocEmptyCommentAndTags.class);

		CtJavaDoc classJavaDoc = (CtJavaDoc) type.getComments().get(0);
		//contract: content is never null
		assertNotNull(classJavaDoc.getContent());
		//contract: empty content is ""
		assertEquals("", classJavaDoc.getContent());

		CtJavaDoc methodJavaDoc = (CtJavaDoc) type.getMethodsByName("m").get(0).getComments().get(1);
		//contract: content is never null
		assertNotNull(methodJavaDoc.getContent());
		//contract: empty content is ""
		assertEquals("", methodJavaDoc.getContent());
	}

	@Test
	public void testRemoveComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(InlineComment.class);
		List<CtComment> comments = type.getComments();
		List<CtComment> compilationUnitComments = type.getPosition().getCompilationUnit().getComments();
		assertEquals(6, comments.size() + compilationUnitComments.size());
		type.removeComment(comments.get(0));
		assertEquals(5, type.getComments().size() +  + compilationUnitComments.size());
	}

	@Test
	public void testInLineComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(InlineComment.class);
		String strType = type.toString();
		
		List<CtComment> compilationUnitComments = type.getPosition().getCompilationUnit().getComments();
		assertEquals(2, compilationUnitComments.size());
		assertEquals(CtComment.CommentType.BLOCK, compilationUnitComments.get(0).getCommentType());
		assertEquals("Top File\nLine 2", compilationUnitComments.get(0).getContent());
		assertEquals("Bottom File", compilationUnitComments.get(1).getContent());

		List<CtComment> comments = type.getElements(new TypeFilter<>(CtComment.class));
		// verify that the number of comment present in the AST is correct
		assertEquals(67, comments.size());

		// verify that all comments present in the AST are printed
		for (CtComment comment : comments) {
			assertNotNull(comment.getParent());
			assertTrue(comment.toString() + ":" + comment.getParent() + " is not printed", strType.contains(comment.toString()));
		}

		assertEquals(4, type.getComments().size());
		assertEquals(createFakeComment(f, "comment class"), type.getComments().get(0));

		CtField<?> field = type.getField("field");
		assertEquals(4, field.getComments().size());
		assertEquals(createFakeComment(f, "Comment Field"), field.getComments().get(0));
		assertEquals("// Comment Field" + newLine
				+ "// comment field 2" + newLine
				+ "// comment in field" + newLine
				+ "private int field = 10;// after field" + newLine, field.toString());

		CtAnonymousExecutable ctAnonymousExecutable = type.getAnonymousExecutables().get(0);
		assertEquals(1, ctAnonymousExecutable.getComments().size());
		assertEquals(createFakeComment(f, "comment static block"), ctAnonymousExecutable.getComments().get(0));
		assertEquals(createFakeComment(f, "comment inside static"), ctAnonymousExecutable.getBody().getStatement(0));
		assertEquals("// comment static block" + newLine
				+ "static {" + newLine
				+ "    // comment inside static" + newLine
				+ "}", ctAnonymousExecutable.toString());

		CtConstructor constructor = type.getConstructor();
		assertEquals(1, constructor.getComments().size());
		assertEquals(createFakeComment(f, "comment constructor"), constructor.getComments().get(0));
		// index 0 is the implicit super call
		assertEquals(createFakeComment(f, "Comment in constructor"), constructor.getBody().getStatement(1));
		assertEquals("// comment constructor" + newLine
				+ "public InlineComment() {" + newLine
				+ "    // Comment in constructor" + newLine
				+ "}", constructor.toString());

		CtMethod<Object> m = type.getMethod("m");
		assertEquals(1, m.getComments().size());
		assertEquals(createFakeComment(f, "comment method"), m.getComments().get(0));
		assertEquals(createFakeComment(f, "comment empty method block"), m.getBody().getStatement(0));
		assertEquals("// comment method" + newLine
				+ "public void m() {" + newLine
				+ "    // comment empty method block" + newLine
				+ "}", m.toString());


		CtMethod<Object> m1 = type.getMethod("m1");

		CtSwitch ctSwitch = m1.getBody().getStatement(0);
		assertEquals(createFakeComment(f, "comment switch"), ctSwitch.getComments().get(0));
		assertEquals("// comment switch" + newLine
				+ "switch (1) {" + newLine
				+ "    // before first case" + newLine
				+ "    case 0 :" + newLine
				+ "        // comment case 0: empty case" + newLine
				+ "    case 1 :" + newLine
				+ "        // comment case 1" + newLine
				+ "        int i = 0;" + newLine
				+ "    default :" + newLine
				+ "        // comment default" + newLine
				+ "}", ctSwitch.toString());

		CtFor ctFor = m1.getBody().getStatement(1);
		assertEquals(createFakeComment(f, "comment for"), ctFor.getComments().get(0));
		assertEquals("// comment for" + newLine
				+ "for (int i = 0; i < 10; i++) {" + newLine
				+ "    // comment for block" + newLine
				+ "}", ctFor.toString());

		CtIf ctIf = m1.getBody().getStatement(2);
		assertEquals(createFakeComment(f, "comment if"), ctIf.getComments().get(0));
		assertEquals("// comment if" + newLine
				+ "if ((1 % 2) == 0) {" + newLine
				+ "    // comment unary operator" + newLine
				+ "    field++;" + newLine
				+ "}", ctIf.toString());

		CtConstructorCall ctConstructorCall = m1.getBody().getStatement(3);
		assertEquals(createFakeComment(f, "comment constructor call"), ctConstructorCall.getComments().get(0));
		assertEquals("// comment constructor call" + newLine
				+ "new spoon.test.comment.testclasses.InlineComment()", ctConstructorCall.toString());

		CtInvocation ctInvocation = m1.getBody().getStatement(4);
		assertEquals(createFakeComment(f, "comment invocation"), ctInvocation.getComments().get(0));
		assertEquals("// comment invocation" + newLine
				+ "this.m()", ctInvocation.toString());

		CtLocalVariable ctLocalVariable = m1.getBody().getStatement(5);
		assertEquals(createFakeComment(f, "comment local variable"), ctLocalVariable.getComments().get(0));
		assertEquals("// comment local variable" + newLine
				+ "int i = 0", ctLocalVariable.toString());

		CtLocalVariable ctLocalVariable2 = m1.getBody().getStatement(6);
		assertEquals(createFakeComment(f, "comment multi assignments"), ctLocalVariable2.getComments().get(0));
		assertEquals("// comment multi assignments" + newLine
				+ "int j = 2", ctLocalVariable2.toString());

		CtDo ctDo = m1.getBody().getStatement(7);
		assertEquals(createFakeComment(f, "comment dowhile"), ctDo.getComments().get(0));
		assertEquals("// comment dowhile" + newLine
				+ "do {" + newLine
				+ "    // comment in do while" + newLine
				+ "    i++;" + newLine
				+ "    // comment end do while" + newLine
				+ "} while (i < 10 )", ctDo.toString());

		CtTry ctTry = m1.getBody().getStatement(8);
		assertEquals(createFakeComment(f, "comment try"), ctTry.getComments().get(0));
		assertEquals("// comment try" + newLine
				+ "try {" + newLine
				+ "    // comment in try" + newLine
				+ "    i++;" + newLine
				+ "}// between" + newLine
				+ "// try/catch" + newLine
				+ " catch (java.lang.Exception e) {" + newLine
				+ "    // comment in catch" + newLine
				+ "}", ctTry.toString());

		CtSynchronized ctSynchronized = m1.getBody().getStatement(9);
		assertEquals(createFakeComment(f, "comment synchronized"), ctSynchronized.getComments().get(0));
		assertEquals("// comment synchronized" + newLine
				+ "synchronized(this) {" + newLine
				+ "    // comment in synchronized" + newLine
				+ "}", ctSynchronized.toString());

		CtLocalVariable ctLocalVariable1 = m1.getBody().getStatement(10);
		CtConditional ctConditional = (CtConditional) ctLocalVariable1.getDefaultExpression();
		assertEquals(createFakeComment(f, "comment after condition CtConditional"), ctConditional.getCondition().getComments().get(0));
		assertEquals(createFakeComment(f, "comment before then CtConditional"), ctConditional.getThenExpression().getComments().get(0));
		assertEquals(createFakeComment(f, "comment after then CtConditional"), ctConditional.getThenExpression().getComments().get(1));
		assertEquals(createFakeComment(f, "comment before else CtConditional"), ctConditional.getElseExpression().getComments().get(0));
		assertEquals(createFakeComment(f, "comment after else CtConditional"), ctLocalVariable1.getComments().get(0));
		assertEquals("java.lang.Double dou = (i == 1// comment after condition CtConditional" + newLine
				+ ") ? // comment before then CtConditional" + newLine
				+ "null// comment after then CtConditional" + newLine
				+ " : // comment before else CtConditional" + newLine
				+ "new java.lang.Double(j / ((double) (i - 1)))// comment after else CtConditional" + newLine, ctLocalVariable1.toString());

		CtNewArray ctNewArray = (CtNewArray) ((CtLocalVariable) m1.getBody().getStatement(11)).getDefaultExpression();
		assertEquals(createFakeComment(f, "last comment at the end of array"), ctNewArray.getComments().get(0));

		CtElement arrayValue = (CtElement) ctNewArray.getElements().get(0);
		assertEquals(createFakeComment(f, "comment before array value"), arrayValue.getComments().get(0));
		assertEquals(createFakeComment(f, "comment after array value"), arrayValue.getComments().get(1));


		CtLocalVariable ctLocalVariableString = m1.getBody().getStatement(12);
		assertEquals(createFakeComment(f, "comment multi line string"), ((CtBinaryOperator) ((CtBinaryOperator) ctLocalVariableString.getDefaultExpression()).getRightHandOperand()).getLeftHandOperand().getComments().get(0));
		assertEquals("\"\" + (\"\"// comment multi line string" + newLine
				+ " + \"\")", ctLocalVariableString.getDefaultExpression().toString());

		ctLocalVariable1 = m1.getBody().getStatement(13);
		assertEquals("boolean c = (i == 1) ? // comment before then boolean CtConditional" + newLine
				+ "i == 1// comment after then boolean CtConditional" + newLine
				+ " : i == 2", ctLocalVariable1.toString());

		CtReturn ctReturn = m1.getBody().getStatement(14);
		assertEquals(createFakeComment(f, "comment return"), ctReturn.getComments().get(0));
		assertEquals("// comment return" + newLine
				+ "return", ctReturn.toString());


		CtMethod m2 = type.getMethodsByName("m2").get(0);
		assertEquals(6, m2.getComments().size());

		CtParameter ctParameter = (CtParameter) m2.getParameters().get(0);
		assertEquals(4, ctParameter.getComments().size());

		assertEquals("// comment before type" + newLine
				+ "// comment after parameter" + newLine
				+ "// comment before throws" + newLine
				+ "// comment before exception 1" + newLine
				+ "// comment before exception 2" + newLine
				+ "// comment before block" + newLine
				+ "public void m2(// comment before name" + newLine
				+ "// comment before parameters" + newLine
				+ "// comment before type parameter" + newLine
				+ "// comment before name parameter" + newLine
				+ "int i) throws java.lang.Exception, java.lang.Error {" + newLine
				+ "}", m2.toString());
	}

	@Test
	public void testBlockComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(BlockComment.class);
		String strType = type.toString();
		
		List<CtComment> compilationUnitComments = type.getPosition().getCompilationUnit().getComments();
		assertEquals(2, compilationUnitComments.size());
		assertEquals("Bottom File", compilationUnitComments.get(1).getContent());

		List<CtComment> comments = type.getElements(new TypeFilter<>(CtComment.class));
		// verify that the number of comment present in the AST is correct
		assertEquals(50, comments.size());

		// verify that all comments present in the AST are printed
		for (CtComment comment : comments) {
			assertNotNull(comment.getParent());
			assertTrue(comment.toString() + ":" + comment.getParent() + " is not printed", strType.contains(comment.toString()));
		}

		assertEquals(3, type.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment class"), type.getComments().get(0));

		CtField<?> field = type.getField("field");
		assertEquals(2, field.getComments().size());
		assertEquals(createFakeBlockComment(f, "Comment Field"), field.getComments().get(0));
		assertEquals("/* Comment Field */" + newLine
				+ "/* comment in field */" + newLine
				+ "private int field = 10;", field.toString());

		CtAnonymousExecutable ctAnonymousExecutable = type.getAnonymousExecutables().get(0);
		assertEquals(1, ctAnonymousExecutable.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment static block"), ctAnonymousExecutable.getComments().get(0));
		assertEquals(createFakeBlockComment(f, "comment inside static"), ctAnonymousExecutable.getBody().getStatement(0));
		assertEquals("/* comment static block */" + newLine
				+ "static {" + newLine
				+ "    /* comment inside static */" + newLine
				+ "}", ctAnonymousExecutable.toString());

		CtConstructor constructor = type.getConstructor();
		assertEquals(1, constructor.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment constructor"), constructor.getComments().get(0));
		// index 0 is the implicit super call
		assertEquals(createFakeBlockComment(f, "Comment in constructor"), constructor.getBody().getStatement(1));
		assertEquals("/* comment constructor */" + newLine
				+ "public BlockComment() {" + newLine
				+ "    /* Comment in constructor */" + newLine
				+ "}", constructor.toString());

		CtMethod<Object> m = type.getMethod("m");
		assertEquals(1, m.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment method"), m.getComments().get(0));
		assertEquals(createFakeBlockComment(f, "comment empty method block"), m.getBody().getStatement(0));
		assertEquals("/* comment method */" + newLine
				+ "public void m() {" + newLine
				+ "    /* comment empty method block */" + newLine
				+ "}", m.toString());


		CtMethod<Object> m1 = type.getMethod("m1");

		CtSwitch ctSwitch = m1.getBody().getStatement(0);
		assertEquals(createFakeBlockComment(f, "comment switch"), ctSwitch.getComments().get(0));
		assertEquals("/* comment switch */" + newLine
				+ "switch (1) {" + newLine
				+ "    /* before first case */" + newLine
				+ "    case 0 :" + newLine
				+ "        /* comment case 0: empty case */" + newLine
				+ "    case 1 :" + newLine
				+ "        /* comment case 1 */" + newLine
				+ "        int i = 0;" + newLine
				+ "    default :" + newLine
				+ "        /* comment default */" + newLine
				+ "}", ctSwitch.toString());

		CtFor ctFor = m1.getBody().getStatement(1);
		assertEquals(createFakeBlockComment(f, "comment for"), ctFor.getComments().get(0));
		assertEquals("/* comment for */" + newLine
				+ "for (int i = 0; i < 10; i++) {" + newLine
				+ "    /* comment for block */" + newLine
				+ "}", ctFor.toString());

		CtIf ctIf = m1.getBody().getStatement(2);
		assertEquals(createFakeBlockComment(f, "comment if"), ctIf.getComments().get(0));
		assertEquals("/* comment if */" + newLine
				+ "if ((1 % 2) == 0) {" + newLine
				+ "    /* comment unary operator */" + newLine
				+ "    field++;" + newLine
				+ "}", ctIf.toString());

		CtConstructorCall ctConstructorCall = m1.getBody().getStatement(3);
		assertEquals(createFakeBlockComment(f, "comment constructor call"), ctConstructorCall.getComments().get(0));
		assertEquals("/* comment constructor call */" + newLine
				+ "new spoon.test.comment.testclasses.BlockComment()", ctConstructorCall.toString());

		CtInvocation ctInvocation = m1.getBody().getStatement(4);
		assertEquals(createFakeBlockComment(f, "comment invocation"), ctInvocation.getComments().get(0));
		assertEquals("/* comment invocation */" + newLine
				+ "this.m()", ctInvocation.toString());

		CtLocalVariable ctLocalVariable = m1.getBody().getStatement(5);
		assertEquals(createFakeBlockComment(f, "comment local variable"), ctLocalVariable.getComments().get(0));
		assertEquals("/* comment local variable */" + newLine
				+ "int i = 0", ctLocalVariable.toString());

		CtLocalVariable ctLocalVariable2 = m1.getBody().getStatement(6);
		assertEquals(createFakeBlockComment(f, "comment multi assignments"), ctLocalVariable2.getComments().get(0));
		assertEquals("/* comment multi assignments */" + newLine
				+ "int j = 2", ctLocalVariable2.toString());

		CtDo ctDo = m1.getBody().getStatement(7);
		assertEquals(createFakeBlockComment(f, "comment dowhile"), ctDo.getComments().get(0));
		assertEquals("/* comment dowhile */" + newLine
				+ "do {" + newLine
				+ "    /* comment in do while */" + newLine
				+ "    i++;" + newLine
				+ "    /* comment end do while */" + newLine
				+ "} while (i < 10 )", ctDo.toString());

		CtTry ctTry = m1.getBody().getStatement(8);
		assertEquals(createFakeBlockComment(f, "comment try"), ctTry.getComments().get(0));
		assertEquals("/* comment try */" + newLine
				+ "try {" + newLine
				+ "    /* comment in try */" + newLine
				+ "    i++;" + newLine
				+ "} catch (java.lang.Exception e) {" + newLine
				+ "    /* comment in catch */" + newLine
				+ "}", ctTry.toString());

		CtSynchronized ctSynchronized = m1.getBody().getStatement(9);
		assertEquals(createFakeBlockComment(f, "comment synchronized"), ctSynchronized.getComments().get(0));
		assertEquals("/* comment synchronized */" + newLine
				+ "synchronized(this) {" + newLine
				+ "    /* comment in synchronized */" + newLine
				+ "}", ctSynchronized.toString());

		CtReturn ctReturn = m1.getBody().getStatement(10);
		assertEquals(createFakeBlockComment(f, "comment return"), ctReturn.getComments().get(0));
		assertEquals("/* comment return */" + newLine
				+ "return", ctReturn.toString());


		CtMethod m2 = type.getMethodsByName("m2").get(0);
		assertEquals(6, m2.getComments().size());

		CtParameter ctParameter = (CtParameter) m2.getParameters().get(0);
		assertEquals(4, ctParameter.getComments().size());

		assertEquals("/* comment before type */" + newLine
				+ "/* comment after parameter */" + newLine
				+ "/* comment before throws */" + newLine
				+ "/* comment before exception 1 */" + newLine
				+ "/* comment before exception 2 */" + newLine
				+ "/* comment before block */" + newLine
				+ "public void m2(/* comment before name */" + newLine
				+ "/* comment before parameters */" + newLine
				+ "/* comment before type parameter */" + newLine
				+ "/* comment before name parameter */" + newLine
				+ "int i) throws java.lang.Exception, java.lang.Error {" + newLine
				+ "}", m2.toString());

		// contract: one does not crash when setting a comment starting with '//' in a block comment
		// https://github.com/INRIA/spoon/issues/2887
		CtComment ctComment = m2.getComments().get(0);
		ctComment.setContent("// foo");
		assertEquals(CtComment.CommentType.BLOCK, ctComment.getCommentType());
		// it's a limitation, you cannot start with ''
		assertEquals("/* foo */", ctComment.toString());

		// workaround #1: the comment can start with '  //'
		ctComment.setContent("  // foo");
		assertEquals(CtComment.CommentType.BLOCK, ctComment.getCommentType());
		// it's a limitation, you cannot start with ''
		assertEquals("/* // foo */", ctComment.prettyprint());

		// workaround #2: one can cast and call '_setRawContent'
		// without setting the comment field through reflection
		((CtCommentImpl) ctComment)._setRawContent("// foo");
		assertEquals(CtComment.CommentType.BLOCK, ctComment.getCommentType());
		// it's a limitation, you cannot start with ''
		assertEquals("/* // foo */", ctComment.prettyprint());

	}

	@Test
	public void testInsertNewComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(InlineComment.class);

		CtMethod method = f.Core().createMethod();
		method.setSimpleName("newMethod");
		method.setBody(f.Core().createBlock());
		method.setType(f.Type().VOID_PRIMITIVE);

		type.addMethod(method);

		method.addComment(createFakeComment(f, "comment method"));
		method.getBody().addStatement(createFakeComment(f, "comment empty block"));

		assertEquals("// comment method" + newLine
				+ "void newMethod() {" + newLine
				+ "    // comment empty block" + newLine
				+ "}", method.toString());

		method.getBody().removeStatement(method.getBody().getStatement(0));

		CtLocalVariable<Integer> i = f.Code().createLocalVariable(f.Type().INTEGER_PRIMITIVE, "i", null);
		i.addComment(createFakeComment(f, "comment local variable"));
		method.getBody().addStatement(i);


		assertEquals("// comment method" + newLine
				+ "void newMethod() {" + newLine
				+ "    // comment local variable" + newLine
				+ "    int i;" + newLine
				+ "}", method.toString());
	}

	@Test
	public void testCoreFactory() {
		Factory spoonFactory = getSpoonFactory();

		CtComment comment = spoonFactory.Core().createComment();
		assertEquals("/*  */", comment.toString());

		comment.setContent("comment");
		assertEquals("/* comment */", comment.toString());

		comment.setCommentType(CtComment.CommentType.INLINE);
		assertEquals(CtComment.CommentType.INLINE, comment.getCommentType());
		assertEquals("// comment", comment.toString());

		comment.setCommentType(CtComment.CommentType.BLOCK);
		assertEquals(CtComment.CommentType.BLOCK, comment.getCommentType());
	}

	@Test
	public void testCodeFactory() {
		Factory spoonFactory = getSpoonFactory();

		CtComment comment = spoonFactory.Code().createComment("comment", CtComment.CommentType.INLINE);
		assertEquals("// comment", comment.toString());
		assertEquals(CtComment.CommentType.INLINE, comment.getCommentType());

		comment = spoonFactory.Code().createInlineComment("comment");
		assertEquals("// comment", comment.toString());
		assertEquals(CtComment.CommentType.INLINE, comment.getCommentType());
	}

	@Test
	public void testSnippedWithComments() {

		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);


		String content = "//class comment\n" + "class PR {\n"
				+ "/**\n * method javadoc comment */\n"
				+ "public java.io.File foo(String p) {\n"
				+ "/* method body comment*/\n"
				+ " return /*inline comment*/ null;"
				+ "}"
				+ "};\n"
				+ "// after class comment";

		JDTSnippetCompiler builder = new JDTSnippetCompiler(factory, content);

		builder.build();

		CtClass<?> clazz1 = (CtClass<?>) factory.Type().getAll().get(0);
		assertNotNull(clazz1);
		assertEquals(2, clazz1.getComments().size());
		assertEquals("class comment", clazz1.getComments().get(0).getContent());
		assertEquals("after class comment", clazz1.getComments().get(1).getContent());

		assertEquals(1, builder.getSnippetCompilationUnit().getDeclaredTypes().size());
		assertTrue(clazz1 == builder.getSnippetCompilationUnit().getDeclaredTypes().get(0));

		CtMethod<?> methodString = (CtMethod<?>) clazz1.getMethods().toArray()[0];
		// we don't call getSignature in order to encapsulate a little bit the changes
		// for the next time we will change the signature :-)
		assertEquals("foo", methodString.getSimpleName());
		assertEquals(1, methodString.getComments().size());
		assertEquals("method javadoc comment", methodString.getComments().get(0).getContent());

		CtReturn<?> returnSt = methodString.getBody().getStatement(0);

		assertEquals(2, returnSt.getComments().size());
		assertEquals("method body comment", returnSt.getComments().get(0).getContent());
		assertEquals("inline comment", returnSt.getComments().get(1).getContent());
	}

	@Test
	public void testAddCommentsToSnippet() {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);

		CtStatement statement = factory.Code().createCodeSnippetStatement("System.out.println(\"Caenorhabditis\")");
		CtComment comment = factory.createComment("My comment on my statement", CtComment.CommentType.INLINE);
		statement.addComment(comment);

		CtExpression expression = factory.Code().createCodeSnippetExpression("\"Caenorhabditis\" + \"Caenorhabditis\"");
		CtComment commentExpression = factory.createComment("My comment on my expression", CtComment.CommentType.INLINE);
		expression.addComment(commentExpression);

		assertEquals("// My comment on my statement" + newLine + "System.out.println(\"Caenorhabditis\")",
				statement.toString());

		assertEquals("// My comment on my expression" + newLine + "\"Caenorhabditis\" + \"Caenorhabditis\"",
				expression.toString());
	}

	@Test
	public void testDocumentationContract() throws Exception {
		// contract: all metamodel classes must be commented with an example.
		
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setComplianceLevel(13);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/");
		launcher.buildModel();

		StringBuffer codeElementsDocumentationPage = new StringBuffer();
		codeElementsDocumentationPage.append(IOUtils.toString(new FileReader("doc/code_elements_header.md")));
		codeElementsDocumentationPage.append("\n\n");
		launcher.getModel().getElements(new TypeFilter<>(CtInterface.class)).stream().forEach(x -> {

			assertNotNull(x.getSimpleName() + " has no documentation", x.getDocComment());
			assertTrue(x.getSimpleName() + " has no documentation", !x.getDocComment().isEmpty());

			// we only consider instantiable interfaces
			if (launcher.getModel().getElements(new AbstractFilter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					return (element instanceof CtNamedElement) && ((CtNamedElement) element).getSimpleName().equals(x.getSimpleName() + "Impl") && (element instanceof CtClass) && !((CtClass) element).hasModifier(ModifierKind.ABSTRACT);
				}
			}).isEmpty()) {
				return;
			}

			// we don't consider references
			if (x.getSimpleName().endsWith("Reference")) {
				return;
			}

			if (x.isSubtypeOf(launcher.getFactory().Type().get(CtStatement.class).getReference())
					|| x.isSubtypeOf(launcher.getFactory().Type().get(CtExpression.class).getReference())
					) {

				// no meaningful snippet
				if ("CtCodeSnippetStatement".equals(x.getSimpleName())) {
					return;
				}

				// no meaningful snippet
				if ("CtCodeSnippetExpression".equals(x.getSimpleName())) {
					return;
				}

				// no comment in snippet mode
				if ("CtComment".equals(x.getSimpleName())) {
					return;
				}

				// a statement in really rare cases
				if ("CtEnum".equals(x.getSimpleName())) {
					return;
				}

				// too hard to snippetize
				if ("CtAnnotationFieldAccess".equals(x.getSimpleName())) {
					return;
				}

				codeElementsDocumentationPage.append("### " + x.getSimpleName() + "\n");
				codeElementsDocumentationPage.append("[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/" + x.getQualifiedName().replace('.', '/') + ".html)\n\n");
				codeElementsDocumentationPage.append("```java" + "\n");
				Pattern p = Pattern.compile("<pre>(.*?)</pre>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE | Pattern.UNIX_LINES);
				Matcher m = p.matcher(x.getDocComment());
				m.find();
				do {
					String snippet = null;
					try {
						snippet = m.group(1);
					} catch (IllegalStateException e) {
						fail(x + " does not have code snippet");
					}
					snippet = StringEscapeUtils.unescapeHtml4(snippet);

					// it must compile
					CtElement el = launcher.getFactory().Code().createCodeSnippetStatement(snippet).compile();

					// the snippet contains this element
					assertTrue(snippet + " does not contain a " + x.getSimpleName(), !el.getElements(new TypeFilter<>(x.getActualClass())).isEmpty());

					codeElementsDocumentationPage.append(snippet + "\n");

				} while (m.find());
				codeElementsDocumentationPage.append("```" + "\n");
			}

		}
		);

		try {
			assertEquals("doc outdated, please commit doc/code_elements.md", IOUtils.toString(new FileReader("doc/code_elements.md")), codeElementsDocumentationPage.toString());
		} finally {
			IOUtils.write(codeElementsDocumentationPage.toString(), new FileOutputStream("doc/code_elements.md"));
		}
	}

	@Test
	public void testCommentsInComment1And2() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(Comment1.class);
		List<CtComment> comments = type.getElements(new TypeFilter<>(CtComment.class));
		assertEquals(4, comments.size());

		type = (CtClass<?>) f.Type().get(Comment2.class);
		comments = type.getElements(new TypeFilter<>(CtComment.class));
		assertEquals(2, comments.size());

		CtComment commentD = comments.get(1);
		assertEquals("D", commentD.getContent());
	}

	@Test
	public void testCommentsInResourcesWithWindowsEOL() throws IOException {
		//contract: the WindowsEOL.java contains MS Windows \r\n as EOL
		try (InputStream is = new FileInputStream(new File("./src/test/java/spoon/test/comment/testclasses/WindowsEOL.java"))) {
			int b;
			boolean lastWasCR = false;
			while ((b = is.read()) != -1) {
				if (lastWasCR) {
					//next must be LF
					assertEquals('\n', b);
					lastWasCR = false;
				}
				if (b == '\r') {
					lastWasCR = true;
				}
			}
		}
		final Launcher launcher = new Launcher();
		launcher.run(new String[]{
				"-i", "./src/test/java/spoon/test/comment/testclasses/WindowsEOL.java",
				"-o", "./target/spooned/",
				"-c"
		});
		Factory f = launcher.getFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(WindowsEOL.class);
		CtJavaDoc classJavaDoc = (CtJavaDoc) type.getComments().get(0);
		//contract: test that java doc is printed correctly
		String str = classJavaDoc.toString();
		StringTokenizer st = new StringTokenizer(str, System.getProperty("line.separator"));
		boolean first = true;
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (first) {
				//first
				first = false;
				assertEquals(3, line.length());
				assertEquals("/**", line);
			} else {
				if (st.hasMoreTokens()) {
					//in the middle
					assertTrue(line.length() >= 2);
					assertEquals(" *", line.substring(0, 2));
				} else {
					//last
					assertTrue(line.length() == 3);
					assertEquals(" */", line.substring(0, 3));
				}
			}
		}
		//This test passes on MS Windows too - why spoon uses `\n` on MS Windows too?
		assertEquals("This file contains MS Windows EOL.\n"
				+ "It is here to test whether comments are printed well\n"
				+ "in this case", classJavaDoc.getContent());
	}

	@Test
	public void testWildComments() {
		//contract: tests that value of comment is correct even for wild combinations of characters. See WildComments class for details
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(WildComments.class);
		List<CtLiteral<String>> literals = (List) ((CtNewArray<?>) type.getField("comments").getDefaultExpression()).getElements();
		assertTrue(literals.size() > 10);
		/*
		 * each string literal has a comment and string value, which defines expected value of its comment
		 */
		for (CtLiteral<String> literal : literals) {
			assertEquals(1, literal.getComments().size());
			CtComment comment = literal.getComments().get(0);
			String expected = literal.getValue();
			assertEquals(literal.getPosition().toString(), expected, comment.getContent());
		}
	}

	@Test
	public void testEnumValueComment() {
		// contract: enum value comments are taken into account

		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/EnumClass.java");
		launcher.getEnvironment().setCommentEnabled(true);
		CtModel model = launcher.buildModel();

		CtEnum<?> ctEnum = model.getElements(new TypeFilter<>(CtEnum.class)).get(0);
		List<CtEnumValue<?>> enumValues = ctEnum.getEnumValues();

		assertEquals(4, enumValues.size());

		CtEnumValue firstEnumValue = enumValues.get(0);
		assertEquals("FAIL", firstEnumValue.getSimpleName());

		List<CtComment> comments = firstEnumValue.getComments();
		assertEquals(1, comments.size());
		assertTrue(comments.get(0) instanceof CtJavaDoc);
		assertEquals("Throw {@link SpoonException} if a conflict happens, it is the default in most cases. But there are some standard Pattern builder algorithms (mainly these which deals with legacy Templates), which are using the other modes.", comments.get(0).getContent());

		CtEnumValue<?> thirdEnumValue = enumValues.get(2);
		assertEquals("KEEP_OLD_NODE", thirdEnumValue.getSimpleName());

		comments = thirdEnumValue.getComments();
		assertEquals(1, comments.size());
		assertTrue(comments.get(0) instanceof CtJavaDoc);
		assertEquals("Keep old {@link RootNode} and ignore requests to add new {@link RootNode}", comments.get(0).getContent());
	}


	@Test
	public void testBug2209() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[]{
				"-i", "src/test/resources/ConfigPlugin.java"
		});
		// the comment should be associated to the expression in the annotation
		assertEquals("// prevent users from disabling", launcher.getFactory().Type().get("net.runelite.client.plugins.config.ConfigPlugin").getAnnotations().get(0).filterChildren(new TypeFilter<>(CtComment.class)).list().get(0).toString());

	}


	@Test
	public void testInlineCommentIfBlock() {
		// contract: when creating an inline comment from a string with line separators, it throws an exception to create block comment
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/WithIfBlock.java");
		launcher.getEnvironment().setCommentEnabled(true);

		CtModel model = launcher.buildModel();

		List<CtIf> ctIfs = model.getElements(new TypeFilter<>(CtIf.class));

		assertEquals(1, ctIfs.size());
		CtIf ctIf = ctIfs.get(0);
		try {
			CtComment ctComment = launcher.getFactory().createInlineComment(ctIf.toString());
			fail("Exception should have been thrown");
		} catch (SpoonException e) {
			assertTrue(e.getMessage().contains("consider using a block comment"));
		}
	}

	@Test
	public void testStatementComments() {
		// contract: the statements have their comment even if they are nested in another block
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/CommentsOnStatements.java");
		launcher.getEnvironment().setCommentEnabled(true);

		CtModel model = launcher.buildModel();

		List<CtStatement> statements = launcher.getFactory().Type().get(CommentsOnStatements.class).getMethodsByName("m1").get(0).getBody().getStatements();
		assertEquals(2, statements.size());
		CtIf ifStatement = (CtIf) statements.get(0);
		assertEquals(Arrays.asList("// c1"), getCommentStrings(ifStatement));
		assertEquals(Arrays.asList("// c2 belongs to toto"), getCommentStrings(((CtBlock) ifStatement.getThenStatement()).getStatement(0)));
		CtIf if2Statement = (CtIf) ((CtBlock) ifStatement.getElseStatement()).getStatement(0);
		assertEquals("// c3 belongs to getClass" + newLine
				+ "this.getClass()", ((CtBlock) if2Statement.getThenStatement()).getStatement(0).toString());
		assertEquals(Arrays.asList("// c3 belongs to getClass"), getCommentStrings(((CtBlock) if2Statement.getThenStatement()).getStatement(0)));

		assertEquals(Arrays.asList("// c4 comment of return"), getCommentStrings(statements.get(1)));
	}

	private List<String> getCommentStrings(CtElement ele) {
		return ele.getComments().stream().map(Object::toString).collect(Collectors.toList());
	}

	@Test
	public void testCommentAssociationAndPrettyPrint() {
		//contract: all comments, which are before an element are assigned to that element
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/TestClassWithComments.java");
		launcher.getEnvironment().setCommentEnabled(true);

		CtModel model = launcher.buildModel();

		Factory factory = launcher.getFactory();
		CtType<?> cls = factory.Type().get(TestClassWithComments.class);
		
		assertEquals(1, cls.getComments().size());
		CtType<?> nestedIface = cls.getNestedType("testInterface");
		assertEquals(4, nestedIface.getComments().size());
		CtMethod<?> method = nestedIface.getMethodsByName("mytest").get(0);
		assertEquals(1, method.getComments().size());
	}

	@Test
	public void testCommentGetRawContent() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/comment/JavaDocComment.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();

		CtClass<?> type = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.comment.testclasses.JavaDocComment");
		//contract: getContent always returns cleaned comment content with \n as EOL
		assertEquals("JavaDoc test class.\n" + 
				"\n" + 
				"Long description", type.getComments().get(0).getContent());
		// contract: return the full original comment with prefix and suffix, incl. the original EOL (\r as EOL here)
		assertEquals("/**\r" + 
				" * JavaDoc test class.\r" + 
				" *\r" + 
				" * Long description\r" + 
				" *\r" + 
				" * @deprecated\r" + 
				" * @since 1.3\r" + 
				" * @author Thomas Durieux\r" + 
				" * @version 1.0\r" + 
				" */", type.getComments().get(0).getRawContent());
	}

	@Test
	public void testEmptyStatementComments() {
		//contract: model building should not produce NPE, comments should exist
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/EmptyStatementComments.java");
		launcher.getEnvironment().setCommentEnabled(true);

		List<CtMethod<?>> methods = launcher.buildModel().getElements(new TypeFilter<>(CtMethod.class));

		List<CtIf> conditions = methods.get(0).getElements(new TypeFilter<>(CtIf.class));
		assertEquals("comment", conditions.get(0).getComments().get(0).getContent());
		assertEquals("comment", conditions.get(1).getComments().get(0).getContent());

		List<CtSwitch<?>> switches = methods.get(1).getElements(new TypeFilter<>(CtSwitch.class));
		assertEquals("commentInline", switches.get(0).getComments().get(0).getContent());
		assertEquals("commentBlock", switches.get(1).getComments().get(0).getContent());
	}

	@Test
	public void testAnnotationTypeComment() {
		//contract: comments in annotations should be properly added to the AST
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/AnnotationTypeComments.java");
		launcher.getEnvironment().setCommentEnabled(true);
		CtModel model = launcher.buildModel();

		List<CtAnnotationType> annotations = model.getElements(new TypeFilter<>(CtAnnotationType.class));
		assertEquals("comment1", annotations.get(0).getComments().get(0).getContent());
		assertTrue(annotations.get(1).getComments().isEmpty());

		Object[] annotationMethods = annotations.get(1).getAnnotationMethods().toArray();
		assertEquals("comment1", ((CtAnnotationMethod) annotationMethods[0]).getComments().get(0).getContent());
		assertEquals("comment2", ((CtAnnotationMethod) annotationMethods[0]).getComments().get(1).getContent());
		assertEquals("comment3", ((CtAnnotationMethod) annotationMethods[1]).getComments().get(0).getContent());
		assertEquals("comment4", ((CtAnnotationMethod) annotationMethods[3]).getComments().get(0).getContent());
	}

  public void testLambdaComments() {
		//contract: comments in lambdas should be properly added to the AST
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/LambdaComments.java");
		launcher.getEnvironment().setCommentEnabled(true);
		CtModel model = launcher.buildModel();

		List<CtLambda> lambdas = model.getElements(new TypeFilter<>(CtLambda.class));
		assertEquals("comment", lambdas.get(0).getExpression().getComments().get(0).getContent());
		assertEquals("comment", lambdas.get(1).getExpression().getComments().get(0).getContent());
		assertEquals("comment", lambdas.get(2).getExpression().getComments().get(0).getContent());
		assertEquals("comment", lambdas.get(3).getParent().getComments().get(0).getContent());
		assertEquals("", lambdas.get(4).getExpression().getComments().get(0).getContent());

		assertEquals("comment", lambdas.get(5).getBody().getStatement(0).getComments().get(0).getContent());
		assertEquals("comment", lambdas.get(6).getComments().get(0).getContent());
		assertEquals("comment", lambdas.get(7).getBody().getStatement(0).getComments().get(0).getContent());

		assertTrue(lambdas.get(8).getParent().getComments().isEmpty());
		assertTrue(lambdas.get(8).getComments().isEmpty());
		assertTrue(lambdas.get(8).getExpression().getComments().isEmpty());
		assertEquals(1, ((CtParameter) lambdas.get(8).getParameters().get(0)).getComments().size());
		assertEquals(1, ((CtParameter) lambdas.get(8).getParameters().get(1)).getComments().size());
		assertEquals("param1", ((CtParameter) lambdas.get(8).getParameters().get(0)).getComments().get(0).getContent());
		assertEquals("param2", ((CtParameter) lambdas.get(8).getParameters().get(1)).getComments().get(0).getContent());

		assertEquals("param1", ((CtParameter) lambdas.get(9).getParameters().get(0)).getComments().get(0).getContent());
		assertEquals("param2", ((CtParameter) lambdas.get(9).getParameters().get(1)).getComments().get(0).getContent());

		assertEquals("param1", ((CtParameter) lambdas.get(10).getParameters().get(0)).getComments().get(0).getContent());
		assertEquals("param2", ((CtParameter) lambdas.get(10).getParameters().get(1)).getComments().get(0).getContent());

		assertTrue(((CtParameter) lambdas.get(11).getParameters().get(0)).getComments().isEmpty());
		assertEquals("param2", ((CtParameter) lambdas.get(11).getParameters().get(1)).getComments().get(0).getContent());

		assertEquals("param1", ((CtParameter) lambdas.get(12).getParameters().get(0)).getComments().get(0).getContent());
		assertEquals("param1", ((CtParameter) lambdas.get(12).getParameters().get(0)).getComments().get(1).getContent());
		assertEquals("param2", ((CtParameter) lambdas.get(12).getParameters().get(1)).getComments().get(0).getContent());
		assertEquals("param2", ((CtParameter) lambdas.get(12).getParameters().get(1)).getComments().get(1).getContent());
	}

	@Test
	public void testCatchComments() {
		//contract: comments in catch should be properly added to the AST
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/comment/testclasses/CatchComments.java");
		launcher.getEnvironment().setCommentEnabled(true);
		CtModel model = launcher.buildModel();

		List<CtCatch> catches = model.getElements(new TypeFilter<>(CtCatch.class));
		assertEquals(1, catches.get(0).getComments().size());
		assertEquals("first comment", catches.get(0).getComments().get(0).getContent());
		assertEquals(1, catches.get(0).getBody().getComments().size());
		assertEquals("second comment", catches.get(0).getBody().getComments().get(0).getContent());
	}
}
