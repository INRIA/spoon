/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.api;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.metamodel.ConceptKind;
import spoon.metamodel.MMMethod;
import spoon.metamodel.MMMethodKind;
import spoon.metamodel.Metamodel;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.MetamodelProperty;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.SuperInheritanceHierarchyFunction;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.visitor.ClassTypingContext;
import spoon.template.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.architecture.SpoonArchitectureEnforcerTest.assertSetEquals;

public class MetamodelTest {
	@Test
	public void testGetAllMetamodelInterfacess() {
		// contract: Spoon supports runtime introspection on the metamodel
		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();
		assertThat(Metamodel.getAllMetamodelInterfaces().stream().map(x -> x.getQualifiedName()).collect(Collectors.toSet()), equalTo(interfaces.getModel().getAllTypes().stream().map(x -> x.getQualifiedName()).collect(Collectors.toSet())));
	}

	@Test
	public void testRuntimeMetamodel() {
		// contract: Spoon supports runtime introspection on the metamodel - all (non abstract) Spoon classes and their fields are accessible by Metamodel
		Metamodel testMetaModel = Metamodel.getInstance();
		Map<String, MetamodelConcept> expectedTypesByName = new HashMap<>();
		testMetaModel.getConcepts().forEach(t -> {
			if (t.getKind() == ConceptKind.LEAF) {
				expectedTypesByName.put(t.getName(), t);
			}
		});
		List<String> problems = new ArrayList<>();
		for (spoon.test.api.Metamodel.Type type : spoon.test.api.Metamodel.getAllMetamodelTypes()) {
			MetamodelConcept expectedType = expectedTypesByName.remove(type.getName());
			assertSame(expectedType.getImplementationClass().getActualClass(), type.getModelClass());
			assertSame(expectedType.getMetamodelInterface().getActualClass(), type.getModelInterface());
			Map<CtRole, MetamodelProperty> expectedRoleToField = new HashMap<>(expectedType.getRoleToProperty());
			for (spoon.test.api.Metamodel.Field field : type.getFields()) {
				MetamodelProperty expectedField = expectedRoleToField.remove(field.getRole());
				if (expectedField == null) {
					problems.add("no method with role " + field.getRole() + " in interface " + type.getName());
					continue;
				}
				if (expectedField.isDerived() != field.isDerived()) {
					problems.add("Field " + expectedField + ".derived hardcoded value = " + field.isDerived() + " but computed value is " + expectedField.isDerived());
				}
				if (expectedField.isUnsettable() != field.isUnsettable()) {
					problems.add("Field " + expectedField + ".unsettable hardcoded value = " + field.isUnsettable() + " but computed value is " + expectedField.isUnsettable());
				}
			}
		}
		if (expectedTypesByName.isEmpty() == false) {
			problems.add("These Metamodel.Type instances are missing:" + expectedTypesByName.keySet());
		}
		assertTrue("You might need to update api/Metamodel.java: " + String.join("\n", problems), problems.isEmpty());
	}
	
	@Test
	public void testGetterSetterForRole() {
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

		List<CtMethod<?>> getters = interfaces.getModel().getElements(new AnnotationFilter<>(PropertyGetter.class));
		Set<String> getterRoles = getters.stream().map(g -> ((CtFieldRead) g.getAnnotation(propertyGetter).getValue("role")).getVariable().getSimpleName()).collect(Collectors.toSet());
		Set<CtMethod<?>> isNotGetter = getters.stream().filter(m -> !(m.getSimpleName().startsWith("get") || m.getSimpleName().startsWith("is"))).collect(Collectors.toSet());

		List<CtMethod<?>> setters = interfaces.getModel().getElements(new AnnotationFilter<>(PropertySetter.class));
		Set<String> setterRoles = setters.stream().map(g -> ((CtFieldRead) g.getAnnotation(propertySetter).getValue("role")).getVariable().getSimpleName()).collect(Collectors.toSet());
		Set<CtMethod<?>> isNotSetter = setters.stream().filter(m -> !(m.getSimpleName().startsWith("set") || m.getSimpleName().startsWith("add") || m.getSimpleName().startsWith("insert") || m.getSimpleName().startsWith("remove"))).collect(Collectors.toSet());

		assertEquals(expectedRoles, getterRoles);
		//derived roles with no setter:
		expectedRoles.remove(CtRole.DECLARED_MODULE.name());
		expectedRoles.remove(CtRole.DECLARED_TYPE.name());
		expectedRoles.remove(CtRole.EMODIFIER.name());

		assertSetEquals("", expectedRoles, setterRoles);

		assertEquals(Collections.EMPTY_SET, isNotGetter);
		assertEquals(Collections.EMPTY_SET, isNotSetter);
	}
	
