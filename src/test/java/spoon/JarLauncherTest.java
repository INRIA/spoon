package spoon;

import org.junit.Ignore;
import org.junit.Test;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtConstructor;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class JarLauncherTest {

	@Ignore
	@Test
	public void testJarLauncher() {

		File baseDir = new File("src/test/resources/jarLauncher");
		File pom = new File(baseDir, "pom.xml");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");
		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), null, pom.getAbsolutePath());
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(model.getAllTypes().size(), 5);
		CtConstructor constructor = (CtConstructor) model.getRootPackage().getFactory().Type().get("se.kth.castor.UseJson").getTypeMembers().get(0);
		CtTry tryStmt = (CtTry) constructor.getBody().getStatement(1);
		CtLocalVariable var = (CtLocalVariable) tryStmt.getBody().getStatement(0);
		assertNotNull(var.getType().getTypeDeclaration());
	}

	@Ignore
	@Test
	public void testJarLauncherNoPom() {
		File baseDir = new File("src/test/resources/jarLauncher");
		File jar = new File(baseDir, "helloworld-1.0-SNAPSHOT.jar");
		JarLauncher launcher = new JarLauncher(jar.getAbsolutePath(), null);
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		CtModel model = launcher.getModel();
		assertEquals(model.getAllTypes().size(),5);
	}

}