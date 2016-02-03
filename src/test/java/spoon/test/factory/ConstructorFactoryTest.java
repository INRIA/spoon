package spoon.test.factory;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.ConstructorFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.testing.utils.ModelUtils.build;

public class ConstructorFactoryTest {

	@Test
	public void testCreate() throws Exception {

		CtClass<?> type = build("spoon.test", "SampleClass");

		Factory factory = type.getFactory();
		ConstructorFactory ctorf = factory.Constructor();
		CoreFactory coref = factory.Core();

		Set<ModifierKind> mods = new HashSet<ModifierKind>();
		mods.add(ModifierKind.PUBLIC);
		List<CtParameter<?>> params = new ArrayList<CtParameter<?>>();
		CtParameter<?> param = coref.createParameter();
		CtTypeReference<?> tref = factory.Type().createReference(String.class);
		param.setType((CtTypeReference)tref);
		param.setSimpleName("str");
		params.add(param);
		Set<CtTypeReference<? extends Throwable>> thrownTypes =
				new HashSet<CtTypeReference<? extends Throwable>>();

		ctorf.create(type,mods,params,thrownTypes);

		CtConstructor<?> c = type.getConstructor(tref);
		Assert.assertEquals(1, c.getParameters().size());
		Assert.assertEquals("str", c.getParameters().get(0).getSimpleName());
	}

	@Test
	public void testCreateDefault() {

		Factory factory =
			new FactoryImpl(new DefaultCoreFactory(),new StandardEnvironment());
		ClassFactory classf = factory.Class();
		ConstructorFactory ctorf = factory.Constructor();

		CtClass<?> ctclass = classf.create("Sample");
		ctorf.createDefault(ctclass);

		CtConstructor<?> c = ctclass.getConstructor();
		Assert.assertEquals(0, c.getParameters().size());
	}
}
