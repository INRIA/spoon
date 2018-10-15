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
package spoon.test.factory;

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

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

public class ConstructorFactoryTest {

	@Test
	public void testCreate() throws Exception {
		CtClass<?> type = build("spoon.test.testclasses", "SampleClass");

		Factory factory = type.getFactory();
		ConstructorFactory ctorf = factory.Constructor();
		CoreFactory coref = factory.Core();

		Set<ModifierKind> mods = new HashSet<>();
		mods.add(ModifierKind.PUBLIC);
		List<CtParameter<?>> params = new ArrayList<>();
		CtParameter<?> param = coref.createParameter();
		CtTypeReference<?> tref = factory.Type().createReference(String.class);
		param.setType((CtTypeReference) tref);
		param.setSimpleName("str");
		params.add(param);
		Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();

		ctorf.create(type, mods, params, thrownTypes);

		CtConstructor<?> c = type.getConstructor(tref);
		assertEquals(1, c.getParameters().size());
		assertEquals("str", c.getParameters().get(0).getSimpleName());
	}

	@Test
	public void testCreateDefault() {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		ClassFactory classf = factory.Class();
		ConstructorFactory ctorf = factory.Constructor();

		CtClass<?> ctclass = classf.create("Sample");
		ctorf.createDefault(ctclass);

		CtConstructor<?> c = ctclass.getConstructor();
		assertEquals(0, c.getParameters().size());
	}
}
