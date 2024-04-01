/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;


import spoon.reflect.reference.CtTypeReference;
/**
 * This element represents a class declaration.
 *
 * <pre>{@code
 * class Foo {
 *   // this is a receiver parameter
 *   void bar(Foo this) {
 *   }
 * }}
 * </pre>
 */
public interface CtReceiverParameter extends CtTypedElement<Object>, CtShadowable, CtElement {

	@Override
	CtTypeReference<Object> getType();

	@Override
	CtReceiverParameter clone();

}
