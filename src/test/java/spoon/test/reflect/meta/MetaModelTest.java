package spoon.test.reflect.meta;

import org.junit.Test;

import spoon.Launcher;
import spoon.Metamodel;
import spoon.SpoonException;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.template.Parameter;
import spoon.test.metamodel.MMMethodKind;
import spoon.test.metamodel.SpoonMetaModel;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
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
	@Test
	public void spoonMetaModelTest() {
		SpoonMetaModel mm = new SpoonMetaModel(new File("./src/main/java"));
		List<String> problems = new ArrayList<>();
		
		//detect unused CtRoles
		Set<CtRole> unhandledRoles = new HashSet<>(Arrays.asList(CtRole.values()));

		mm.getConcepts().forEach(mmConcept -> {
			mmConcept.getRoleToProperty().forEach((role, mmField) -> {
				if (mmField.isUnsettable()) {
					//contract: all unsettable fields are derived too
					assertTrue("Unsettable field " + mmField + " must be derived too", mmField.isDerived());
				}
				unhandledRoles.remove(role);
				if (mmField.getMethod(MMMethodKind.GET) == null) {
					problems.add("Missing getter for " + mmField.getOwnerConcept().getName() + " and CtRole." + mmField.getRole());
				}
				if (mmField.getMethod(MMMethodKind.SET) == null) {
                	if (mmConcept.getTypeContext().isSubtypeOf(mm.getFactory().Type().createReference(CtReference.class)) == false
                			&& mmConcept.getName().equals("CtTypeInformation") == false) {
                		//only NON references needs a setter
                		problems.add("Missing setter for " + mmField.getOwnerConcept().getName() + " and CtRole." + mmField.getRole());
                	}
				}
				//contract: type of field value is never implicit
				assertFalse("Value type of Field " + mmField.toString() + " is implicit", mmField.getValueType().isImplicit());
				assertFalse("Item value type of Field " + mmField.toString() + " is implicit", mmField.getItemValueType().isImplicit());
				
				mmField.forEachUnhandledMethod(ctMethod -> problems.add("Unhandled method signature: " + ctMethod.getDeclaringType().getSimpleName() + "#" + ctMethod.getSignature()));
			});
		});
		
		unhandledRoles.forEach(it -> problems.add("Unused CtRole." + it.name()));
		/*
		 * This assertion prints all the methods which are not covered by current implementation of SpoonMetaModel.
		 * It is not a bug. It is useful to see how much is SpoonMetaModel covering real Spoon model.
		 */
//		assertTrue(String.join("\n", problems), problems.isEmpty());
	}
	@Test
	public void testGetRoleHandlersOfClass() {
		int countOfIfaces = 0;
		for (CtType spoonIface : Metamodel.getAllMetamodelInterfaces()) {
			countOfIfaces++;
			checkRoleHandlersOfType(spoonIface);
		}
		assertTrue(countOfIfaces > 10);
	}
	
	private void checkRoleHandlersOfType(CtType iface) {
		Class ifaceClass =  iface.getActualClass();
		//contract: check that for each Spoon model interface we have correct list of Role handlers
		List<RoleHandler> roleHandlersOfIFace = new ArrayList<>(RoleHandlerHelper.getRoleHandlers(ifaceClass));
		Set<RoleHandler> allRoleHandlers = new HashSet<>();
		RoleHandlerHelper.forEachRoleHandler(rh -> allRoleHandlers.add(rh));
		for (CtRole role : CtRole.values()) {
			RoleHandler rh = RoleHandlerHelper.getOptionalRoleHandler(ifaceClass, role);
			if (rh != null) {
				assertTrue("RoleHandler for role " + role + " is missing for " + ifaceClass, roleHandlersOfIFace.remove(rh));
				assertTrue("RoleHandler " + rh + " is not accessible by RoleHandlerHelper#forEachRoleHandler()", allRoleHandlers.contains(rh));
			}
		}
		assertTrue("There are unexpected RoleHandlers " + roleHandlersOfIFace + " for " + ifaceClass, roleHandlersOfIFace.isEmpty());
	}
	
	@Test
	public void testGetParentRoleHandler() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtClass<?> type = (CtClass) factory.Core().create(CtClass.class);
		CtField<?> field = factory.Field().create(type, Collections.emptySet(), factory.Type().booleanPrimitiveType(), "someField");
		assertSame(type, field.getDeclaringType());
		//contract: RoleHandlerHelper#getParentRoleHandler returns role handler which handles it's relationship to parent
		assertSame(CtRole.TYPE_MEMBER, RoleHandlerHelper.getRoleHandlerWrtParent(field).getRole());
		assertSame(CtRole.TYPE_MEMBER, field.getRoleInParent());
		//contract: RoleHandlerHelper#getParentRoleHandler returns null if there is no parent
		field.setParent(null);
		assertNull(RoleHandlerHelper.getRoleHandlerWrtParent(field));
		//contract: RoleHandlerHelper#getParentRoleHandler returns null if parent relation cannot be handled in this case
		//parent of new CtClass is root package - there is no way how to modify that
		assertNull(RoleHandlerHelper.getRoleHandlerWrtParent(type));
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
	@Test
	public void singleValueRoleAddSetRemove() {
		//contract: single value roles supports multivalue interface too 
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtTypeReference<?> typeRef = factory.Type().createReference("some.test.package.TestType");
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(typeRef.getClass(), CtRole.PACKAGE_REF);

		//contract: single value role provides a List
		List<CtPackageReference> packages = rh.asList(typeRef);
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract: adding of existing value fails and changes nothing
		try {
			packages.add(typeRef.getPackage());
			fail();
		} catch (Exception e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract: adding of null fails and changes nothing
		try {
			assertFalse(packages.add(null));
			fail();
		} catch (Exception e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract: adding of different value fails, and changes nothing
		try {
			packages.add(factory.Package().createReference("some.test.another_package"));
			fail();
		} catch (SpoonException e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract remove of different value changes nothing
		assertFalse(packages.remove(factory.Package().createReference("some.test.another_package")));
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract remove of null value changes nothing
		assertFalse(packages.remove(null));
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract remove of existing value sets value to null and size to 0
		assertTrue(packages.remove(factory.Package().createReference("some.test.package")));
		assertListContracts(packages, typeRef, 0, null);
		
		//contract add of null into empty collection changes size to 1, but value is still null
		assertTrue(packages.add(null));
		assertListContracts(packages, typeRef, 1, null);
		
		//contract: adding of new value into collection with single null value fails and changes nothing
		try {
			packages.add(factory.Package().createReference("some.test.another_package"));
			fail();
		} catch (SpoonException e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, null);
		
		//contract: set of new value replaces existing value
		assertEquals(null, packages.set(0, factory.Package().createReference("some.test.package")));
		assertListContracts(packages, typeRef, 1, "some.test.package");
		
		//contract: set of null value keeps size==1 even if value is replaced by null
		assertEquals("some.test.package", packages.set(0, null).getQualifiedName());
		assertListContracts(packages, typeRef, 1, null);
		
		//contract: remove of null value by index sets size==0 the value is still null
		assertNull(packages.remove(0));
		assertListContracts(packages, typeRef, 0, null);
		
		//contract: add of null value sets size==1 the value is still null
		assertTrue(packages.add(null));
		assertListContracts(packages, typeRef, 1, null);
		
		//contract: remove of null value by value sets size==0 the value is still null
		assertTrue(packages.remove(null));
		assertListContracts(packages, typeRef, 0, null);
		
		//contract: set of new value on empty collection fails with IndexOutOfBounds and changes nothing
		try {
			packages.set(0, factory.Package().createReference("some.test.another_package"));
			fail();
		} catch(IndexOutOfBoundsException e) {
			//OK
		}
		assertListContracts(packages, typeRef, 0, null);
		
		//contract: adding of value into empty collection adds value
		assertTrue(packages.add(factory.Package().createReference("some.test.another_package")));
		assertListContracts(packages, typeRef, 1, "some.test.another_package");
		
		//contract: remove of value by index from collection removes that value
		assertEquals("some.test.another_package", packages.remove(0).getQualifiedName());
		assertListContracts(packages, typeRef, 0, null);
	}
	
	private void assertListContracts(List<CtPackageReference> packages, CtTypeReference<?> typeRef, int expectedSize, String expectedValue) {
		if (expectedSize == 0) {
			assertEquals(0, packages.size());
			assertNull(typeRef.getPackage());
			//contract: get(x) fails for each x when called on empty collection
			for (int i = -1; i < 3; i++) {
				try {
					packages.get(i);
					fail();
				} catch(IndexOutOfBoundsException e) {
					//OK
				}
			}
		} else if (expectedSize == 1) {
			assertEquals(1, packages.size());
			assertPackageName(expectedValue, typeRef.getPackage());
			//contract: get(x) fails for each x when called on collection with one item, excluding for index == 0
			for (int i = -1; i < 3; i++) {
				if (i == 0) {
					assertPackageName(expectedValue, packages.get(0));
				} else {
					try {
						packages.get(i);
						fail();
					} catch(IndexOutOfBoundsException e) {
						//OK
					}
				}
			}
		} else {
			fail();
		}
	}
	
	private void assertPackageName(String expectedPackageName, CtPackageReference packageRef) {
		if (expectedPackageName == null) {
			assertNull(packageRef);
		} else {
			assertEquals(expectedPackageName, packageRef.getQualifiedName());
		}
	}
	
	@Test
	public void listValueRoleSetOn() {
		//contract: multi-value role supports set(int, Object)
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		CtClass<?> ctClass = factory.Class().create("some.test.TestClass");
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(ctClass.getClass(), CtRole.TYPE_MEMBER);
		List<CtTypeMember> typeMembers = rh.asList(ctClass);
		assertEquals(0, typeMembers.size());
		CtField<?> field1 = createField(factory, "field1");
		CtField<?> field2 = createField(factory, "field2");
		CtField<?> field3 = createField(factory, "field3");
		//check that field was not added in type yet
		assertEquals(0, typeMembers.size());
		//contract: call of add on RoleHandler collection adds the item into real collection too 
		typeMembers.add(field1);
		assertEquals(1, typeMembers.size());
		assertEquals(1, ctClass.getTypeMembers().size());
		assertSame(ctClass, field1.getDeclaringType());
		assertThat(asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		//contract: call of add on RoleHandler collection adds the item into real collection too 
		typeMembers.add(field2);
		assertSame(ctClass, field2.getDeclaringType());
		assertThat(asList("field1","field2"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		//contract: call of set on RoleHandler collection replaces the item in real collection
		typeMembers.set(0, field3);
		assertSame(ctClass, field3.getDeclaringType());
		assertThat(asList("field3","field2"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		typeMembers.set(1, field1);
		assertThat(asList("field3","field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		//contract: call of remove(int) on RoleHandler collection removes the item in real collection
		assertSame(field3, typeMembers.remove(0));
		assertThat(asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		//contract: call of remove(Object) which does not exist does nothing
		assertFalse(typeMembers.remove(field2));
		assertThat(asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
		//contract: call of remove(Object) on RoleHandler collection removes the item in real collection
		assertTrue(typeMembers.remove(field1));
		assertThat(asList(), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e)->e.getSimpleName()).list())) ;
	}
	
	private CtField<?> createField(Factory factory, String name) {
		CtField<?> field = factory.Core().createField();
		field.setType((CtTypeReference) factory.Type().booleanType());
		field.setSimpleName(name);
		return field;
	}
}
