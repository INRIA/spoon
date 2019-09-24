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
import org.junit.Ignore;
import org.junit.Test;
import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.Decompiler;
import spoon.decompiler.FernflowerDecompiler;
import spoon.decompiler.ProcyonDecompiler;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtConstructor;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JarLauncherTest {

	@Test
	public void testJarLauncherWithCFR() throws IOException {
		testJarLauncher(new CFRDecompiler());
	}

	@Test
	public void testJarLauncherWithProcyon() throws IOException {
		testJarLauncher(new ProcyonDecompiler());
	}

	@Ignore
	@Test
	public void testJarLauncherWithFernflower() throws IOException {
		testJarLauncher(new FernflowerDecompiler());
	}

	@Ignore
	@Test
	public void testTmpDirDeletion() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");
		File pathToDecompiledRoot = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp");

		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath());

		//contract: temporary directory is created
		assertTrue(pathToDecompiledRoot.exists());

		//Throws a SpoonException if the directory is not deletable.
		JarLauncher launcher2 = new JarLauncher(jar.getAbsolutePath());

		//contract: temporary directory is deleted and recreated.
		assertTrue(pathToDecompiledRoot.exists());

		if(pathToDecompiledRoot.exists()) {
			pathToDecompiledRoot.delete();
		}
	}


	public void testJarLauncher(Decompiler decompiler) throws IOException {
		File baseDir = new File("src/test/resources/jarLauncher");
		File pom = new File(baseDir, "pom.xml");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");

		File pathToDecompiledRoot = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "spoon-tmp");
		if(pathToDecompiledRoot.exists()) {
			FileUtils.deleteDirectory(pathToDecompiledRoot);
			pathToDecompiledRoot.delete();
		}
		File pathToDecompile = new File(pathToDecompiledRoot,"src/main/java");
		pathToDecompile.mkdirs();

		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), pathToDecompiledRoot.getPath(), pom.getAbsolutePath(),decompiler);
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();

		//contract: all types are decompiled (Sources are produced for each type)
		assertEquals(5, model.getAllTypes().size());


		CtConstructor constructor = (CtConstructor) model.getRootPackage().getFactory().Type().get("se.kth.castor.UseJson").getTypeMembers().get(0);
		CtTry tryStmt = (CtTry) constructor.getBody().getStatement(1);
		CtLocalVariable var = (CtLocalVariable) tryStmt.getBody().getStatement(0);

		//contract: UseJson is correctly decompiled (UseJSON.java contains a local variable declaration)
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
		assertEquals(5, model.getAllTypes().size());
	}

	@Ignore
	@Test
	public void testJarLauncherNoPomFernflower() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");
		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), null, new FernflowerDecompiler());
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(5, model.getAllTypes().size());
	}
}
