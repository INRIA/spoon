package spoon.test.sealedclasses;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class SealedClassesTest {

	@Test
	void testSealedClassWithInnerSubclassModel() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);

		launcher.addInputResource("src/test/resources/sealedclasses/SealedClassWithNestedSubclasses.java");
		CtModel ctModel = launcher.buildModel();
		ctModel.getAllTypes().forEach(System.out::println);
		int a = 3;
	}

	@Test
	void testEnumSealed() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);

		launcher.addInputResource("src/test/resources/sealedclasses/EnumWithAnonymousValue.java");
		CtModel ctModel = launcher.buildModel();
		ctModel.getAllTypes().forEach(System.out::println);
		int a = 3;
	}

	@Test
	void testMultiCompilationUnitSealed() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(16);
		launcher.getEnvironment().setPreviewFeaturesEnabled(true);

		launcher.addInputResource("src/test/resources/sealedclasses/SealedClassWithPermits.java");
		launcher.addInputResource("src/test/resources/sealedclasses/ExtendingClass.java");
		launcher.addInputResource("src/test/resources/sealedclasses/OtherExtendingClass.java");
		CtModel ctModel = launcher.buildModel();
		ctModel.getAllTypes().forEach(System.out::println);
		int a = 3;
	}
}
