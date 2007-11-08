package spoon.support.builder;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import spoon.processing.Builder;
import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.support.builder.support.CtVirtualFile;

public class SnippetCompiler {

	@SuppressWarnings("unchecked")
	static public <T> T compileStatement(CtCodeSnippetStatement st,
			Class<T> expectedType) throws CtSnippetCompilationError {
		CtStatement s = compileStatement(st);
		if (expectedType.isAssignableFrom(s.getClass())) {
			return (T) s;
		}
		throw new CtSnippetCompilationError("Incorrect Type for snippet "
				+ st.toString());
	}

	static public void compileAndReplaceSnippetsIn(CtSimpleType<?> c) {
		Factory f = c.getFactory();
		CtSimpleType<?> workCopy = c;
		Set<ModifierKind> backup = new TreeSet<ModifierKind>(workCopy
				.getModifiers());

		workCopy.getModifiers().remove(ModifierKind.PUBLIC);

		try {
			build(f, workCopy.toString());
		} finally {
			// restore modifiers
			c.setModifiers(backup);
		}

	}

	static public CtStatement compileStatement(CtCodeSnippetStatement st)
			throws CtSnippetCompilationError {

		return internalCompileStatement(st);
	}

	private static CtStatement internalCompileStatement(CtStatement st) {
		Factory f = st.getFactory();
		CtClass<?> w = createWrapper(st, f);

		compile(f, w);

		CtSimpleType<?> c = f.Type().get("Wrapper");

		// Get the part we want

		CtMethod<?> wrapper = Query.getElements(c, new Filter<CtMethod<?>>() {

			@SuppressWarnings("unchecked")
			public Class<CtMethod<?>> getType() {
				try {
					return (Class<CtMethod<?>>) Class.forName(CtMethod.class
							.getName());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

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

		body.getStatements().add(st);

		Set<ModifierKind> x = new TreeSet<ModifierKind>();

		f.Method().create(w, x, f.Type().createReference(void.class), "wrap",
				new ArrayList<CtParameter<?>>(),
				new TreeSet<CtTypeReference<? extends Throwable>>(), body);

		return w;
	}

	private static void compile(Factory f, CtType<?> w)
			throws CtSnippetCompilationError {

		String contents = w.toString();

		build(f, contents);

	}

	private static void build(Factory f, String contents, String name) {
		// Build contents
		boolean success;
		Builder builder = new SnippetBuilder(f);
		try {
			builder.addInputSource(new CtVirtualFile(contents, name));
			success = builder.build();
		} catch (Exception e) {
			success = debugCompilationError(f, e);
		}

		if (!success) {
			throw new CtSnippetCompilationError(builder.getProblems());
		}
	}

	private static void build(Factory f, String contents)
			throws CtSnippetCompilationError {
		build(f, contents, "");
	}

	private static boolean debugCompilationError(Factory f, Exception e) {
		boolean success;
		f.getEnvironment().debugMessage("BORKED SnippetCompilation");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(byteArrayOutputStream);
		e.printStackTrace(pw);
		String s = new String(byteArrayOutputStream.toByteArray());
		f.getEnvironment().debugMessage(s);
		success = false;
		return success;
	}

	@SuppressWarnings("unchecked")
	static public <T> CtExpression<T> compileExpression(
			CtCodeSnippetExpression<T> expr) throws CtSnippetCompilationError {
		// create wrapping template

		Factory f = expr.getFactory();
		CtClass<?> w = createWrapper(expr, f);

		String contents = w.toString();

		build(f, contents);

		CtSimpleType c = f.Type().get("Wrapper");

		// Get the part we want

		CtMethod wrapper = Query.getElements(c, new Filter<CtMethod>() {

			public Class<CtMethod> getType() {
				return CtMethod.class;
			}

			public boolean matches(CtMethod element) {
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
		body.getStatements().add(ret);

		Set<ModifierKind> x = new TreeSet<ModifierKind>();

		f.Method().create(w, x, f.Type().createReference(Object.class), "wrap",
				new ArrayList<CtParameter<?>>(),
				new TreeSet<CtTypeReference<? extends Throwable>>(), body);

		return w;
	}

}