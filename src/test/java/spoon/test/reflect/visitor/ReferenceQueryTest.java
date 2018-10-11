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
package spoon.test.reflect.visitor;

import org.junit.Test;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.test.reflect.visitor.testclasses.ReferenceQueryTestEnum;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class ReferenceQueryTest {
	@Test
	public void getAllTypeReferencesInEnum() throws Exception {
		CtEnum<ReferenceQueryTestEnum> testEnum = build("spoon.test.reflect.visitor.testclasses", "ReferenceQueryTestEnum");
		List< CtTypeReference<?> > enumTypeRefs = Query.getElements(testEnum, new ReferenceTypeFilter<>(CtTypeReference.class));
		TypeFactory typeFactory = testEnum.getFactory().Type();
		for (Class<?> c : new Class<?>[]{Integer.class, Long.class, Boolean.class, Number.class, String.class, Void.class}) {
			assertTrue("the reference query on the enum should return all the types defined in the enum declaration", enumTypeRefs.contains(typeFactory.createReference(c)));
		}
	}
}
