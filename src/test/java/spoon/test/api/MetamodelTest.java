package spoon.test.api;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;

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
	/**
	 * contract: all non-final fields must be annotated with {@link spoon.reflect.annotations.MetamodelPropertyField}
	 */
	public void testRoleOnField() {
		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect");
		implementations.buildModel();

		Factory factory = implementations.getFactory();

		CtTypeReference metamodelPropertyField = factory.Type().get(MetamodelPropertyField.class).getReference();

		List<CtField> fieldWithoutAnnotation = (List<CtField>) implementations.getModel().getElements(new TypeFilter<CtField>(CtField.class) {
			@Override
			public boolean matches(CtField candidate) {
				if (candidate.hasModifier(ModifierKind.FINAL) || candidate.hasModifier(ModifierKind.STATIC) || candidate.hasModifier(ModifierKind.TRANSIENT)) {
					return false;
				}
				if ( 	// not a role
						"parent".equals(candidate.getSimpleName())
						|| "metadata".equals(candidate.getSimpleName())
						// cache field
						|| "valueOfMethod".equals(candidate.getSimpleName())) {
					return false;
				}
				CtClass parent = candidate.getParent(CtClass.class);
				return parent != null
						&& !(parent.isSubtypeOf(candidate.getFactory().createCtTypeReference(CtReference.class)))
						&& parent.isSubtypeOf(candidate.getFactory().createCtTypeReference(CtElement.class));
			}
		}).stream().filter(f -> f.getAnnotation(metamodelPropertyField) == null).collect(Collectors.toList());


		Assert.assertEquals(Collections.emptyList(), fieldWithoutAnnotation);


		final CtTypeReference propertySetter = factory.Type().get(PropertySetter.class).getReference();
		final CtTypeReference propertyGetter = factory.Type().get(PropertyGetter.class).getReference();

		List<CtField> fields = factory.getModel().getElements(new AnnotationFilter<CtField>(MetamodelPropertyField.class));
		for (CtField field : fields) {
			CtClass parent = field.getParent(CtClass.class);
			String role = ((CtFieldRead) field.getAnnotation(metamodelPropertyField).getValue("role")).getVariable().getSimpleName();

			CtQuery superQuery = parent.map(new SuperInheritanceHierarchyFunction());

			List<CtType> superType = superQuery.list();

			List<CtMethod> methods = superQuery.map((CtType type) -> type.getMethodsAnnotatedWith(propertyGetter, propertySetter)).list();

			boolean setterFound = false;
			boolean getterFound = false;
			for (CtMethod method : methods) {
				CtAnnotation getterAnnotation = method.getAnnotation(propertyGetter);
				CtAnnotation setterAnnotation = method.getAnnotation(propertySetter);
				if (getterAnnotation != null) {
					getterFound |= ((CtFieldRead) getterAnnotation.getValue("role")).getVariable().getSimpleName().equals(role);
				}
				if (setterAnnotation != null) {
					setterFound |= ((CtFieldRead) setterAnnotation.getValue("role")).getVariable().getSimpleName().equals(role);
				}
			}

			Assert.assertTrue(role + " must have a getter in " + parent.getQualifiedName(), getterFound);
			Assert.assertTrue(role + " must have a setter in " + parent.getQualifiedName(), setterFound);
		}

	}
}
