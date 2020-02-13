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
package spoon;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import spoon.decompiler.Decompiler;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtConstructor;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DecompiledResourceTest {
	@Test
	public void testDecompiledResourceWithCFR() throws IOException {
		testDecompiledResource(null);
	}

	public void testDecompiledResource(Decompiler decompiler) throws IOException {

		File baseDir = new File("src/test/resources/jarLauncher");

		File pathToDecompiledRoot = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp");
		if(pathToDecompiledRoot.exists()) {
			FileUtils.deleteDirectory(pathToDecompiledRoot);
			pathToDecompiledRoot.delete();
		}
		File pathToDecompile = new File(pathToDecompiledRoot,"src/main/java");
		pathToDecompile.mkdirs();

		Launcher launcher = new Launcher();
		launcher.addInputResource(new DecompiledResource(baseDir.getAbsolutePath(), new String[]{}, decompiler, pathToDecompiledRoot.getPath()));

		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();

		//contract: all types are decompiled (Sources are produced for each type)
		assertEquals(5, model.getAllTypes().size());


		CtConstructor constructor = (CtConstructor) model.getRootPackage().getFactory().Type().get("se.kth.castor.UseJson").getTypeMembers().get(0);
		CtTry tryStmt = (CtTry) constructor.getBody().getStatement(1);
		CtLocalVariable var = (CtLocalVariable) tryStmt.getBody().getStatement(0);

		//contract: UseJson is correctly decompiled (UseJSON.java contains a local variable declaration)
		assertEquals("org.json.JSONObject", var.getType().getQualifiedName());
	}

}