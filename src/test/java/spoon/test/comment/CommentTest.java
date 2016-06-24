package spoon.test.comment;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.comment.testclasses.BlockComment;
import spoon.test.comment.testclasses.InlineComment;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommentTest {

	private String newLine = System.getProperty("line.separator");

	private Factory getSpoonFactory()  {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/comment/testclasses/",
				"-o", "./target/spooned/",
				"-c"
		});
		return launcher.getFactory();
	}

	private CtComment createFakeComment(Factory factory,  String content) {
		return factory.Code().createInlineComment(content);
	}

	private CtComment createFakeBlockComment(Factory factory,  String content) {
		return factory.Code().createComment(content, CtComment.CommentType.BLOCK);
	}

	@Test
	public void testRemoveComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(InlineComment.class);
		List<CtComment> comments = type.getComments();
		assertEquals(3, comments.size());
		type.removeComment(comments.get(0));
		assertEquals(2, type.getComments().size());
	}

	@Test
	public void testInLineComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(InlineComment.class);
		String strType = type.toString();

		List<CtComment> comments = type.getElements(new TypeFilter<CtComment>(CtComment.class));
		// verify that the number of comment present in the AST is correct
		assertEquals(57, comments.size());

		// verify that all comments present in the AST is printed
		for (CtComment comment : comments) {
			if (comment.getCommentType() == CtComment.CommentType.FILE) {
				// the header of the file is not printed with the toString
				continue;
			}
			assertNotNull(comment.getParent());
			assertTrue(comment.toString() + ":" + comment.getParent() + " is not printed", strType.contains(comment.toString()));
		}

		assertEquals(3, type.getComments().size());
		assertEquals(CtComment.CommentType.FILE, type.getComments().get(0).getCommentType());
		assertEquals(createFakeComment(f, "comment class"), type.getComments().get(1));

		CtField<?> field = type.getField("field");
		assertEquals(2, field.getComments().size());
		assertEquals(createFakeComment(f, "Comment Field"),  field.getComments().get(0));
		assertEquals("// Comment Field" + newLine
				+ "// comment in field" + newLine
				+ "private int field = 10;", field.toString());

		CtAnonymousExecutable ctAnonymousExecutable = type.getAnonymousExecutables().get(0);
		assertEquals(1, ctAnonymousExecutable.getComments().size());
		assertEquals(createFakeComment(f, "comment static block"),  ctAnonymousExecutable.getComments().get(0));
		assertEquals(createFakeComment(f, "comment inside static"), ctAnonymousExecutable.getBody().getStatement(0));
		assertEquals("// comment static block" + newLine
				+ "static {" + newLine
				+ "    // comment inside static" + newLine
				+ "}", ctAnonymousExecutable.toString());

		CtConstructor constructor = type.getConstructor();
		assertEquals(1, constructor.getComments().size());
		assertEquals(createFakeComment(f, "comment constructor"),  constructor.getComments().get(0));
		// index 0 is the implicit super call
		assertEquals(createFakeComment(f, "Comment in constructor"), constructor.getBody().getStatement(1));
		assertEquals("// comment constructor" + newLine
				+ "public InlineComment() {" + newLine
				+ "    // Comment in constructor" + newLine
				+ "}", constructor.toString());

		CtMethod<Object> m = type.getMethod("m");
		assertEquals(1, m.getComments().size());
		assertEquals(createFakeComment(f, "comment method"),  m.getComments().get(0));
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
				+ "    (field)++;" + newLine
				+ "} ", ctIf.toString());

		CtConstructorCall ctConstructorCall = m1.getBody().getStatement(3);
		assertEquals(createFakeComment(f, "comment constructor call"), ctConstructorCall.getComments().get(0));
		assertEquals("// comment constructor call" + newLine
				+ "new spoon.test.comment.testclasses.InlineComment()", ctConstructorCall.toString());

		CtInvocation ctInvocation = m1.getBody().getStatement(4);
		assertEquals(createFakeComment(f, "comment invocation"), ctInvocation.getComments().get(0));
		assertEquals("// comment invocation" + newLine
				+ "spoon.test.comment.testclasses.InlineComment.this.m()", ctInvocation.toString());

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
				+ "} catch (java.lang.Exception e) {" + newLine
				+ "    // comment in catch" + newLine
				+ "}", ctTry.toString());

		CtSynchronized ctSynchronized = m1.getBody().getStatement(9);
		assertEquals(createFakeComment(f, "comment synchronized"), ctSynchronized.getComments().get(0));
		assertEquals("// comment synchronized" + newLine
				+ "synchronized(spoon.test.comment.testclasses.InlineComment.this) {" + newLine
				+ "    // comment in synchronized" + newLine
				+ "}", ctSynchronized.toString());

		CtLocalVariable ctLocalVariable1 = m1.getBody().getStatement(10);
		CtConditional ctConditional = (CtConditional) ctLocalVariable1.getDefaultExpression();
		assertEquals(createFakeComment(f, "comment after condition CtConditional"), ctConditional.getCondition().getComments().get(0));
		assertEquals(createFakeComment(f, "comment before then CtConditional"), ctConditional.getThenExpression().getComments().get(0));
		assertEquals(createFakeComment(f, "comment after then CtConditional"), ctConditional.getThenExpression().getComments().get(1));
		assertEquals(createFakeComment(f, "comment before else CtConditional"), ctConditional.getElseExpression().getComments().get(0));
		assertEquals(createFakeComment(f, "comment after else CtConditional"), ctLocalVariable1.getComments().get(0));
		assertEquals("java.lang.Double dou = i == 1// comment after condition CtConditional" + newLine
				+ " ? // comment before then CtConditional" + newLine
				+ "null// comment after then CtConditional" + newLine
				+ " : // comment before else CtConditional" + newLine
				+ "new java.lang.Double((j / ((double) ((i - 1)))))", ctLocalVariable1.toString());

		CtNewArray ctNewArray = (CtNewArray) ((CtLocalVariable) m1.getBody().getStatement(11)).getDefaultExpression();
		CtElement arrayValue = (CtElement) ctNewArray.getElements().get(0);
		assertEquals(createFakeComment(f, "comment before array value"), arrayValue.getComments().get(0));
		assertEquals(createFakeComment(f, "comment after array value"), arrayValue.getComments().get(1));

		CtReturn ctReturn = m1.getBody().getStatement(12);
		assertEquals(createFakeComment(f, "comment return"), ctReturn.getComments().get(0));
		assertEquals("// comment return" + newLine
				+ "return ", ctReturn.toString());


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
				+ "int i) throws java.lang.Error, java.lang.Exception {" + newLine
				+ "}", m2.toString());
	}

	@Test
	public void testBlockComment() {
		Factory f = getSpoonFactory();
		CtClass<?> type = (CtClass<?>) f.Type().get(BlockComment.class);
		String strType = type.toString();

		List<CtComment> comments = type.getElements(new TypeFilter<CtComment>(CtComment.class));
		// verify that the number of comment present in the AST is correct
		assertEquals(51, comments.size());

		// verify that all comments present in the AST is printed
		for (CtComment comment : comments) {
			if (comment.getCommentType() == CtComment.CommentType.FILE) {
				// the header of the file is not printed with the toString
				continue;
			}
			assertNotNull(comment.getParent());
			assertTrue(comment.toString() + ":" + comment.getParent() + " is not printed", strType.contains(comment.toString()));
		}

		assertEquals(4, type.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment class"), type.getComments().get(1));

		CtField<?> field = type.getField("field");
		assertEquals(2, field.getComments().size());
		assertEquals(createFakeBlockComment(f, "Comment Field"),  field.getComments().get(0));
		assertEquals("/* Comment Field */" + newLine
				+ "/* comment in field */" + newLine
				+ "private int field = 10;", field.toString());

		CtAnonymousExecutable ctAnonymousExecutable = type.getAnonymousExecutables().get(0);
		assertEquals(1, ctAnonymousExecutable.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment static block"),  ctAnonymousExecutable.getComments().get(0));
		assertEquals(createFakeBlockComment(f, "comment inside static"), ctAnonymousExecutable.getBody().getStatement(0));
		assertEquals("/* comment static block */" + newLine
				+ "static {" + newLine
				+ "    /* comment inside static */" + newLine
				+ "}", ctAnonymousExecutable.toString());

		CtConstructor constructor = type.getConstructor();
		assertEquals(1, constructor.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment constructor"),  constructor.getComments().get(0));
		// index 0 is the implicit super call
		assertEquals(createFakeBlockComment(f, "Comment in constructor"), constructor.getBody().getStatement(1));
		assertEquals("/* comment constructor */" + newLine
				+ "public BlockComment() {" + newLine
				+ "    /* Comment in constructor */" + newLine
				+ "}", constructor.toString());

		CtMethod<Object> m = type.getMethod("m");
		assertEquals(1, m.getComments().size());
		assertEquals(createFakeBlockComment(f, "comment method"),  m.getComments().get(0));
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
				+ "    (field)++;" + newLine
				+ "} ", ctIf.toString());

		CtConstructorCall ctConstructorCall = m1.getBody().getStatement(3);
		assertEquals(createFakeBlockComment(f, "comment constructor call"), ctConstructorCall.getComments().get(0));
		assertEquals("/* comment constructor call */" + newLine
				+ "new spoon.test.comment.testclasses.BlockComment()", ctConstructorCall.toString());

		CtInvocation ctInvocation = m1.getBody().getStatement(4);
		assertEquals(createFakeBlockComment(f, "comment invocation"), ctInvocation.getComments().get(0));
		assertEquals("/* comment invocation */" + newLine
				+ "spoon.test.comment.testclasses.BlockComment.this.m()", ctInvocation.toString());

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
				+ "synchronized(spoon.test.comment.testclasses.BlockComment.this) {" + newLine
				+ "    /* comment in synchronized */" + newLine
				+ "}", ctSynchronized.toString());

		CtReturn ctReturn = m1.getBody().getStatement(10);
		assertEquals(createFakeBlockComment(f, "comment return"), ctReturn.getComments().get(0));
		assertEquals("/* comment return */" + newLine
				+ "return ", ctReturn.toString());


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
				+ "int i) throws java.lang.Error, java.lang.Exception {" + newLine
				+ "}", m2.toString());
	}

	@Test
	public void testInsertNewComment () {
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
}
