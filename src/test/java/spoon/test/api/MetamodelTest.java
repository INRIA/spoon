package spoon.test.api;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.PropertyGetter;
import spoon.support.PropertySetter;
import spoon.support.UnsettableProperty;

import java.util.Arrays;
import java.util.Collections;
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

	@Test
	public void testAllGetterAndSetterHaveAnAnnotation() {
		// contract: all getter and setter must have an annotation

		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();

		Factory factory = interfaces.getFactory();
		CtTypeReference propertyGetter = factory.Type().get(PropertyGetter.class).getReference();
		CtTypeReference propertySetter = factory.Type().get(PropertySetter.class).getReference();

		CtTypeReference derivedProperty = factory.Type().get(DerivedProperty.class).getReference();
		CtTypeReference unsettableProperty = factory.Type().get(UnsettableProperty.class).getReference();

		List<String> getterSetterWithoutAnnotation = (List<String>) interfaces.getModel()
				.getElements(new TypeFilter<CtMethod>(CtMethod.class)).stream()
				.filter(m -> {
					String name = m.getSimpleName();
					return m.getParent() instanceof CtInterface &&
							(name.startsWith("get")
							|| name.startsWith("set")
							|| name.startsWith("add")
							|| name.startsWith("remove")) &&
							( m.getAnnotation(propertyGetter) == null
									&& m.getAnnotation(propertySetter) == null
									&& m.getAnnotation(derivedProperty) == null
									&& m.getAnnotation(unsettableProperty) == null);
				}).map(m -> ((CtMethod) m).getParent(CtInterface.class).getQualifiedName() + "#" + ((CtMethod) m).getSimpleName())
				.collect(Collectors.toList());
		Assert.assertEquals(Collections.EMPTY_LIST, getterSetterWithoutAnnotation);
	}

}
