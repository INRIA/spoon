/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeclarationNodeTest {

	@Test
	void testToStringContainsQualifiedName() {
		// contract: DeclarationNode.toString() includes the qualified name of the induced type
		Factory factory = new Launcher().getFactory();
		CtTypeReference<?> listRef = factory.Type().createReference(List.class);

		DeclarationNode node = new DeclarationNode(listRef);
		String result = node.toString();

		assertTrue(result.contains("java.util.List"), "toString() should contain the qualified name");
		assertTrue(result.startsWith("{"), "toString() should start with '{'");
		assertTrue(result.endsWith("}"), "toString() should end with '}'");
		// No children: the result should not contain the children section marker "  [\n"
		assertFalse(result.contains("  [\n"), "toString() without children should not contain children section");
	}

	@Test
	void testToStringWithChildren() {
		// contract: DeclarationNode.toString() includes children when present
		Factory factory = new Launcher().getFactory();
		CtTypeReference<?> listRef = factory.Type().createReference(List.class);
		CtTypeReference<?> arrayListRef = factory.Type().createReference(java.util.ArrayList.class);

		DeclarationNode declarationNode = new DeclarationNode(listRef);
		GlueNode glueChild = new GlueNode(arrayListRef);
		declarationNode.addChild(glueChild);

		String result = declarationNode.toString();

		assertTrue(result.contains("java.util.List"), "toString() should contain the qualified name of the declaration node");
		assertTrue(result.contains("java.util.ArrayList"), "toString() with children should contain the child's qualified name");
		assertTrue(result.contains("["), "toString() with children should contain '['");
	}
}
