package spoon.test.replace;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.SpoonException;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.Filter;
import spoon.support.UnsettableProperty;
import spoon.test.SpoonTestHelpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static spoon.test.SpoonTestHelpers.getAllSetters;
import static spoon.test.parent.ParentContractTest.createCompatibleObject;
import static spoon.testing.utils.ModelUtils.createFactory;

@RunWith(Parameterized.class)
public class ReplaceParametrizedTest<T extends CtVisitable> {

	private static Factory factory = createFactory();
	private static final List<CtType<? extends CtElement>> allInstantiableMetamodelInterfaces = SpoonTestHelpers.getAllInstantiableMetamodelInterfaces();

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		List<Object[]> values = new ArrayList<>();
		for (CtType t : allInstantiableMetamodelInterfaces) {
			values.add(new Object[] { t });
		}
		return values;
	}

	@Parameterized.Parameter(0)
	public CtType<?> toTest;


	@Test
	public void testContract() throws Throwable {
		// contract: all elements are replaceable wherever they are in the model
		// this test puts them at all possible locations
		Object o = factory.Core().create((Class<? extends CtElement>) toTest.getActualClass());
		for (CtMethod<?> ctsetter : getAllSetters(toTest)) {
			Method setter = ctsetter.getReference().getActualMethod();
			Class<? extends CtElement> argType = (Class<? extends CtElement>) setter.getParameters()[0].getType();

			if (!CtElement.class.isAssignableFrom(argType)) {
				continue;
			}


			CtElement argument = (CtElement) createCompatibleObject(ctsetter.getParameters().get(0).getType());

			// special cases...
			if (o.getClass().getSimpleName().equals("CtAnnotationFieldAccessImpl") && setter.getName().equals("setVariable")) {
				argument = factory.Core().createFieldReference();
			}
			if (CtFieldAccess.class.isAssignableFrom(o.getClass())&& setter.getName().equals("setVariable")) {
				argument = factory.Core().createFieldReference();
			}
			// we create a fresh object
			CtElement receiver = ((CtElement) o).clone();

			// we invoke the setter
			setter.invoke(receiver, new Object[]{argument});

			final CtElement argument2 = argument.clone();
			assertNotSame(argument, argument2);

			// we do the replace
			argument.replace(argument2);

			if (ctsetter.getAnnotation(UnsettableProperty.class) == null) {
				// the new element is indeed now in this AST
				assertTrue(receiver.getClass().getSimpleName() + " failed for " + setter.getName(), receiver.getElements(new Filter<CtElement>() {
					@Override
					public boolean matches(CtElement element) {
						return element == argument2;
					}
				}).size() > 0);
			}
		}
	}



}
