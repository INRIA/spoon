package spoon.test.sealedclasses;

import org.junit.jupiter.api.Test;
import spoon.Launcher;

public class SealedClassesTest {

	@Test
	void testSealedClassWithInnerSubclassModel() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);

		launcher.addInputResource("src/test/resources/sealedclasses/");
		launcher.buildModel();
	}
}
