package spoon.support.compiler.jdt;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import spoon.support.StandardEnvironment;

import java.util.Comparator;
import java.util.Random;

public class CompilationUnitComparator implements Comparator<CompilationUnitDeclaration> {
    private int seed;

    public CompilationUnitComparator(int seed) {
        this.seed = seed;
    }


    @Override
    public int compare(CompilationUnitDeclaration o1, CompilationUnitDeclaration o2) {
        if (this.seed == StandardEnvironment.DEFAULT_SEED_CU_COMPARATOR) {
            String s1 = new String(o1.getFileName());
            String s2 = new String(o2.getFileName());
            return s1.compareTo(s2);
        } else {
            Random random = new Random(seed);
            int r = random.nextInt(3); // can be 0, 1 or 2
            return r-1; // can be -1, 0 or 1
        }
    }
}
