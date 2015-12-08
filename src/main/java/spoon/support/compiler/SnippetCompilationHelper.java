/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.compiler;

import java.util.EnumSet;
import java.util.Set;

import spoon.compiler.ModelBuildingException;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.declaration.CtElementImpl;

public class SnippetCompilationHelper {

	public static void compileAndReplaceSnippetsIn(CtType<?> c) {
		Factory f = c.getFactory();
		CtType<?> workCopy = c;
		Set<ModifierKind> backup = EnumSet.noneOf(ModifierKind.class);
		backup.addAll(workCopy.getModifiers());

		workCopy.getModifiers().remove(ModifierKind.PUBLIC);

		try {
			build(f, workCopy.toString());
		} finally {
			// restore modifiers
			c.setModifiers(backup);
		}

	}

	public static CtStatement compileStatement(CtCodeSnippetStatement st)
			throws SnippetCompilationError {
		return internalCompileStatement(st);
	}

	private static CtStatement internalCompileStatement(CtStatement st) {
		Factory f = st.getFactory();

		CtClass<?> w = createWrapper(st, f);

		compile(f, w);

		CtType<?> c = f.Type().get("Wrapper");

		// Get the part we want

		CtMethod<?> wrapper = Query.getElements(c, new Filter<CtMethod<?>>() {

			public boolean matches(CtMethod<?> element) {
				return element.getSimpleName().equals("wrap");
			}

		}).get(0);

		CtStatement ret = wrapper.getBody().getStatements().get(0);

		// Clean up
		c.getPackage().getTypes().remove(c);

		// check typing?

		return ret;
	}

	private static CtClass<?> createWrapper(CtStatement st, Factory f) {
		CtClass<?> w = f.Class().create("Wrapper");

		CtBlock<Void> body = f.Core().createBlock();

		body.addStatement(st);

		Set<ModifierKind> x = EnumSet.noneOf(ModifierKind.class);

		f.Method().create(
				w,
				x,
				f.Type().createReference(void.class),
				"wrap",
				CtElementImpl.<CtParameter<?>>emptyList(),
				CtElementImpl
						.<CtTypeReference<? extends Throwable>>emptySet(),
				body);

		return w;
	}

	private static void compile(Factory f, CtType<?> w)
			throws SnippetCompilationError {

		String contents = w.toString();

		build(f, contents);

	}

	private static void build(Factory f, String contents) {
		// Build contents
		SpoonCompiler builder = new JDTSnippetCompiler(f, contents);
		try {
			builder.build();
		} catch (Exception e) {
			throw new ModelBuildingException(
					"snippet compilation error while compiling: " + contents, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> compileExpression(
			CtCodeSnippetExpression<T> expr) throws SnippetCompilationError {
		// create wrapping template

		Factory f = expr.getFactory();
		CtClass<?> w = createWrapper(expr, f);

		String contents = w.toString();

		build(f, contents);

		CtType<?> c = f.Type().get("Wrapper");

		// Get the part we want

		CtMethod<T> wrapper = Query.getElements(c, new Filter<CtMethod<T>>() {

			public boolean matches(CtMethod<T> element) {
				return element.getSimpleName().equals("wrap");
			}

		}).get(0);

		CtReturn<T> ret = (CtReturn<T>) wrapper.getBody().getStatements()
				.get(0);

		// Clean up (delete wrapper from factory)
		c.getPackage().getTypes().remove(c);

		return ret.getReturnedExpression();
	}

	private static <R, B extends R> CtClass<?> createWrapper(
			CtExpression<B> st, Factory f) {
		CtClass<?> w = f.Class().create("Wrapper");

		CtBlock<B> body = f.Core().createBlock();
		CtReturn<B> ret = f.Core().createReturn();
		ret.setReturnedExpression(st);
		body.addStatement(ret);

		Set<ModifierKind> x = EnumSet.noneOf(ModifierKind.class);

		f.Method().create(
				w,
				x,
				f.Type().createReference(Object.class),
				"wrap",
				CtElementImpl.<CtParameter<?>>emptyList(),
				CtElementImpl
						.<CtTypeReference<? extends Throwable>>emptySet(),
				body);

		return w;
	}

}
