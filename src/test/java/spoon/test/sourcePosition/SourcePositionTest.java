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
package spoon.test.sourcePosition;

import org.junit.Test;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.cu.CompilationUnitImpl;
import spoon.support.reflect.cu.position.BodyHolderSourcePositionImpl;
import spoon.support.reflect.cu.position.DeclarationSourcePositionImpl;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.test.sourcePosition.testclasses.Brambora;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class SourcePositionTest {

	@Test
	public void equalPositionsHaveSameHashcode() throws Exception {
		String packageName = "spoon.test.testclasses";
		String sampleClassName = "SampleClass";
		String qualifiedName = packageName + "." + sampleClassName;

		Filter<CtMethod<?>> methodFilter = new TypeFilter<>(CtMethod.class);

		Factory aFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> methods = aFactory.Class().get(qualifiedName).getElements(methodFilter);

		Factory newInstanceOfSameFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> newInstanceOfSameMethods = newInstanceOfSameFactory.Class().get(qualifiedName).getElements(methodFilter);

		assertEquals(methods.size(), newInstanceOfSameMethods.size());
		for (int i = 0; i < methods.size(); i += 1) {
			SourcePosition aPosition = methods.get(i).getPosition();
			SourcePosition newInstanceOfSamePosition = newInstanceOfSameMethods.get(i).getPosition();
			assertTrue(aPosition.equals(newInstanceOfSamePosition));
			assertEquals(aPosition.hashCode(), newInstanceOfSamePosition.hashCode());
		}
	}

	private Factory factoryFor(String packageName, String className) throws Exception {
		return build(packageName, className).getFactory();
	}
	
	@Test
	public void testSourcePositionOfSecondPrimitiveType() throws Exception {
		/*
		 * contract: fix bug: the other references to primitive type (e.g. void)
		 * in return type of ExecutableRefernce "System.out.println" DOES NOT copy the source position
		 * from the return type of owner method
		 */
		CtType<?> type = ModelUtils.buildClass(Brambora.class);
		CtInvocation<?> invocation = type.getMethodsByName("sourcePositionOfMyReturnTypeMustNotBeCopied").get(0).getBody().getStatement(0);
		CtExecutableReference<?> execRef = invocation.getExecutable();
		CtTypeReference<?> typeOfReturnValueOfPrintln = execRef.getType();
		assertEquals("void", typeOfReturnValueOfPrintln.getQualifiedName());
		SourcePosition sp = typeOfReturnValueOfPrintln.getPosition();
		if (sp.isValidPosition()) {
			//it copied source position from owner method return type
			fail("The source position of invisible implicit reference to void is: [" + sp.getSourceStart() + "; " + sp.getSourceEnd() + "]");
		}
	}

	@Test
	public void testSourcePositionStringFragment() {
		CompilationUnit cu = new CompilationUnitImpl() {
			@Override
			public String getOriginalSourceCode() {
				return "0123456789";
			}
		};
		SourcePositionImpl sp = new SourcePositionImpl(cu, 1, 9, null);
		assertEquals("|1;9|123456789|", sp.getSourceDetails());
		
		DeclarationSourcePositionImpl dsp = new DeclarationSourcePositionImpl(cu, 4, 7, 2, 2, 1,9, null);
		assertEquals("|1;9|123456789|\n" + 
				"modifier = |2;2|2|\n" + 
				"name = |4;7|4567|", dsp.getSourceDetails());
		
		BodyHolderSourcePositionImpl bhsp = new BodyHolderSourcePositionImpl(cu, 4, 7, 2, 2, 1,9, 8, 9, null);
		assertEquals("|1;9|123456789|\n" + 
				"modifier = |2;2|2|\n" + 
				"name = |4;7|4567|\n" + 
				"body = |8;9|89|", bhsp.getSourceDetails());
	}
}