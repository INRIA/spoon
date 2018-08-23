package spoon.test.change;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import spoon.support.modelobs.ChangeCollector;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.test.change.testclasses.SubjectOfChange;
import spoon.testing.utils.ModelUtils;

public class ChangeCollectorTest {

	@Test
	public void testChangeCollector() throws Exception {
		//contract: test ChangeCollector
		CtType<?> ctClass = ModelUtils.buildClass(SubjectOfChange.class);

		Factory f = ctClass.getFactory();

		assertNull(ChangeCollector.getChangeCollector(f.getEnvironment()));

		ChangeCollector changeCollector = new ChangeCollector().attachTo(f.getEnvironment());

		assertSame(changeCollector, ChangeCollector.getChangeCollector(f.getEnvironment()));

		//contract: after ChangeCollector is created there is no direct or indirect change
		assertEquals(0, changeCollector.getChanges(f.getModel().getRootPackage()).size());
		f.getModel().getRootPackage().filterChildren(null).forEach((CtElement e) -> {
			assertEquals(0, changeCollector.getDirectChanges(e).size());
		});

		ctClass.setSimpleName("aaa");

		assertEquals(new HashSet<>(Arrays.asList(CtRole.SUB_PACKAGE)), changeCollector.getChanges(f.getModel().getRootPackage()));
		assertEquals(new HashSet<>(), changeCollector.getDirectChanges(f.getModel().getRootPackage()));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.CONTAINED_TYPE)), changeCollector.getChanges(ctClass.getPackage()));
		assertEquals(new HashSet<>(Arrays.asList()), changeCollector.getDirectChanges(ctClass.getPackage()));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getDirectChanges(ctClass));

		CtField<?> field = ctClass.getField("someField");
		field.getDefaultExpression().delete();

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getDirectChanges(ctClass));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getChanges(field));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getDirectChanges(field));

		/*
		 * TODO:
		 * field.delete();
		 * calls internally setTypeMembers, which deletes everything and then adds remaining
		 */
		ctClass.removeTypeMember(field);

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getDirectChanges(ctClass));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getChanges(field));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getDirectChanges(field));
	}
}
