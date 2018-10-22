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

import java.io.File;
import java.lang.annotation.ElementType;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.FileSystemFile;
import spoon.test.template.testclasses.EnumAccessTemplate;
import spoon.testing.utils.ModelUtils;

public class TemplateEnumAccessTest {

	@Test
	public void testEnumAccessTest() {
		//contract: the template engine supports enum value access substitution
		Launcher launcher = new Launcher();
		launcher.addTemplateResource(new FileSystemFile("./src/test/java/spoon/test/template/testclasses/EnumAccessTemplate.java"));

		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtClass<?> resultKlass = factory.Class().create(factory.Package().getOrCreate("spoon.test.template"), "EnumAccessResult");
		new EnumAccessTemplate(ElementType.FIELD, launcher.getFactory()).apply(resultKlass);
		assertEquals("java.lang.annotation.ElementType.FIELD.name()", resultKlass.getMethod("method").getBody().getStatement(0).toString());
		launcher.setSourceOutputDirectory(new File("./target/spooned/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
		ModelUtils.canBeBuilt(new File("./target/spooned/spoon/test/template/EnumAccessResult.java"), 8);
	}

}
