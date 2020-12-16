// this import will be unresolved as the package does not exist
import non.existing.pkg.SomeClass;

public class UnresolvedImport {
    public static void main(String[] args) {
        SomeClass instance = new SomeClass();
    }
}