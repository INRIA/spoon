package spoon.reflect.factory;

import spoon.compiler.Environment;

/**
 * Provides the sub-factories required by Spoon.
 * 
 * Most classes provides a method getFactory() that returns the current factory.
 * 
 * Otherwise {@link spoon.reflect.factory.FactoryImpl} is a default implementation.
 */
public interface Factory {

	/**
	 * The core factory.
	 *
	 * @return a core factory
	 */
	CoreFactory Core(); // used 238 times

	/**
	 * The {@link spoon.reflect.declaration.CtType} sub-factory.
	 *
	 * @return a type factory
	 */
	TypeFactory Type(); // used 107 times

	/**
	 * Gets the Spoon environment that encloses this factory.
	 *
	 * @return a environment
	 */
	Environment getEnvironment(); // used 71 times

	/**
	 * The {@link spoon.reflect.declaration.CtPackage} sub-factory.
	 *
	 * @return a package factory
	 */
	PackageFactory Package(); // used 30 times

	/**
	 * The {@link spoon.reflect.code.CtCodeElement} sub-factory.
	 *
	 * @return a code factory
	 */
	CodeFactory Code(); // used 28 times

	/**
	 * The {@link spoon.reflect.declaration.CtClass} sub-factory.
	 *
	 * @return a class factory
	 */
	ClassFactory Class(); // used 27 times

	/**
	 * The {@link spoon.reflect.declaration.CtField} sub-factory.
	 *
	 * @return a field factory
	 */
	FieldFactory Field(); // used 9 times

	/**
	 * The {@link spoon.reflect.declaration.CtExecutable} sub-factory.
	 *
	 * @return a executable factory
	 */
	ExecutableFactory Executable(); // used 8 times

	/**
	 * The {@link spoon.reflect.cu.CompilationUnit} sub-factory.
	 *
	 * @return a compilation unit factory
	 */
	CompilationUnitFactory CompilationUnit(); // used 7 times

	/**
	 * The {@link spoon.reflect.declaration.CtMethod} sub-factory.
	 *
	 * @return a method factory
	 */
	MethodFactory Method(); // used 5 times

	/**
	 * The {@link spoon.reflect.declaration.CtAnnotationType} sub-factory.
	 *
	 * @return an annotation factory
	 */
	AnnotationFactory Annotation(); // used 4 times

	/**
	 * The evaluators sub-factory.
	 *
	 * @return an eval factory
	 */
	EvalFactory Eval(); // used 4 times

	/**
	 * The {@link spoon.reflect.declaration.CtConstructor} sub-factory.
	 *
	 * @return a constractor factory
	 */
	ConstructorFactory Constructor(); // used 3 times
}
