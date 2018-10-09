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
package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ElasticsearchStackoverflowTest {

	private class Scanner extends CtScanner {
		@Override
		public <T> void visitCtExecutableReference(
				CtExecutableReference<T> reference) {
			super.visitCtExecutableReference(reference);
			reference.getDeclaration();
		}
	}

	@Test
	public void testStackOverflow() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/elasticsearch-stackoverflow");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtModel model = launcher.getModel();
		Scanner scanner = new Scanner();
		scanner.scan(model.getRootPackage());

		List<CtExecutableReference> executables = launcher.getModel().getElements(new TypeFilter<>(CtExecutableReference.class));
		assertFalse(executables.isEmpty());

		boolean result = false;
		for (CtExecutableReference execRef : executables) {
			if ("setParentTask".equals(execRef.getSimpleName())) {
				CtTypeReference typeRef = execRef.getDeclaringType();
				assertTrue(typeRef instanceof CtTypeParameterReference);
				assertEquals("ShardRequest", typeRef.getSimpleName());

				CtType typeRefDecl = typeRef.getDeclaration();
				assertEquals("BroadcastShardRequest", typeRefDecl.getSuperclass().getSimpleName());

				assertNull(execRef.getDeclaration());
				result = true;
			}
		}

		assertTrue(result);

	}

}
