package spoon.test.reflect.visitor;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.test.reflect.visitor.testclasses.ReferenceQueryTestEnum;

import java.util.List;

import static spoon.testing.utils.ModelUtils.build;

public class ReferenceQueryTest {
	@Test
	public void getAllTypeReferencesInEnum() throws Exception {
		CtEnum<ReferenceQueryTestEnum> testEnum = build("spoon.test.reflect.visitor.testclasses", "ReferenceQueryTestEnum");
		List< CtTypeReference<?> > enumTypeRefs = Query.getElements(testEnum, new ReferenceTypeFilter<>(CtTypeReference.class));
		TypeFactory typeFactory = testEnum.getFactory().Type();
		for (Class<?> c : new Class<?>[]{Integer.class, Long.class, Boolean.class, Number.class, String.class, Void.class}) {
			Assert.assertTrue("the reference query on the enum should return all the types defined in the enum declaration", enumTypeRefs.contains(typeFactory.createReference(c)));
		}
	}
}