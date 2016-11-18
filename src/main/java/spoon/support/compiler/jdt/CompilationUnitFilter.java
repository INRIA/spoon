package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.compiler.builder.JDTBuilder;

/**
 * This interface is used by instances of {@link spoon.SpoonModelBuilder} to
 * filter particular {@link CompilationUnitDeclaration}s from the
 * {@link spoon.reflect.CtModel} created with
 * {@link spoon.SpoonModelBuilder#build(JDTBuilder)}.
 */
public interface CompilationUnitFilter {

	/**
	 * Tests if {@code cud} should be excluded from the
     * {@link spoon.reflect.CtModel} create by
     * {@link spoon.SpoonModelBuilder#build(JDTBuilder)}.
	 *
	 * @param cud
	 *      The {@link CompilationUnitDeclaration} that may or may not be
	 *      excluded.
	 * @param path
	 *      {@code cud}'s file path (see
	 *      {@link CompilationUnitDeclaration#getFileName()})
	 * @return {@code true} if and only if {@code cud} should be excluded,
	 *         {@code false} otherwise.
	 */
	boolean exclude(final CompilationUnitDeclaration cud, final String path);
}