	private static final Set<String> IGNORED_FIELD_NAMES = new HashSet<>(Arrays.asList(
			"parent",
			"metadata",
			"factory",
			"valueOfMethod",
			"autoImport",
			"file",
			"lineSeparatorPositions",
			"rootFragment",
			"originalSourceCode",
			"myPartialSourcePosition"));

	@Test
	public void testRoleOnField() {
		//  contract: all non-final / transient / static fields must be annotated with {@link spoon.reflect.annotations.MetamodelPropertyField}
		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect");
		implementations.buildModel();

		String nl = System.getProperty("line.separator");
		Factory factory = implementations.getFactory();

		CtTypeReference metamodelPropertyField = factory.Type().get(MetamodelPropertyField.class).getReference();

		final List<String> result = new ArrayList();
		List<CtField> fieldWithoutAnnotation = (List<CtField>) implementations.getModel().getElements(new TypeFilter<CtField>(CtField.class) {
			@Override
			public boolean matches(CtField candidate) {
				if (candidate.hasModifier(ModifierKind.FINAL) || candidate.hasModifier(ModifierKind.STATIC) || candidate.hasModifier(ModifierKind.TRANSIENT)) {
					return false;
				}
				if (IGNORED_FIELD_NAMES.contains(candidate.getSimpleName())) {
					// not a role
					return false;
				}
				CtClass parent = candidate.getParent(CtClass.class);
				return parent != null
						// code and reference element fields must be annotated
						&&
						(parent.isSubtypeOf(candidate.getFactory().createCtTypeReference(CtReference.class))
						|| parent.isSubtypeOf(candidate.getFactory().createCtTypeReference(CtElement.class))
						);
			}
		}).stream().map(x -> {
			result.add(x.toString()); return x;
		}).filter(f -> f.getAnnotation(metamodelPropertyField) == null).collect(Collectors.toList());

		assertTrue(result.contains("@spoon.reflect.annotations.MetamodelPropertyField(role = spoon.reflect.path.CtRole.IS_SHADOW)" + nl + "boolean isShadow;"));
		assertTrue(result.contains("@spoon.reflect.annotations.MetamodelPropertyField(role = spoon.reflect.path.CtRole.TYPE)" + nl + "spoon.reflect.reference.CtTypeReference<T> type;"));
		assertTrue(result.size() > 100);
		assertEquals(Collections.emptyList(), fieldWithoutAnnotation);

		final CtTypeReference propertySetter = factory.Type().get(PropertySetter.class).getReference();
		final CtTypeReference propertyGetter = factory.Type().get(PropertyGetter.class).getReference();

		List<CtField> fields = factory.getModel().getElements(new AnnotationFilter<>(MetamodelPropertyField.class));
		for (CtField field : fields) {
			CtClass parent = field.getParent(CtClass.class);
			CtExpression roleExpression = field.getAnnotation(metamodelPropertyField).getValue("role");
			List<String> roles = new ArrayList<>();
			if (roleExpression instanceof CtFieldRead) {
				roles.add(((CtFieldRead) roleExpression).getVariable().getSimpleName());
			} else  if (roleExpression instanceof CtNewArray) {
				List<CtFieldRead> elements = ((CtNewArray) roleExpression).getElements();
				for (CtFieldRead ctFieldRead : elements) {
					roles.add(ctFieldRead.getVariable().getSimpleName());
				}
			}

			CtQuery superQuery = parent.map(new SuperInheritanceHierarchyFunction());

			List<CtMethod> methods = superQuery.map((CtType type) -> type.getMethodsAnnotatedWith(propertyGetter, propertySetter)).list();

			boolean setterFound = false;
			boolean getterFound = false;
			for (CtMethod method : methods) {
				CtAnnotation getterAnnotation = method.getAnnotation(propertyGetter);
				CtAnnotation setterAnnotation = method.getAnnotation(propertySetter);
				if (getterAnnotation != null) {
					getterFound |= roles.contains(((CtFieldRead) getterAnnotation.getValue("role")).getVariable().getSimpleName());
				}
				if (setterAnnotation != null) {
					setterFound |= roles.contains(((CtFieldRead) setterAnnotation.getValue("role")).getVariable().getSimpleName());
				}
			}

			assertTrue(roles + " must have a getter in " + parent.getQualifiedName(), getterFound);
			assertTrue(roles + " must have a setter in " + parent.getQualifiedName(), setterFound);
		}
	}

