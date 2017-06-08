package spoon.test.api;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MetamodelTest {

	@Test
	public void testGetterSetterFroRole() {
		// contract: all roles in spoon metamodel must at least have a setter and a getter

		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();

		Factory factory = interfaces.getFactory();
		CtTypeReference propertyGetter = factory.Type().get(PropertyGetter.class).getReference();
		CtTypeReference propertySetter = factory.Type().get(PropertySetter.class).getReference();

		Set<String> expectedRoles = Arrays.stream(CtRole.values()).map(r -> r.name()).collect(Collectors.toSet());

		List<CtMethod> getters = interfaces.getModel().getElements(new AnnotationFilter<CtMethod>(PropertyGetter.class));
		Set<String> getterRoles = getters.stream().map(g -> ((CtFieldRead)g.getAnnotation(propertyGetter).getValue("role")).getVariable().getSimpleName()).collect(Collectors.toSet());

		List<CtMethod> setters = interfaces.getModel().getElements(new AnnotationFilter<CtMethod>(PropertySetter.class));
		Set<String> setterRoles = setters.stream().map(g -> ((CtFieldRead)g.getAnnotation(propertySetter).getValue("role")).getVariable().getSimpleName()).collect(Collectors.toSet());


		Assert.assertEquals(expectedRoles, getterRoles);
		Assert.assertEquals(expectedRoles, setterRoles);
	}
}
