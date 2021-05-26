import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

/**
 * This class contains a nested type, is duplicated across the two modules in this project, and
 * starts with an A, which places it at the top of the compilation units as these are sorted
 * lexicographically for each module by JDT. At the time of writing this test file, this causes
 * the nested type's type binding to be checked in
 * {@link spoon.support.compiler.jdt.JDTTreeBuilderQuery#searchTypeBinding(String, CompilationUnitDeclaration[])},
 * which if done without a null check causes a crash due to the type duplication making the binding
 * null in one case.
 */
public class AlsoWithNestedEnum {
    public enum SomeEnum {

    }
}