	@Test
	public void testMetamodelWithoutSources() {
		//contract: metamodel based on spoon sources delivers is same like metamodel based on shadow classes
		Metamodel runtimeMM = Metamodel.getInstance();
		Collection<MetamodelConcept> concepts = runtimeMM.getConcepts();

		Metamodel sourceBasedMM = new Metamodel(new File("src/main/java"));
		Map<String, MetamodelConcept> expectedConceptsByName = new HashMap<>();
		sourceBasedMM.getConcepts().forEach(c -> {
			expectedConceptsByName.put(c.getName(), c);
		});
		for (MetamodelConcept runtimeConcept : concepts) {
			MetamodelConcept expectedConcept = expectedConceptsByName.remove(runtimeConcept.getName());
			assertNotNull(expectedConcept);
			assertConceptsEqual(expectedConcept, runtimeConcept);
		}
		assertEquals(expectedConceptsByName.keySet().toString(), 0, expectedConceptsByName.size());
	}

	private void assertConceptsEqual(MetamodelConcept expectedConcept, MetamodelConcept runtimeConcept) {
		assertEquals(expectedConcept.getName(), runtimeConcept.getName());
		if (expectedConcept.getImplementationClass() == null) {
			assertNull(runtimeConcept.getImplementationClass());
		} else {
			assertNotNull(runtimeConcept.getImplementationClass());
			assertSame(expectedConcept.getImplementationClass().getActualClass(), runtimeConcept.getImplementationClass().getActualClass());
		}
		assertSame(expectedConcept.getMetamodelInterface().getActualClass(), runtimeConcept.getMetamodelInterface().getActualClass());
		assertEquals(expectedConcept.getKind(), runtimeConcept.getKind());

		// must be sorted as the order of super concepts from source is affected by the order of elements in the
		// implements clause
		List<MetamodelConcept> expectedSuperConcepts = expectedConcept.getSuperConcepts();
		List<MetamodelConcept> runtimeSuperConcepts = runtimeConcept.getSuperConcepts();
		expectedSuperConcepts.sort(Comparator.comparing(MetamodelConcept::getName));
		runtimeSuperConcepts.sort(Comparator.comparing(MetamodelConcept::getName));

		assertEquals(expectedSuperConcepts.size(), runtimeSuperConcepts.size());
		for (int i = 0; i < expectedSuperConcepts.size(); i++) {
			assertConceptsEqual(expectedSuperConcepts.get(i), runtimeSuperConcepts.get(i));
		}

		Map<CtRole, MetamodelProperty> expectedRoleToProperty = new HashMap(expectedConcept.getRoleToProperty());
		for (Map.Entry<CtRole, MetamodelProperty> e : runtimeConcept.getRoleToProperty().entrySet()) {
			MetamodelProperty runtimeProperty = e.getValue();
			MetamodelProperty expectedProperty = expectedRoleToProperty.remove(e.getKey());
			assertPropertiesEqual(expectedProperty, runtimeProperty);
		}
		assertEquals(0, expectedRoleToProperty.size());
	}

	private void assertPropertiesEqual(MetamodelProperty expectedProperty, MetamodelProperty runtimeProperty) {
		assertSame(expectedProperty.getRole(), runtimeProperty.getRole());
		assertEquals(expectedProperty.getName(), runtimeProperty.getName());
		assertSame(expectedProperty.getTypeofItems().getActualClass(), runtimeProperty.getTypeofItems().getActualClass());
		assertEquals(expectedProperty.getOwner().getName(), runtimeProperty.getOwner().getName());
		assertSame(expectedProperty.getContainerKind(), runtimeProperty.getContainerKind());
		assertEquals(expectedProperty.getTypeOfField(), runtimeProperty.getTypeOfField());
		assertEquals(expectedProperty.isDerived(), runtimeProperty.isDerived());
		assertEquals(expectedProperty.isUnsettable(), runtimeProperty.isUnsettable());
	}

	@Test
	public void testMetamodelCachedInFactory() {
		//contract: Metamodel concepts are accessible
		Metamodel.getInstance().getConcepts();
	}

