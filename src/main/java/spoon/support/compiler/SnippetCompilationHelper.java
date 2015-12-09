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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.declaration.CtElementImpl;

public class SnippetCompilationHelper {

	private static final String WRAPPER_CLASS_NAME = "Wrapper";
	private static final String WRAPPER_METHOD_NAME = "wrap";

	public static void compileAndReplaceSnippetsIn(CtType<?> c) {
		Factory f = c.getFactory();
		CtType<?> workCopy = c;
		Set<ModifierKind> backup = EnumSet.noneOf(ModifierKind.class);
		backup.addAll(workCopy.getModifiers());
		workCopy.removeModifier(ModifierKind.PUBLIC);

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

	private static CtStatement internalCompileStatement(CtElement st) {
		Factory f = st.getFactory();

		CtClass<?> w = createWrapper(st, f);

		build(f, w);

		CtType<?> c = f.Type().get(WRAPPER_CLASS_NAME);

		// Get the part we want

		CtMethod<?> wrapper = c.getMethod(WRAPPER_METHOD_NAME);

		CtStatement ret = wrapper.getBody().getStatements().get(0);

		// Clean up
		c.getPackage().getTypes().remove(c);

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> compileExpression(
			CtCodeSnippetExpression<T> expr) throws SnippetCompilationError {

		CtReturn<T> ret = (CtReturn<T>) internalCompileStatement(expr);

		return ret.getReturnedExpression();
	}

	private static void build(Factory f, CtType<?> w) {

		String contents = w.toString();

		build(f, contents);
	}

	private static void build(Factory f, String contents) {
		// Build contents
		SpoonCompiler builder = new JDTSnippetCompiler(f, contents);
		try {
			builder.build();
		} catch (Exception e) {
			throw new ModelBuildingException("snippet compilation error while compiling: " + contents, e);
		}
	}

	private static CtClass<?> createWrapper(CtElement element, Factory f) {
		CtClass<?> w = f.Class().create(WRAPPER_CLASS_NAME);

		// Clean up (delete wrapper from factory)
		w.getPackage().getTypes().remove(w);

		CtBlock body = f.Core().createBlock();

		CtTypeReference returnType = f.Type().VOID_PRIMITIVE;
		if (element instanceof CtStatement) {
			body.addStatement((CtStatement) element);
		} else if (element instanceof CtExpression) {
			CtReturn ret = f.Core().createReturn();
			ret.setReturnedExpression((CtExpression) element);
			body.addStatement(ret);
			returnType = f.Type().OBJECT;
		}

		Set<ModifierKind> modifiers = EnumSet.noneOf(ModifierKind.class);

		f.Method().create(
				w,
				modifiers,
				returnType,
				WRAPPER_METHOD_NAME,
				CtElementImpl.<CtParameter<?>>emptyList(),
				CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet(),
				body);

		return w;
	}

}
