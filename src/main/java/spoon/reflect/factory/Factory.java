package spoon.reflect.factory;

import spoon.compiler.Environment;

/**
 * Provides the sub-factories required by Spoon.
 * 
 * Most classes provides a method getFactory() that returns the current factory.
 * 
 * Otherwise FactoryImpl is a default implementation.
 */
public interface Factory {

	CoreFactory Core(); // used 238 times

	TypeFactory Type(); // used 107 times
	
	Environment getEnvironment(); // used 71 times

	PackageFactory Package(); // used 30 times
	
	CodeFactory Code(); // used 28 times
	
	ClassFactory Class(); // used 27 times

	FieldFactory Field(); // used 9 times
	
	ExecutableFactory Executable(); // used 8 times

	CompilationUnitFactory CompilationUnit(); // used 7 times
	
	MethodFactory Method(); // used 5 times
	
	AnnotationFactory Annotation(); // used 4 times

	EvalFactory Eval(); // used 4 times

	ConstructorFactory Constructor(); // used 3 times
}
