package spoon.test.variable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.Burritos;
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
	public void testNoFQN() throws Exception {
		// contract: no fully qualified name if top package is shadowed by a local variable
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/spoon/test/variable/testclasses/Burritos.java");
		String output = "target/spooned-" + this.getClass().getSimpleName()+"/";
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		canBeBuilt(output, 7);
	}

}