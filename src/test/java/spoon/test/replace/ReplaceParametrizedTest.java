package spoon.test.replace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.parent.ParentContractTest.createCompatibleObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import spoon.SpoonException;
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
import spoon.test.metamodel.MetamodelProperty;
import spoon.test.metamodel.MetamodelConcept;
import spoon.test.metamodel.MMTypeKind;
import spoon.test.metamodel.SpoonMetaModel;

@RunWith(Parameterized.class)
public class ReplaceParametrizedTest<T extends CtVisitable> {

	private static Factory factory;
	private static SpoonMetaModel metaModel;

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		metaModel = new SpoonMetaModel(new File("src/main/java"));
		factory = metaModel.getFactory();

		List<Object[]> values = new ArrayList<>();
		for (MetamodelConcept t : metaModel.getConcepts()) {
			if(t.getKind()==MMTypeKind.LEAF) {
				values.add(new Object[] { t });
			}
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public MetamodelConcept typeToTest;
	

	@Test
	public void testContract() throws Throwable {
		List<String> problems = new ArrayList<>();
		
		// contract: all elements are replaceable wherever they are in the model
		// this test puts them at all possible locations
		CtType<?> toTest = typeToTest.getModelInterface();
		CtElement o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		for (MetamodelProperty mmField : typeToTest.getRoleToProperty().values()) {
			Class<?> argType = mmField.getItemValueType().getActualClass();

			if (!CtElement.class.isAssignableFrom(argType)) {
				continue;
			}


			CtTypeReference<?> itemType = mmField.getItemValueType();
			// special cases...
			if (itemType.getQualifiedName().equals(CtStatement.class.getName())) {
				//the children of CtLoop wraps CtStatement into an implicit CtBlock. So make a block directly to test plain get/set and not wrapping.
				itemType = factory.createCtTypeReference(CtBlock.class);
			}
			if (o.getClass().getSimpleName().equals("CtAnnotationFieldAccessImpl") && mmField.getRole()==CtRole.VARIABLE) {
				itemType = factory.createCtTypeReference(CtFieldReference.class);
			} else if (CtFieldAccess.class.isAssignableFrom(o.getClass()) &&  mmField.getRole()==CtRole.VARIABLE) {
				itemType = factory.createCtTypeReference(CtFieldReference.class);
			}
			CtElement argument = (CtElement) createCompatibleObject(itemType);

			assertNotNull(argument);

			// we create a fresh object
			CtElement receiver = ((CtElement) o).clone();

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
//						if (rh.getRole()==CtRole.TYPE && role==CtRole.MULTI_TYPE) {
//							//CtCatchVaraible#type sets CtCatchVaraible#multiType - OK 
//							found = true;
//							return;
//						}
						problems.add("Argument was set into " + rh.getRole() + " but was found in " + role);
					}
				}
			};
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
		if (problems.size() > 0) {
			fail(getReport(problems));
		}
	}
	
	private String getReport(List<String> problems) {
		if (problems.size() > 0) {
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
