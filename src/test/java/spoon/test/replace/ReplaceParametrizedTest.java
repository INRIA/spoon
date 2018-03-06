package spoon.test.replace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.parent.ParentContractTest.createCompatibleObject;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import spoon.SpoonException;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.Filter;
import spoon.test.metamodel.MMField;
import spoon.test.metamodel.MMType;
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
		for (MMType t : metaModel.getMMTypes()) {
			if(t.getKind()==MMTypeKind.LEAF) {
				values.add(new Object[] { t });
			}
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public MMType typeToTest;
	

	@Test
	public void testContract() throws Throwable {
		// contract: all elements are replaceable wherever they are in the model
		// this test puts them at all possible locations
		CtType<?> toTest = typeToTest.getModelInterface();
		CtElement o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		for (MMField mmField : typeToTest.getRole2field().values()) {
			Class<?> argType = mmField.getItemValueType().getActualClass();

			if (!CtElement.class.isAssignableFrom(argType)) {
				continue;
			}


			CtElement argument = (CtElement) createCompatibleObject(mmField.getItemValueType());

			// special cases...
			if (o.getClass().getSimpleName().equals("CtAnnotationFieldAccessImpl") && mmField.getRole()==CtRole.VARIABLE) {
				argument = factory.Core().createFieldReference();
			}
			if (CtFieldAccess.class.isAssignableFrom(o.getClass()) &&  mmField.getRole()==CtRole.VARIABLE) {
				argument = factory.Core().createFieldReference();
			}

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
				CtElement arg = argument;
				//Uncomment this assert to see all unsettable properties, which are setting something 
				//assertTrue("Unsettable field " + mmField + " has setter, which changes model", receiver.getElements(e -> e==arg).size() == 0);
				return;
			} 

			// we invoke the setter
			invokeSetter(rh, receiver, argument);
				
			// contract: a property setter sets properties that are visitable by a scanner
			CtElement finalArgument = argument;
			try {
				receiver.accept(new CtScanner() {
					@Override
					public void scan(CtElement e) {
						super.scan(e);
						if (e == finalArgument) {
							throw new SpoonException();
						}
					}
				});
				fail("Not derived field " + mmField.toString() + " should set value");
			} catch (SpoonException expected) {}
			
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

}
