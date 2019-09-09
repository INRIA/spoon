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
package spoon.test.javadoc;

import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonAPI;
import spoon.javadoc.internal.JavadocInlineTag;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtJavaDocTag.TagType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.imports.ImportTest;
import spoon.test.javadoc.testclasses.Bar;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spoon.testing.utils.Check.assertCtElementEquals;

public class JavaDocTest {


	@Test
	public void testJavaDocReprint() {
		SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setCopyResources(false);
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/");
		launcher.setSourceOutputDirectory("./target/spooned/");
		launcher.run();
		Factory factory = launcher.getFactory();
		CtClass<?> aClass = factory.Class().get(Bar.class);

		assertEquals("public class Bar {" + System.lineSeparator()
				+ "    /**" + System.lineSeparator()
				+ "     * Creates an annotation type." + System.lineSeparator()
				+ "     *" + System.lineSeparator()
				+ "     * @param owner" + System.lineSeparator()
				+ "     * \t\tthe package of the annotation type" + System.lineSeparator()
				+ "     * @param simpleName" + System.lineSeparator()
				+ "     * \t\tthe name of annotation" + System.lineSeparator()
				+ "     */" + System.lineSeparator()
				+ "    public <T> CtAnnotationType<?> create(CtPackage owner, String simpleName) {" + System.lineSeparator()
				+ "        return null;" + System.lineSeparator()
				+ "    }" + System.lineSeparator()
				+ "}", ImportTest.printByPrinter(aClass));

		// contract: getDocComment never returns null, it returns an empty string if no comment
		assertEquals("", aClass.getDocComment());

		// contract: getDocComment returns the comment content together with the tag content
		CtMethod<?> method = aClass.getMethodsByName("create").get(0);
		assertEquals("Creates an annotation type." + System.lineSeparator() + System.lineSeparator()
				+ "@param owner" + System.lineSeparator()
				+ "\t\tthe package of the annotation type" + System.lineSeparator()
				+ "@param simpleName" + System.lineSeparator()
				+ "\t\tthe name of annotation" + System.lineSeparator()
				, method.getDocComment());

		CtJavaDoc ctJavaDoc = method.getComments().get(0).asJavaDoc();
		assertEquals(2, ctJavaDoc.getTags().size());

		assertEquals(2, ctJavaDoc.clone().getTags().size());
		assertCtElementEquals(ctJavaDoc, ctJavaDoc.clone());

		// contract: one can parse the content of the javadoc
		CtClass<?> bar = factory.Class().get("spoon.test.javadoc.testclasses.Foo");
		CtJavaDoc javadoc = (CtJavaDoc) bar.getComments().get(0);
		assertEquals(2, javadoc.getJavadocElements().size());
		assertEquals("This is a ", javadoc.getJavadocElements().get(0).toText());
		JavadocInlineTag link = (JavadocInlineTag) javadoc.getJavadocElements().get(1);
		assertEquals("{@link Exception}", link.toText());
		assertEquals("link", link.getName());
		assertEquals("Exception", link.getContent());


	}

	@Test
	public void testJavadocNotPresentInAST() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setCommentEnabled(false);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/");
		launcher.run();

		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null) {
					assertEquals(0, element.getComments().size());
				}
				super.scan(element);
			}

			@Override
			public void visitCtComment(CtComment comment) {
				fail("Shouldn't have comment in the model.");
				super.visitCtComment(comment);
			}
		}.scan(launcher.getModel().getRootPackage());
	}

	@Test
	public void testBugSetContent() {
		// contract: call to setContent directly should also set tags.
		CtJavaDoc j = (CtJavaDoc) new Launcher().getFactory().createComment("/** sd\n@see foo */", CtComment.CommentType.JAVADOC);
		assertEquals("sd", j.getLongDescription());
		assertEquals(1, j.getTags().size());
		assertEquals("foo", j.getTags().get(0).getContent());
	}

	@Test
	public void testTagsParameters() {
		// contract: @throws and @exception should have proper params
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/A.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();

		List<CtJavaDoc> javadocs = model.getElements(new TypeFilter<>(CtJavaDoc.class));

		CtJavaDocTag throwsTag = javadocs.get(0).getTags().get(0);
		CtJavaDocTag exceptionTag = javadocs.get(1).getTags().get(0);

		assertEquals("IllegalArgumentException", throwsTag.getParam());
		assertEquals("FileNotFoundException", exceptionTag.getParam());
	}

	@Test
	public void testJavadocTagNames() {
		//contract: we should handle all possible javadoc tags properly
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/javadoc/testclasses/B.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();

		CtType<?> type = model.getAllTypes().stream().findFirst().get();
		assertEquals(TagType.VERSION, type.getElements(new TypeFilter<>(CtEnum.class)).get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.AUTHOR, type.getMethodsByName("m1").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.DEPRECATED, type.getMethodsByName("m2").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.EXCEPTION, type.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.PARAM, type.getMethodsByName("m4").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.RETURN, type.getMethodsByName("m5").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.SEE, type.getMethodsByName("m6").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.SERIAL, type.getField("m7").getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.SERIAL_DATA, type.getMethodsByName("m8").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.SERIAL_FIELD, type.getField("m9").getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.SINCE, type.getMethodsByName("m10").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.THROWS, type.getMethodsByName("m11").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
		assertEquals(TagType.UNKNOWN, type.getMethodsByName("m12").get(0).getElements(new TypeFilter<>(CtJavaDoc.class)).get(0).getTags().get(0).getType());
	}

}