	/*
	 * this test reports all spoon model elements which are not yet handled by meta model
	 * actually this is the result
	 */
	@Test
	public void spoonMetaModelTest() {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		Metamodel mm = Metamodel.getInstance();
		List<String> problems = new ArrayList<>();

		//detect unused CtRoles
		Set<CtRole> unhandledRoles = new HashSet<>(Arrays.asList(CtRole.values()));

		mm.getConcepts().forEach(mmConcept -> {
			mmConcept.getRoleToProperty().forEach((role, mmField) -> {
				unhandledRoles.remove(role);
				if (mmField.getMethod(MMMethodKind.GET) == null) {
					problems.add("Missing getter for " + mmField.getOwner().getName() + " and CtRole." + mmField.getRole());
				}
				if (mmField.getMethod(MMMethodKind.SET) == null) {
					if (new ClassTypingContext(mmConcept.getMetamodelInterface()).isSubtypeOf(factory.Type().createReference(CtReference.class)) == false
							&& "CtTypeInformation".equals(mmConcept.getName()) == false) {
						//only NON references needs a setter
						problems.add("Missing setter for " + mmField.getOwner().getName() + " and CtRole." + mmField.getRole());
					}
				}
				//contract: type of field value is never implicit
				assertFalse("Value type of Field " + mmField.toString() + " is implicit", mmField.getTypeOfField().isImplicit());
				assertFalse("Item value type of Field " + mmField.toString() + " is implicit", mmField.getTypeofItems().isImplicit());

				mmField.getMethods(MMMethodKind.OTHER).forEach(
						mmethod -> mmethod.getDeclaredMethods().forEach(
								ctMethod -> problems.add("Unhandled method signature: " + ctMethod.getDeclaringType().getSimpleName() + "#" + ctMethod.getSignature())
						)
				);

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
		assertSame(CtElement.class, roleHandler.getTargetType());
		assertSame(CtRole.ANNOTATION, roleHandler.getRole());
		assertSame(ContainerKind.LIST, roleHandler.getContainerKind());
		assertSame(CtAnnotation.class, roleHandler.getValueClass());

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
		CtTypeReference<?> typeRef = factory.Type().createReference("some.test.foo.TestType");
		RoleHandler rh = RoleHandlerHelper.getRoleHandler(typeRef.getClass(), CtRole.PACKAGE_REF);

		//contract: single value role provides a List
		List<CtPackageReference> packages = rh.asList(typeRef);
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract: adding of existing value fails and changes nothing
		try {
			packages.add(typeRef.getPackage());
			fail();
		} catch (Exception e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract: adding of null fails and changes nothing
		try {
			assertFalse(packages.add(null));
			fail();
		} catch (Exception e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract: adding of different value fails, and changes nothing
		try {
			packages.add(factory.Package().createReference("some.test.another_package"));
			fail();
		} catch (SpoonException e) {
			//OK
		}
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract remove of different value changes nothing
		assertFalse(packages.remove(factory.Package().createReference("some.test.another_package")));
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract remove of null value changes nothing
		assertFalse(packages.remove(null));
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract remove of existing value sets value to null and size to 0
		assertTrue(packages.remove(factory.Package().createReference("some.test.foo")));
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
		assertNull(packages.set(0, factory.Package().createReference("some.test.foo")));
		assertListContracts(packages, typeRef, 1, "some.test.foo");

		//contract: set of null value keeps size==1 even if value is replaced by null
		assertEquals("some.test.foo", packages.set(0, null).getQualifiedName());
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
		} catch (IndexOutOfBoundsException e) {
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
				} catch (IndexOutOfBoundsException e) {
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
					} catch (IndexOutOfBoundsException e) {
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
		assertThat(Arrays.asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		//contract: call of add on RoleHandler collection adds the item into real collection too
		typeMembers.add(field2);
		assertSame(ctClass, field2.getDeclaringType());
		assertThat(Arrays.asList("field1", "field2"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		//contract: call of set on RoleHandler collection replaces the item in real collection
		typeMembers.set(0, field3);
		assertSame(ctClass, field3.getDeclaringType());
		assertThat(Arrays.asList("field3", "field2"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		typeMembers.set(1, field1);
		assertThat(Arrays.asList("field3", "field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		//contract: call of remove(int) on RoleHandler collection removes the item in real collection
		assertSame(field3, typeMembers.remove(0));
		assertThat(Arrays.asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		//contract: call of remove(Object) which does not exist does nothing
		assertFalse(typeMembers.remove(field2));
		assertThat(Arrays.asList("field1"), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
		//contract: call of remove(Object) on RoleHandler collection removes the item in real collection
		assertTrue(typeMembers.remove(field1));
		assertThat(Arrays.asList(), is(ctClass.filterChildren(new TypeFilter(CtField.class)).map((CtField e) -> e.getSimpleName()).list()));
	}

	@Test
	public void testMethodBySignature() {
		//contract: MetamodelProperty#getMethodBySignature(String) returns null for unknown signature
		MMMethod method = Metamodel.getInstance().getConcept(CtClass.class).getProperty(CtRole.NAME).getMethodBySignature("getSimpleName()");
		assertEquals("getSimpleName", method.getName());
		assertEquals("getSimpleName()", method.getSignature());
	}

	@Test
	public void testMethodBySignatureReturnsNullIfNotFound() {
		//contract: MetamodelProperty#getMethodBySignature(String) returns method with same signature
		assertNull(Metamodel.getInstance().getConcept(CtClass.class).getProperty(CtRole.NAME).getMethodBySignature("xyz()"));
	}

	private CtField<?> createField(Factory factory, String name) {
		CtField<?> field = factory.Core().createField();
		field.setType((CtTypeReference) factory.Type().booleanType());
		field.setSimpleName(name);
		return field;
	}
}
