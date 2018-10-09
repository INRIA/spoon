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
package spoon.test.replace;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.SpoonException;
import spoon.metamodel.ConceptKind;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.MetamodelProperty;
import spoon.metamodel.Metamodel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.parent.ContractOnSettersParametrizedTest.createCompatibleObject;

@RunWith(Parameterized.class)
public class ReplaceParametrizedTest<T extends CtVisitable> {

	private static Metamodel metaModel;

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		metaModel = Metamodel.getInstance();

		List<Object[]> values = new ArrayList<>();
		for (MetamodelConcept t : metaModel.getConcepts()) {
			if(t.getKind()==ConceptKind.LEAF) {
				values.add(new Object[] { t });
			}
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public MetamodelConcept typeToTest;
	

	@Test
	public void testContract() {
		List<String> problems = new ArrayList<>();
		
		// contract: all elements are replaceable wherever they are in the model
		// this test puts them at all possible locations
		CtType<?> toTest = typeToTest.getMetamodelInterface();
		Factory factory = toTest.getFactory();

		CtElement o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		Map<CtRole, MetamodelProperty> roleToProperty = typeToTest.getRoleToProperty();
		for (MetamodelProperty mmField : roleToProperty.values()) {
			Class<?> argType = mmField.getTypeofItems().getActualClass();

			if (!CtElement.class.isAssignableFrom(argType)) {
				continue;
			}


			CtTypeReference<?> itemType = mmField.getTypeofItems();
			// special cases...
			if (itemType.getQualifiedName().equals(CtStatement.class.getName())) {
				//the children of CtLoop wraps CtStatement into an implicit CtBlock. So make a block directly to test plain get/set and not wrapping.
				itemType = factory.createCtTypeReference(CtBlock.class);
			}
			if ("CtAnnotationFieldAccessImpl".equals(o.getClass().getSimpleName()) && mmField.getRole()==CtRole.VARIABLE) {
				itemType = factory.createCtTypeReference(CtFieldReference.class);
			} else if (CtFieldAccess.class.isAssignableFrom(o.getClass()) &&  mmField.getRole()==CtRole.VARIABLE) {
				itemType = factory.createCtTypeReference(CtFieldReference.class);
			}
			CtElement argument = (CtElement) createCompatibleObject(itemType);

			assertNotNull(argument);

			// we create a fresh object
			CtElement receiver = o.clone();

			RoleHandler rh = RoleHandlerHelper.getRoleHandler(o.getClass(), mmField.getRole());

			if (mmField.isUnsettable()) {
				try {
					// we invoke the setter
					invokeSetter(rh, receiver, argument);
				} catch (SpoonException e) {
					//ok this unsettable property has no setter at all
					return;
				}
				//this unsettable property has setter, but it should do nothing
				CtRole argumentsRoleInParent = argument.getRoleInParent();
				if (argumentsRoleInParent == null) {
					//OK - unsettable property set no value
					continue;
				}
				if (argumentsRoleInParent == mmField.getRole()) {
					problems.add("UnsettableProperty " + mmField + " sets the value");
				} else {
					if (mmField.isDerived()) {
						//it is OK, that setting of value into derived unsettable field influences other field
						//Example 1: CtCatchVariable.setType(x) influences result of getMultitype()
						//Example 2: CtEnumValue.setAssignment(x) influences result of getDefaultExpression()
					} else {
						problems.add("UnsettableProperty " + mmField + " sets the value into different role " + argumentsRoleInParent);
					}
				}
				continue;
			} 

			// we invoke the setter
			invokeSetter(rh, receiver, argument);
				
			// contract: a property setter sets properties that are visitable by a scanner
			CtElement finalArgument = argument;
			class Scanner extends CtScanner {
				boolean found = false;
				@Override
				public void scan(CtRole role, CtElement e) {
					super.scan(role, e);
					if (e == finalArgument) {
						if (rh.getRole()==role || rh.getRole().getSuperRole()==role) {
							found = true;
							return;
						}
						problems.add("Argument was set into " + rh.getRole() + " but was found in " + role);
					}
				}
			}
			Scanner s = new Scanner();
			receiver.accept(s);
			assertTrue("Settable field " + mmField.toString() + " should set value.\n" + getReport(problems), s.found);
			
			// contract: a property getter on the same role can be used to get the value back
			assertSame(argument, invokeGetter(rh, receiver));
			
			final CtElement argument2 = argument.clone();
			assertNotSame(argument, argument2);

			// we do the replace
			argument.replace(argument2);

			// the new element is indeed now in this AST
			assertTrue(receiver.getClass().getSimpleName() + " failed for " + mmField, receiver.getElements(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					return element == argument2;
				}
			}).size() == 1);
		}
		if (!problems.isEmpty()) {
			fail(getReport(problems));
		}
	}
	
	private String getReport(List<String> problems) {
		if (!problems.isEmpty()) {
			StringBuilder report = new StringBuilder();
			report.append("The accessors of " + typeToTest + " have problems:");
			for (String problem : problems) {
				report.append("\n").append(problem);
			}
			return report.toString();
		}
		return "";
	}

	private static void invokeSetter(RoleHandler rh, CtElement receiver, CtElement item) {
		if (rh.getContainerKind() == ContainerKind.SINGLE) {
			rh.setValue(receiver, item);
		} else if (rh.getContainerKind() == ContainerKind.MAP) {
			rh.asMap(receiver).put("dummyKey", item);
		} else {
			rh.asCollection(receiver).add(item);
		}
	}
	private static CtElement invokeGetter(RoleHandler rh, CtElement receiver) {
		if (rh.getContainerKind() == ContainerKind.SINGLE) {
			return rh.getValue(receiver);
		} else if (rh.getContainerKind() == ContainerKind.MAP) {
			return (CtElement) rh.asMap(receiver).get("dummyKey");
		} else {
			return (CtElement) rh.asCollection(receiver).stream().findFirst().get();
		}
	}

}
