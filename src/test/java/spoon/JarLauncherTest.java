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

import org.junit.Test;
import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.FernflowerDecompiler;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtConstructor;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JarLauncherTest {

	@Test
	public void testJarLauncher() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File pom = new File(baseDir, "pom.xml");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");

		File pathToDecompiledRoot = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp");
		if(pathToDecompiledRoot.exists()) {
			pathToDecompiledRoot.delete();
		}
		File pathToDecompile = new File(pathToDecompiledRoot,"src/main/java");
		pathToDecompile.mkdirs();

		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), pathToDecompiledRoot.getPath(), pom.getAbsolutePath(), new CFRDecompiler(pathToDecompile));
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(model.getAllTypes().size(), 5);
		CtConstructor constructor = (CtConstructor) model.getRootPackage().getFactory().Type().get("se.kth.castor.UseJson").getTypeMembers().get(0);
		CtTry tryStmt = (CtTry) constructor.getBody().getStatement(1);
		CtLocalVariable var = (CtLocalVariable) tryStmt.getBody().getStatement(0);
		assertNotNull(var.getType().getTypeDeclaration());


		if(pathToDecompiledRoot.exists()) {
			pathToDecompiledRoot.delete();
		}
	}


	@Test
	public void testJarLauncherFernflower() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File pom = new File(baseDir, "pom.xml");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");

		File pathToDecompiledRoot = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp");
		if(pathToDecompiledRoot.exists()) {
			pathToDecompiledRoot.delete();
		}
		File pathToDecompile = new File(pathToDecompiledRoot,"src/main/java");
		pathToDecompile.mkdirs();

		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), pathToDecompiledRoot.getPath(), pom.getAbsolutePath(), new FernflowerDecompiler(pathToDecompile));
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(model.getAllTypes().size(), 5);
		CtConstructor constructor = (CtConstructor) model.getRootPackage().getFactory().Type().get("se.kth.castor.UseJson").getTypeMembers().get(0);
		CtTry tryStmt = (CtTry) constructor.getBody().getStatement(1);
		CtLocalVariable var = (CtLocalVariable) tryStmt.getBody().getStatement(0);
		assertNotNull(var.getType().getTypeDeclaration());


		if(pathToDecompiledRoot.exists()) {
			pathToDecompiledRoot.delete();
		}
	}

	@Test
	public void testJarLauncherNoPom() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");
		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), null);
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(model.getAllTypes().size(), 5);
	}
}
