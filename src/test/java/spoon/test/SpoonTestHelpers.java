package spoon.test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpoonTestHelpers {
	// only static methods
	private SpoonTestHelpers(){
	}

	public static List<CtType<? extends CtElement>> getAllInstantiableMetamodelInterfaces() {
		List<CtType<? extends CtElement>> result = new ArrayList<>();
		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();

		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect/declaration");
		implementations.addInputResource("src/main/java/spoon/support/reflect/code");
		implementations.addInputResource("src/main/java/spoon/support/reflect/reference");
		implementations.buildModel();

		for(CtType<? > itf : interfaces.getModel().getAllTypes()) {
			String impl = itf.getQualifiedName().replace("spoon.reflect", "spoon.support.reflect")+"Impl";
			CtType implClass = implementations.getFactory().Type().get(impl);
			if (implClass != null && !implClass.hasModifier(ModifierKind.ABSTRACT)) {
				result.add((CtType<? extends CtElement>) itf);
			}
		}
		return result;
	}
}
