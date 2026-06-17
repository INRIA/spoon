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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlueNodeTest {

	@Test
	void testToStringWithoutChildren() {
		// contract: GlueNode.toString() produces a string containing the type's qualified name
		// when there are no children
		Factory factory = new Launcher().getFactory();

		// Create a reference to ArrayList with String as actual type argument
		CtTypeReference<?> arrayListRef = factory.Type().createReference(ArrayList.class);
		CtTypeReference<?> stringRef = factory.Type().createReference(String.class);
		arrayListRef.setActualTypeArguments(List.of(stringRef));

		GlueNode node = new GlueNode(arrayListRef);
		String result = node.toString();

		assertTrue(result.contains("java.util.ArrayList"),
			"toString() should contain the qualified name of the type");
		// The child array block starts with "[\n", distinct from the JSON array for actualArguments
		assertFalse(result.contains("[\n"), "toString() without children should not contain the child array block");
	}

	@Test
	void testToStringWithChild() {
		// contract: GlueNode.toString() includes children's representation when children are present
		Factory factory = new Launcher().getFactory();

		// Create a GlueNode for ArrayList<String>
		CtTypeReference<?> arrayListRef = factory.Type().createReference(ArrayList.class);
		CtTypeReference<?> stringRef = factory.Type().createReference(String.class);
		arrayListRef.setActualTypeArguments(List.of(stringRef));

		GlueNode glueNode = new GlueNode(arrayListRef);

		// Create a DeclarationNode for List as a child
		CtTypeReference<?> listRef = factory.Type().createReference(List.class);
		CtTypeReference<?> eRef = factory.Type().createReference(Object.class);
		listRef.setActualTypeArguments(List.of(eRef));
		DeclarationNode declarationChild = new DeclarationNode(listRef);

		glueNode.addChild(declarationChild);

		String result = glueNode.toString();

		assertTrue(result.contains("java.util.ArrayList"),
			"toString() should contain the GlueNode's qualified name");
		assertTrue(result.contains("java.util.List"),
			"toString() should contain the child DeclarationNode's qualified name");
		// The child array block starts with "[\n", distinct from the JSON array for actualArguments
		assertTrue(result.contains("[\n"),
			"toString() with children should contain the child array block marker");
	}
}
