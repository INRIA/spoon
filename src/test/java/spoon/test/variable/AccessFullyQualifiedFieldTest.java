package spoon.test.variable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.Tacos;

import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class AccessFullyQualifiedFieldTest {
	@Test
	public void testCheckAssignmentContracts() throws Exception {
		final Factory factory = build(Tacos.class);

		MainTest.checkAssignmentContracts(factory.Package().getRootPackage());
	}

	@Test
	public void testNoFQNWhenShadowedByField() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a field variable
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/BurritosFielded.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"-Field/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenShadowedByLocalVariable() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a local variable
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/Burritos.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"-Local/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInInnerClassAndShadowedByLocalVariable() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a local variable
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/BurritosStaticMethod.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"-StaticMethod/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInTryCatch() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a local variable
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/BurritosWithTryCatch.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"-TryCatch/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInLoop() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a local variable
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/BurritosWithLoop.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"-Loop/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

}
