package spoon.test.serializable;

import static org.junit.Assert.*;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.util.ByteSerialization;

public class SerializableTest {

	@Test
	public void testSerialCtStatement() throws Exception {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement sta2 = (factory).Code()
				.createCodeSnippetStatement("String hello =\"t1\"; System.out.println(hello)").compile();

		byte[] ser = ByteSerialization.serialize(sta2);
		CtStatement des = (CtStatement) ByteSerialization.deserialize(ser);

		String sigBef = sta2.getSignature();
		String sigAf = des.getSignature();
		
		CtType<?> typeBef = sta2.getParent(CtType.class);
		assertNotNull(typeBef);
		
		assertEquals(sigBef, sigAf);
		
		des.setFactory(factory);
		String toSBef = sta2.toString();
		String toSgAf = des.toString();
		
		assertEquals(toSBef, toSgAf);

		CtType<?> typeDes = des.getParent(CtType.class);
		assertNotNull(typeDes);
		//After deserialization, getDeclaringType throws an exception
		CtType<?> decl =  typeDes.getDeclaringType();
		assertNull(decl);
				
		CtPackage parentOriginal = (CtPackage) typeBef.getParent();
		CtPackage parentDeser = (CtPackage) typeDes.getParent();
		
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,parentOriginal.getSimpleName());
		
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,parentDeser.getSimpleName());
				
	}

	@Test
	public void testSerialFile() throws Exception {
		CtType<?> type = build("spoon.test.serializable", "Dummy");
		byte[] ser = ByteSerialization.serialize(type);
		CtType<?> des = (CtType<?>) ByteSerialization.deserialize(ser);
	}
	
}
