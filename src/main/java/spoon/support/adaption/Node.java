/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.adaption;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * A node in the type adaption hierarchy. This hierarchy consists of declaration nodes with formal
 * parameters and glue nodes with actual parameters. Every time we encounter a type definition we
 * create a declaration node for it and assign it the formal type parameters of the type. Then, when
 * we find a type reference (e.g. to a superclass) we record the actual type arguments that were
 * passed in a glue node. This allows us to build a tree detailing how individual parameters change
 * from subclass to parent and across the hierarchy.
 *
 * The java types
 * <pre>{@code
 *   private interface GenericRenameTop<T> {}
 *   private interface GenericRenameMiddle<R> extends GenericRenameTop<R> {}
 * }</pre>
 * would create a hierarchy like this:
 * <pre>
 * {
 *   "String" : "spoon.support.TypeAdaptorTest.GenericRenameTop",
 *   "Formal": ["T"],
 *   [
 *     {
 *       "String" : "spoon.support.TypeAdaptorTest.GenericRenameTop<R>",
 *       "Actual": ["R"],
 *       [
 *         {
 *           "String" : "spoon.support.TypeAdaptorTest.GenericRenameMiddle",
 *           "Formal": ["R"]
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 * As you can see we have a declaration node at the top and bottom and a glue node connecting them,
 * recording how the parameter was renamed.
 *
 * <br><br>Arguments do not have to be simple type parameters. The hierarchy for
 * <pre>{@code
 *   interface ConcreteTypeTop<T> {}
 *   interface ConcreteTypeMiddle extends ConcreteTypeTop<List<String>> {}
 *   interface ConcreteTypeBottom<T extends String & CharSequence> extends ConcreteTypeMiddle {}
 * }</pre>
 * is
 * <pre>
 * {
 *   "String" : "spoon.support.TypeAdaptorTest.ConcreteTypeTop",
 *   "Formal": ["T"],
 *   [
 *     {
 *       "String" : "spoon.support.TypeAdaptorTest.ConcreteTypeTop<java.util.List<java.lang.String>>",
 *       "Actual": ["java.util.List<java.lang.String>"],
 *       [
 *         {
 *           "String" : "spoon.support.TypeAdaptorTest.ConcreteTypeMiddle",
 *           "Formal": [],
 *           [
 *             {
 *               "String" : "spoon.support.TypeAdaptorTest.ConcreteTypeBottom",
 *               "Formal": ["T extends java.lang.String & java.lang.CharSequence"]
 *             }
 *           ]
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 * We can see here that the type parameter vanished in the first glue node as it was replaced with a
 * concrete type. The glue node between the last two was also skipped as the type has no formal
 * parameters and therefore there will never be a mapping for them.
 */
abstract class Node {

	protected final CtTypeReference<?> inducedBy;
	protected final Set<Node> lowerNodes;
	protected final TypeAdaptor typeAdaptor;

	protected Node(CtTypeReference<?> inducedBy, TypeAdaptor typeAdaptor) {
		this.inducedBy = inducedBy;
		this.typeAdaptor = typeAdaptor;
		this.lowerNodes = new HashSet<>();
	}

	/**
	 * @return true if this is a glue node that has actual type arguments
	 */
	public abstract boolean isGlueNode();

	/**
	 * @return true if this is a declaration node with formal type arguments
	 */
	public boolean isDeclarationNode() {
		return !isGlueNode();
	}

	/**
	 * Resolves a type parameter within this node's hierarchy. This is done by walking down the chain
	 * until we encounter a concrete type or reach the end. If we can not find a mapping, empty is
	 * returned. If we can't find the declaration for a formal parameter however, an exception is
	 * thrown as type resolution is longer possible.
	 *
	 * @param reference the reference to resolve
	 * @return the resolved reference if found
	 */
	public abstract Optional<CtTypeReference<?>> resolveTypeParameter(CtTypeParameterReference reference);

	/**
	 * Add a new child to this node.
	 *
	 * @param lower the child
	 */
	public void addLower(Node lower) {
		lowerNodes.add(lower);
	}

	/**
	 * @return the qualified name of the reference this node points to
	 */
	public String getInducedQualifiedName() {
		return inducedBy.getQualifiedName();
	}

	/**
	 * @return the arguments of this node formatted like a json key ({@code "<name>": <value>}
	 * @see #quote(List) for how to format it
	 */
	protected abstract String argumentsForToString();

	@Override
	public String toString() {
		String result = "";

		result += "{";
		result += "\n  \"String\" : " + quote(inducedBy.toString()) + ",";
		result += "\n  " + argumentsForToString();

		if (!lowerNodes.isEmpty()) {
			result += ",\n  [";
			String childResult = lowerNodes.stream()
					.map(Node::toString)
					.collect(Collectors.joining(",\n"));
			result += "\n";
			result += Arrays.stream(childResult.split("\n"))
					.map(it -> "    " + it)
					.collect(Collectors.joining("\n"));
			result += "\n  ]";
		}
		result += "\n}";

		return result;
	}

	private String quote(String input) {
		return '"' + input + '"';
	}

	/**
	 * Converts a list of values to a crudely formatted JSON-y array string. You can use this for
	 * {@link #argumentsForToString()}.
	 *
	 * @param input the input list
	 * @return the list as a quoted array
	 */
	protected String quote(List<?> input) {
		return "["
				+ input.stream()
				.map(Objects::toString)
				.map(this::quote)
				.collect(Collectors.joining(", "))
				+ "]";
	}

	/**
	 * Builds a new node for a given reference.
	 *
	 * @param adaptor the type adaptor to use for {@link #resolveTypeParameter(CtTypeParameterReference)}
	 * @param reference the reference to build a node for
	 * @return a node for the given reference
	 */
	public static Node forReference(TypeAdaptor adaptor, CtTypeReference<?> reference) {
		if (reference.getActualTypeArguments().isEmpty()) {
			CtType<?> typeDeclaration = reference.getTypeDeclaration();
			if (typeDeclaration == null) {
				return forActual(adaptor, reference);
			}
			return Node.forFormal(adaptor, typeDeclaration.getReference());
		}
		return Node.forActual(adaptor, reference);
	}

	private static Node forActual(TypeAdaptor adaptor, CtTypeReference<?> inducedBy) {
		return new GlueNode(inducedBy, adaptor, inducedBy.getActualTypeArguments());
	}

	private static Node forFormal(TypeAdaptor adaptor, CtTypeReference<?> inducedBy) {
		return new DeclarationNode(
				inducedBy,
				adaptor,
				inducedBy.getTypeDeclaration().getFormalCtTypeParameters()
		);
	}
}
