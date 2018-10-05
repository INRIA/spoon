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
package spoon.test.template;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.getOptimizedString;

import java.io.File;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.ReturnReplaceTemplate;
import spoon.testing.utils.ModelUtils;

public class TemplateReplaceReturnTest {

	@Test
	public void testReturnReplaceTemplate() {
		//contract: the template engine supports replace of `return _param_.S()` by `<CtBlock>`
		Launcher launcher = new Launcher();
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ReturnReplaceTemplate.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtBlock<String> model = (CtBlock) factory.Class().get(ReturnReplaceTemplate.class).getMethod("sample").getBody();
		
		CtClass<?> resultKlass = factory.Class().create(factory.Package().getOrCreate("spoon.test.template"), "ReturnReplaceResult");
		new ReturnReplaceTemplate(model).apply(resultKlass);
		assertEquals("{ if (((java.lang.System.currentTimeMillis()) % 2L) == 0) { return \"Panna\"; }else { return \"Orel\"; }}", getOptimizedString(resultKlass.getMethod("method").getBody()));
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt(new File("./target/spooned/spoon/test/template/ReturnReplaceResult.java"), 8);
	}

	@Test
	public void testNoReturnReplaceTemplate() {
		//contract: the template engine supports replace of return expression by `<CtExpression>`
		Launcher launcher = new Launcher();
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/ReturnReplaceTemplate.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtExpression<String> model = factory.createLiteral("AStringLiteral");
		
		CtClass<?> resultKlass = factory.Class().create(factory.Package().getOrCreate("spoon.test.template"), "ReturnReplaceResult");
		new ReturnReplaceTemplate(model).apply(resultKlass);
		assertEquals("{ return \"AStringLiteral\";}", getOptimizedString(resultKlass.getMethod("method").getBody()));
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt(new File("./target/spooned/spoon/test/template/ReturnReplaceResult.java"), 8);
	}

}
