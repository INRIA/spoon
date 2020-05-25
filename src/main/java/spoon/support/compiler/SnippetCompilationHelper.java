/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import spoon.SpoonModelBuilder;
import spoon.compiler.ModelBuildingException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Helper class for working with snippets */
public class SnippetCompilationHelper {

	private SnippetCompilationHelper() { }

	private static final String WRAPPER_CLASS_NAME = "Wrapper";
	private static final String WRAPPER_METHOD_NAME = "wrap";

	/**
	 * Takes the class given as parameter, pretty-prints it, get the JDT'ast and the corrspondong Spoon AST
	 * and finally replace all children of initialClass by the the fresh ones.
	 *
	 * This results that all snippets are now full-fledged ASTs.
	 *
	 */
	public static void compileAndReplaceSnippetsIn(CtType<?> initialClass) {

		Map<CtPath, CtElement> elements2before = new HashMap<>();
		Map<CtPath, CtElement> elements2after = new HashMap<>();
		for (Object o : initialClass.filterChildren(new TypeFilter<>(CtCodeSnippet.class)).list()) {
			CtElement el = (CtElement) o;
			elements2before.put(el.getPath(), el);
		}
		Factory f = initialClass.getFactory();

		// we need to slightly play with the modifiers
		Set<ModifierKind> backup = EnumSet.noneOf(ModifierKind.class);
		backup.addAll(initialClass.getModifiers());
		initialClass.removeModifier(ModifierKind.PUBLIC);

		// we need to delete the current class from its package
		// otherwsise the new type is not added because it has the same fully qualified name
		initialClass.delete();

		try {
			build(f, "package " + initialClass.getPackage().getQualifiedName() + ";" + initialClass.toString());
		} finally {
			// restore modifiers
			initialClass.setModifiers(backup);
		}

		// we get the newly created class
		CtType<?> newClass = f.Type().get(initialClass.getQualifiedName());

		// we find the snippets that are now ASTs
		for (CtPath p : elements2before.keySet()) {
			elements2after.put(p, p.evaluateOn(f.getModel().getRootPackage()).iterator().next());
		}

		// and we replace the new class in the factory by the initial one
		newClass.replace(initialClass);

		// and we replace the snippets
		for (Map.Entry<CtPath, CtElement> ctPath : elements2before.entrySet()) {
			CtElement toReplace = ctPath.getValue();
			toReplace.replace(elements2after.get(ctPath.getKey()));
		}
	}

	public static CtStatement compileStatement(CtCodeSnippetStatement st)
			throws SnippetCompilationError {
		return internalCompileStatement(st, st.getFactory().Type().VOID_PRIMITIVE);
	}

	public static CtStatement compileStatement(CtCodeSnippetStatement st, CtTypeReference returnType)
			throws SnippetCompilationError {
		return internalCompileStatement(st, returnType);
	}

	private static CtStatement internalCompileStatement(CtElement st, CtTypeReference returnType) {
		Factory f = st.getFactory();

		String contents = createWrapperContent(st, f, returnType);

		build(f, contents);

		CtType<?> c = f.Type().get(WRAPPER_CLASS_NAME);

		// Get the part we want

		CtMethod<?> wrapper = c.getMethod(WRAPPER_METHOD_NAME);

		List<CtStatement> statements = wrapper.getBody().getStatements();
		CtStatement ret = statements.get(statements.size() - 1);

		// Clean up
		c.getPackage().removeType(c);

		//disconnect element from the parent, so it can be added to another model
		ret.delete();
		ret.setParent(null);

		if (ret instanceof CtClass) {
			CtClass klass = (CtClass) ret;
			ret.getFactory().Package().getRootPackage().addType(klass);
			klass.setSimpleName(klass.getSimpleName().replaceAll("^[0-9]*", ""));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> compileExpression(
			CtCodeSnippetExpression<T> expr) throws SnippetCompilationError {

		CtReturn<T> ret = (CtReturn<T>) internalCompileStatement(expr, expr.getFactory().Type().OBJECT);

		CtExpression<T> returnedExpression = ret.getReturnedExpression();

		// this compiled expression is not in a parent, ready to be used
		returnedExpression.delete();
		returnedExpression.setParent(null);

		return returnedExpression;
	}

	private static void build(Factory f, String contents) {
		// Build contents
		SpoonModelBuilder builder = new JDTSnippetCompiler(f, contents);
		try {
			builder.build();
		} catch (Exception e) {
			throw new ModelBuildingException("snippet compilation error while compiling: " + contents, e);
		}
	}

	private static String createWrapperContent(final CtElement element, final Factory f, final CtTypeReference returnType) {
		CtClass<?> w = f.Class().create(WRAPPER_CLASS_NAME);

		CtBlock body = f.Core().createBlock();

		if (element instanceof CtStatement) {
			body.addStatement((CtStatement) element);
		} else if (element instanceof CtExpression) {
			CtReturn ret = f.Core().createReturn();
			ret.setReturnedExpression((CtExpression) element);
			body.addStatement(ret);
		}

		Set<ModifierKind> modifiers = EnumSet.of(ModifierKind.STATIC);

		Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();
		thrownTypes.add(f.Class().<Throwable>get(Throwable.class).getReference());
		f.Method().create(
				w,
				modifiers,
				returnType,
				WRAPPER_METHOD_NAME,
				CtElementImpl.<CtParameter<?>>emptyList(),
				thrownTypes,
				body);

		String contents = w.toString();
		// Clean up (delete wrapper from factory) after it is printed. The DefaultJavaPrettyPrinter needs w in model to be able to print it correctly
		w.getPackage().removeType(w);

		return contents;
	}

}
