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

import org.junit.jupiter.api.Test;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.GitHubIssue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CodeFactoryTest {

	@Test
	public void testThisAccess() {
		final Factory factory = createFactory();
		final CtTypeReference<Object> type = factory.Type().createReference("fr.inria.Test");
		final CtThisAccess<Object> thisAccess = factory.Code().createThisAccess(type);

		assertNotNull(thisAccess.getTarget());
		assertTrue(thisAccess.getTarget() instanceof CtTypeAccess);
		assertEquals(type, ((CtTypeAccess) thisAccess.getTarget()).getAccessedType());
	}

	@Test
	public void testCreateVariableAssignement() {
		Factory factory = createFactory();
		CtType t = factory.Class().create("my.MyClass");
		CtField f = factory.createCtField("f", factory.Type().booleanPrimitiveType(), "false", ModifierKind.PRIVATE);
		t.addField(f);
		CtAssignment va = factory.Code().createVariableAssignment(f.getReference(),false,factory.createLiteral(true));

		//Variable assignment's assignee is a CtVariableWrite that point toward the right variable.
		assertTrue(va.getAssigned() instanceof CtVariableWrite);
		assertEquals(f.getReference(), ((CtVariableWrite) va.getAssigned()).getVariable());
	}
	
	@GitHubIssue(issueNumber= 4956, fixed = true)
	void createCtCatcVariablehWithoutModifiers() {
		// contract: CtCatchVariable without modifiers is created. This a test for the regression of #4940
		Factory factory = createFactory();
		CtTypeReference<Exception> exceptionType = factory.Type().createReference(Exception.class);
		assertDoesNotThrow(() -> factory.Code().createCatchVariable(exceptionType, "e"));
	}
}
