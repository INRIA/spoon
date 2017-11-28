package spoon.test.reflect.meta;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.template.Parameter;
import spoon.test.metamodel.MMMethodKind;
import spoon.test.metamodel.SpoonMetaModel;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaModelTest {

	/*
	 * this test reports all spoon model elements which are not yet handled by meta model
	 * actually this is the result
	 */
	//enable this test after everything is covered.
	@Test
	public void spoonMetaModelTest() {
		SpoonMetaModel mm = new SpoonMetaModel(new File("./src/main/java"));
		List<String> problems = new ArrayList<>();
		
		//detect unused CtRoles
		Set<CtRole> unhandledRoles = new HashSet<>(Arrays.asList(CtRole.values()));

		mm.getMMTypes().forEach(mmType -> {
			mmType.getRole2field().forEach((role, mmField) -> {
				unhandledRoles.remove(role);
				if (mmField.getMethod(MMMethodKind.GET) == null) {
					problems.add("Missing getter for " + mmField.getOwnerType().getName() + " and CtRole." + mmField.getRole());
				}
				if (mmField.getMethod(MMMethodKind.SET) == null) {
                	if (mmType.getTypeContext().isSubtypeOf(mm.getFactory().Type().createReference(CtReference.class)) == false
                			&& mmType.getName().equals("CtTypeInformation") == false) {
                		//only NON references needs a setter
                		problems.add("Missing setter for " + mmField.getOwnerType().getName() + " and CtRole." + mmField.getRole());
                	}
				}
				//contract: type of field value is never implicit
				assertFalse("Value type of Field " + mmField.toString() + " is implicit", mmField.getValueType().isImplicit());
				assertFalse("Item value type of Field " + mmField.toString() + " is implicit", mmField.getItemValueType().isImplicit());
				
				mmField.forEachUnhandledMethod(ctMethod -> problems.add("Unhandled method signature: " + ctMethod.getDeclaringType().getSimpleName() + "#" + ctMethod.getSignature()));
			});
		});
		
		unhandledRoles.forEach(it -> problems.add("Unused CtRole." + it.name()));
		
//		assertTrue(String.join("\n", problems), problems.isEmpty());
	}
	@Test
	public void elementAnnotationRoleHandlerTest() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtClass<?> type = (CtClass) factory.Core().create(CtClass.class);
		CtAnnotation<?> annotation = factory.Annotation().annotate(type, Parameter.class, "value", "abc");
		
		//check contract of low level RoleHandler
		RoleHandler roleHandler = RoleHandlerHelper.getRoleHandler(type.getClass(), CtRole.ANNOTATION);
		assertNotNull(roleHandler);
		assertEquals(CtElement.class, roleHandler.getTargetType());
		assertSame(CtRole.ANNOTATION, roleHandler.getRole());
		assertSame(ContainerKind.LIST, roleHandler.getContainerKind());
		assertEquals(CtAnnotation.class, roleHandler.getValueClass());

		//check getting value using role handler
		List<CtAnnotation<?>> value = roleHandler.getValue(type);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		
		//check we have got direct readonly List
		try {
			value.remove(annotation);
			fail();
		} catch (Exception e) {
			this.getClass();
		}
		
		//check setValueByRole
		roleHandler.setValue(type, Collections.emptyList());
		value = roleHandler.getValue(type);
		assertEquals(0, value.size());

		roleHandler.setValue(type, Collections.singletonList(annotation));
		value = roleHandler.getValue(type);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		
		try {
			//contract value must be a list of annotation. One annotation is not actually OK. This contract might be changed in future
			roleHandler.setValue(type, annotation);
			fail();
		} catch (ClassCastException e) {
			//OK
		}
	}

	@Test
	public void elementAnnotationRoleTest() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtClass<?> type = (CtClass) factory.Core().create(CtClass.class);
		CtAnnotation<?> annotation = factory.Annotation().annotate(type, Parameter.class, "value", "abc");
		
		//check direct getValueByRole
		List<CtAnnotation<?>> value = type.getValueByRole(CtRole.ANNOTATION);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		
		try {
			value.remove(annotation);
			fail();
		} catch (Exception e) {
			this.getClass();
		}
		
		//check setValueByRole
		type.setValueByRole(CtRole.ANNOTATION, Collections.emptyList());
		value = type.getValueByRole(CtRole.ANNOTATION);
		assertEquals(0, value.size());

		type.setValueByRole(CtRole.ANNOTATION, Collections.singletonList(annotation));
		value = type.getValueByRole(CtRole.ANNOTATION);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		
		try {
			//contract value must be a list of annotation. One annotation is not actually OK. This contract might be changed in future
			type.setValueByRole(CtRole.ANNOTATION, annotation);
			fail();
		} catch (ClassCastException e) {
			//OK
		}
	}
	@Test
	public void elementAnnotationAdaptedRoleTest() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtClass<?> type = (CtClass) factory.Core().create(CtClass.class);
		CtAnnotation<?> annotation = factory.Annotation().annotate(type, Parameter.class, "value", "abc");
		
		//check adaptation of attribute to modifiable List
		List<CtAnnotation<?>> value = RoleHandlerHelper.getRoleHandler(type.getClass(), CtRole.ANNOTATION).asList(type);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		
		//check we can remove from this collection
		value.remove(annotation);
		assertEquals(0, value.size());
		assertEquals(0, ((List) type.getValueByRole(CtRole.ANNOTATION)).size());

		//check we can add to this collection
		value.add(annotation);
		assertEquals(1, value.size());
		assertSame(annotation, value.get(0));
		assertEquals(1, ((List) type.getValueByRole(CtRole.ANNOTATION)).size());
		assertEquals(annotation, ((List) type.getValueByRole(CtRole.ANNOTATION)).get(0));
	}
}
