package spoon.test.imports;

import org.junit.Test;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.test.imports.testclasses.A;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static spoon.testing.utils.ModelUtils.build;

public class CtUnresolvedImportTest {

	@Test
	public void testEquality() throws Exception {

		final Factory factory = build(A.class);
		CtImport i1 = factory.createUnresolvedImport("spoon.A", false);
		CtImport i2 = factory.createUnresolvedImport("spoon.A", false);
		CtImport i3 = factory.createUnresolvedImport("spoon.A", true);
		CtImport i4 = factory.createUnresolvedImport("spoon.B", false);
		CtImport i5 = factory.createUnresolvedImport("spoon.m", true);
		CtImport i6 = factory.createUnresolvedImport("spoon.m", true);


		CtReference ref = factory.createReference("spoon.A");
		CtImport i7 = factory.createImport(ref);

		//contract: CtUnresolvedImport#equals compare the contained reference and isStatic field
		assertFalse(i1.equals(null));
		assertFalse(i1.equals(i7));
		assertFalse(i7.equals(i1));
		assertTrue(i1.equals(i2));
		assertFalse(i1.equals(i3));
		assertFalse(i1.equals(i4));
		assertTrue(i5.equals(i6));

		//contract: CtUnresolvedImport#clone yield a equivalent CtImport
		assertTrue(i5.equals(i5.clone()));

		//contract: CtUnresolvedImport#hashCode compare the contained reference and isStatic field
		assertEquals(i1.hashCode(), i2.hashCode());
		assertNotEquals(i1, i3);

		Set<CtImport> imports = new HashSet<>();
		assertTrue(imports.add(i1));
		assertFalse(imports.add(i2));
		assertTrue(imports.add(i3));
		assertTrue(imports.add(i4));
		assertTrue(imports.add(i5));
		assertFalse(imports.add(i6));
		assertTrue(imports.add(i7));
		assertEquals(5, imports.size());
	}

}